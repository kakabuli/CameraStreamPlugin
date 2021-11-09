package com.kakabuli.voice.caePk.utils;

/**
 * @author yinxiaowei
 * @date 2020/7/4
 * @description:
 */

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Android运行linux命令
 */
public final class ShellUtils {
    private static ArrayList<String> cardNameList = new ArrayList<>();

    static {
        cardNameList.add("Bothlent UAC Dongle");
        cardNameList.add("AC108 USB Audio");
        cardNameList.add("NationalChip UAC Dongle");
    }

    private static final String TAG = "ShellUtils";
    private static boolean mHaveRoot = false;

    /**
     * 判断机器Android是否已经root，即是否获取root权限
     */
    public static boolean haveRoot() {
        if (!mHaveRoot) {
            int ret = execRootCmdSilent("echo test"); // 通过执行测试命令来检测
            if (ret != -1) {
                Log.i(TAG, "have root!");
                execRootCmdSilent("chmod 777 /dev/snd/*");
                mHaveRoot = true;
            } else {
                Log.i(TAG, "not root!");
            }
        } else {
            Log.i(TAG, "mHaveRoot = true, have root!");
        }
        return mHaveRoot;
    }

    public static int cardN = -1;

    public static int fetchCards() {
        cardN = -1;
        execShellCmd("cat /proc/asound/cards", line -> {
            for (String cardName : cardNameList) {
                if (line.contains(cardName)) {
                    Log.d(TAG, "Find USB card:" + line);
                    line = line.replace('[', ',');
                    line = line.replace(']', ',');
                    Log.d(TAG, "Find USB card parse:" + line);
                    String[] strs = line.split(",");
                    if (strs.length > 0) {
                        String numStr = strs[0].trim();
                        cardN = Integer.parseInt(numStr);
                        Log.d(TAG, "USB card Number=" + cardN);
                        return true;
                    }
                }
            }
            return false;
        });
        return cardN;
    }

    private static String cardPermission = "";

    public static String fetchCardPermission() {
        String cardDevice = String.format(Locale.getDefault(), "pcmC%dD0c", cardN);
        execShellCmd("ls -l /dev/snd/", data -> {
            Log.e(TAG, "onReadLine: " + data);
            if (data.contains(cardDevice)) {
                String[] s = data.split(" ");
                if (s.length > 0) {
                    cardPermission = s[0];
                    return true;
                }
            }
            return false;
        });
        return cardPermission;
    }


    /**
     * 执行命令并且输出结果
     */
    private static void execShellCmd(String cmd, IShellCallBack iShellCallBack) {
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            Process p = Runtime.getRuntime().exec(cmd);// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());
            Log.i(TAG, cmd);
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                boolean b = iShellCallBack.onReadLine(line);
                if (b) {
                    return;
                }
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 执行命令但不关注结果输出
     */
    private static int execRootCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;
        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());

            Log.i(TAG, cmd);
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    public static final boolean ping() {
        DataOutputStream dos = null;
        String cmd = "ping -c 3 -w 10 114.114.114.114";
        DataInputStream dis = null;
        try {
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            StringBuilder stringBuffer = new StringBuilder();
            String line = null;
            while ((line = dis.readLine()) != null) {
                stringBuffer.append(line);
            }
            // ping的状态
            int status = p.waitFor();
            Log.d("------ping-----", "result content : " + stringBuffer.toString() + "   " + status);
            if (status == 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


}
