package com.kakabuli.camerastream.utils;

import android.content.Context;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 功能性函数扩展类
 */
public class FucUtil {
	/**
	 * 读取asset目录下文件。
	 * @return content
	 */
	public static String readFile(Context mContext, String file, String code)
	{
		int len = 0;
		byte []buf = null;
		String result = "";
		try {
			InputStream in = mContext.getAssets().open(file);
			len  = in.available();
			buf = new byte[len];
			in.read(buf, 0, len);
			
			result = new String(buf,code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean copyAssetFolder(Context context, String srcName, String dstName) {
		try {
			boolean result = true;
			String fileList[] = context.getAssets().list(srcName);
			if (fileList == null) return false;

			if (fileList.length == 0) {
				result = copyAssetFile(context, srcName, dstName);
			} else {
				File file = new File(dstName);
				result = file.mkdirs();
				for (String filename : fileList) {
					result &= copyAssetFolder(context, srcName + File.separator + filename, dstName + File.separator + filename);
				}
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean copyAssetFile(Context context, String srcName, String dstName) {
		try {
			InputStream in = context.getAssets().open(srcName);
			File outFile = new File(dstName);
			if(!outFile.getParentFile().exists()) {
				outFile.getParentFile().mkdirs();
			}
			OutputStream out = new FileOutputStream(outFile);
			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
