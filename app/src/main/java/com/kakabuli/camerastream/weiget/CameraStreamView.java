package com.kakabuli.camerastream.weiget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.opengl.GLES11Ext;
import android.util.Log;
import android.view.Surface;

import com.serenegiant.glutils.ShaderConst;
import com.serenegiant.usb.IFrameCallback;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.widget.UVCCameraTextureView;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.serenegiant.glutils.ShaderConst.GL_TEXTURE_EXTERNAL_OES;

public class CameraStreamView {

    private Context mContext;
    private UVCCamera uvcCamera;

    private CameraCallback previewCallback;
    private int mPreviewWidth = UVCCamera.DEFAULT_PREVIEW_WIDTH;
    private int mPreviewHeight = UVCCamera.DEFAULT_PREVIEW_HEIGHT;
    private ByteBuffer videoBuffer;
    private volatile boolean isEncoding;
    private Thread videoThread;
    private final Object writeLock = new Object();
    private ConcurrentLinkedQueue<ByteBuffer> videoBufferQueue = new ConcurrentLinkedQueue<>();

    public CameraStreamView(Context context){
        this.mContext = context;
    }


    public int[] setPreviewResolution(int width, int height) {
        if (width > 0 && height > 0) {
            mPreviewWidth = width;
            mPreviewHeight = height;
        }
        return new int[]{mPreviewWidth, mPreviewHeight};
    }

    public UVCCamera startCamera(USBMonitor.UsbControlBlock ctrlBlock) {
        if (uvcCamera == null) {
            uvcCamera = openCamera(ctrlBlock);
            if (uvcCamera == null) {
                return null;
            }
        }
        try {
            uvcCamera.setPreviewSize(mPreviewWidth, mPreviewHeight, UVCCamera.FRAME_FORMAT_MJPEG);
        } catch (IllegalArgumentException e) {
            try {
                uvcCamera.setPreviewSize(mPreviewWidth, mPreviewHeight, UVCCamera.FRAME_FORMAT_YUYV);
            } catch (IllegalArgumentException e1) {
                e1.printStackTrace();
                uvcCamera.destroy();
                return null;
            }
        }
        uvcCamera.setAutoFocus(true);
        uvcCamera.setAutoWhiteBlance(true);
//        surface = getSurface();
//        if (surface != null) {
//            uvcCamera.setPreviewDisplay(surface);

        SurfaceTexture surfaceTexture = new SurfaceTexture(ShaderConst.GL_TEXTURE_EXTERNAL_OES);
        uvcCamera.setPreviewTexture(surfaceTexture);
        Log.d("shulan_CameraStreamView","startCamera---");
//        uvcCamera.setFrameCallback(frameCallback, UVCCamera.PIXEL_FORMAT_YUV420SP);
        uvcCamera.setFrameCallback(frameCallback, UVCCamera.PIXEL_FORMAT_YUV);
        uvcCamera.startPreview();
        uvcCamera.updateCameraParams();
        return uvcCamera;
//        }
//        return null;
    }
    public void stopCamera() {
        disableEncoding();
        if (uvcCamera != null) {
            uvcCamera.destroy();
        }
    }

    private UVCCamera openCamera(USBMonitor.UsbControlBlock ctrlBlock) {
        try {
            UVCCamera uvcCamera = new UVCCamera();
            uvcCamera.open(ctrlBlock);
            return uvcCamera;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isOpened(){
        return uvcCamera != null;
    }

    public void startPreview() {
        if (uvcCamera != null) {
            uvcCamera.startPreview();
        }
    }

    public void stopPreview() {
        if (uvcCamera != null) {
            uvcCamera.stopPreview();
        }
    }

    public void enableEncoding() {
        videoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    while (!videoBufferQueue.isEmpty()) {
                        if (videoBuffer != null && previewCallback != null) {
                            videoBuffer.clear();
                            videoBuffer.put(videoBufferQueue.poll());
                            videoBuffer.flip();
                            previewCallback.onGetPreviewFrame(videoBuffer.array(), mPreviewWidth, mPreviewHeight);
                        }
                    }
                    // Waiting for next frame
                    synchronized (writeLock) {
                        try {
                            // isEmpty() may take some time, so we set timeout to detect next frame
                            writeLock.wait(500);
                        } catch (InterruptedException ie) {
                            videoThread.interrupt();
                        }
                    }
                }
            }
        });
        videoThread.start();
        isEncoding = true;
    }

    public void disableEncoding() {
        isEncoding = false;
        videoBufferQueue.clear();

        if (videoThread != null) {
            videoThread.interrupt();
            try {
                videoThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                videoThread.interrupt();
            }
        }
    }

    private IFrameCallback frameCallback = new IFrameCallback() {
        @Override
        public void onFrame(ByteBuffer frame) {
            int len = frame.capacity();
            final byte[] yuv = new byte[len];
            frame.get(yuv);
            Log.i("shulan_CameraStreamView", "-摄像头返回数据------" + yuv.length);
            if (isEncoding) {
                if (videoBuffer == null) {
                    videoBuffer = ByteBuffer.allocate(frame.limit());
                }
                videoBufferQueue.add(frame);
                synchronized (writeLock) {
                    writeLock.notifyAll();
                }
            }
        }
    };

    public void setPreviewCallback(CameraCallback previewCallback) {
        this.previewCallback = previewCallback;
    }

    public boolean isEqual(UsbDevice device) {
        return (uvcCamera != null) && (uvcCamera.getDevice() != null) && uvcCamera.getDevice().equals(device);
    }

    public interface CameraCallback {
        void onGetPreviewFrame(byte[] data, int width, int height);
    }
}
