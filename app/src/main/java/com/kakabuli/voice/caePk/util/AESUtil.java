package com.kakabuli.voice.caePk.util;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密解密工具类 128位
 *
 * @author zhaopeng
 * 2018/10/28 11:25
 */
public class AESUtil {
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String KEY_AES = "AES";

    /**
     * 加密
     *
     * @param data 需要加密的内容
     * @param key  加密密码
     * @return
     */
    public static String encryptToAES(String data, String key) {
        return doAES(data, key, Cipher.ENCRYPT_MODE);
    }

    /**
     * 解密
     *
     * @param data 待解密内容
     * @param key  解密密钥
     * @return
     */
    public static String decryptByAES(String data, String key) {
        //Log.e("kuwolog","decryptByAES");
        return doAES(data, key, Cipher.DECRYPT_MODE);
    }

    /**
     * 加解密
     *
     * @param data 待处理数据
     * @param key  密钥
     * @param mode 加解密mode
     * @return
     */
    private static String doAES(String data, String key, int mode) {
        //Log.e("kuwolog","doAES");
        try {
            if (key == null || "".equals(key)) {
                return null;
            }
            boolean encrypt = mode == Cipher.ENCRYPT_MODE;
            byte[] content;
            //Log.e("kuwolog","doAES 1");
            //判断是加密还是解密
            if (encrypt) {
                content = data.getBytes(DEFAULT_CHARSET);
            } else {
                if (!isBase64(data)){
                    return data;
                }
                content = Base64Coder.decode(data);
            }
            SecretKeySpec keySpec = new SecretKeySpec(str2ByteArray(key), KEY_AES);

            // 根据指定算法AES创建密码器
            Cipher cipher = Cipher.getInstance(KEY_AES);
            //Log.e("kuwolog","doAES 4 KEY_AES:");
            // 初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(mode, keySpec);
            //Log.e("kuwolog","doAES 5");
            byte[] result = cipher.doFinal(content);
            //Log.e("kuwolog","doAES 6");
            //Log.e("kuwolog","encrypt:"+encrypt);
            if (encrypt) {
                // 加密 对二进制结果base64编码
                return Base64Coder.encode(result);
            } else {
                // 解密 将二进制转为String
                //Log.e("kuwolog","resulet:"+result.toString());
                return new String(result, DEFAULT_CHARSET);
            }
        } catch (Exception e) {
            //Log.e("kuwolog","AES encrypt error,mode:" + e.getMessage());
        }
        return null;
    }
    private static boolean isBase64(String str) {
         String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
         return Pattern.matches(base64Pattern, str);
    }
    public static byte[] str2ByteArray(String s) {
        int byteArrayLength = s.length() / 2;
        byte[] b = new byte[byteArrayLength];
        for (int i = 0; i < byteArrayLength; i++) {
            byte b0 = (byte) Integer.valueOf(s.substring(i * 2, i * 2 + 2), 16)
                    .intValue();
            b[i] = b0;
        }

        return b;
    }
    public static String encryptToMD5(String info) {
        byte[] digesta = null;

        try {
            MessageDigest alga = MessageDigest.getInstance("MD5");
            alga.update(info.getBytes());
            digesta = alga.digest();
        } catch (NoSuchAlgorithmException var3) {
            var3.printStackTrace();
        }

        String rs = byte2hex(digesta);
        return rs;
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";

        for (int n = 0; n < b.length; ++n) {
            stmp = Integer.toHexString(b[n] & 255);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }

        return hs.toUpperCase();
    }

    static class Base64Coder {
        public static final String key = "mobile";
        private static char[] map1 = new char[64];
        private static byte[] map2;

        public static String encodeString(String s) {
            return encode(s.getBytes());
        }

        public static String encode(byte[] in) {
            return new String(encode(in, in.length));
        }

        public static String encodeString(String s, String charset) {
            String result = "";

            try {
                result = new String(encode(s.getBytes(charset), s.getBytes(charset).length));
            } catch (Exception var4) {
                ;
            }

            return result;
        }

        public static String encodeString(String s, String charset, String key) {
            String result = "";

            try {
                result = new String(encode(s.getBytes(charset), s.getBytes(charset).length, key));
            } catch (Exception var5) {
                ;
            }

            return result;
        }

        public static char[] encode(byte[] in, int iLen) {
            return encode(in, iLen, (String) null);
        }

        public static char[] encode(byte[] in, int iLen, String key) {
            int i;
            if (key != null && !"".equals(key)) {
                byte[] keyArr = key.getBytes();
                i = 0;

                while (i < in.length) {
                    for (int j = 0; j < keyArr.length && i < in.length; ++j) {
                        int var10001 = i++;
                        in[var10001] ^= keyArr[j];
                    }
                }
            }

            int oDataLen = (iLen * 4 + 2) / 3;
            i = (iLen + 2) / 3 * 4;
            char[] out = new char[i];
            int ip = 0;

            for (int op = 0; ip < iLen; ++op) {
                int i0 = in[ip++] & 255;
                int i1 = ip < iLen ? in[ip++] & 255 : 0;
                int i2 = ip < iLen ? in[ip++] & 255 : 0;
                int o0 = i0 >>> 2;
                int o1 = (i0 & 3) << 4 | i1 >>> 4;
                int o2 = (i1 & 15) << 2 | i2 >>> 6;
                int o3 = i2 & 63;
                out[op++] = map1[o0];
                out[op++] = map1[o1];
                out[op] = op < oDataLen ? map1[o2] : 61;
                ++op;
                out[op] = op < oDataLen ? map1[o3] : 61;
            }

            return out;
        }

        public static String decodeString(String s, String charset, String key) {
            try {
                byte[] result = decode(s.toCharArray());
                byte[] keyArr = key.getBytes();
                int i = 0;

                while (i < result.length) {
                    for (int j = 0; j < keyArr.length && i < result.length; ++j) {
                        int var10001 = i++;
                        result[var10001] ^= keyArr[j];
                    }
                }

                try {
                    if (charset == null) {
                        return new String(result);
                    }

                    return new String(result, charset);
                } catch (UnsupportedEncodingException var7) {
                    ;
                }
            } catch (OutOfMemoryError var8) {
                ;
            }

            return null;
        }

        public static String decodeString(String s, String charset) throws UnsupportedEncodingException {
            try {
                return new String(decode(s), charset);
            } catch (OutOfMemoryError var3) {
                return null;
            }
        }

        public static byte[] decode(String s) {
            return decode(s.toCharArray());
        }

        public static byte[] decode(char[] in) {
            int iLen = in.length;
            if (iLen % 4 != 0) {
                throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4.");
            } else {
                while (iLen > 0 && in[iLen - 1] == '=') {
                    --iLen;
                }

                int oLen = iLen * 3 / 4;
                byte[] out = new byte[oLen];
                int ip = 0;
                int op = 0;

                while (ip < iLen) {
                    int i0 = in[ip++];
                    int i1 = in[ip++];
                    int i2 = ip < iLen ? in[ip++] : 65;
                    int i3 = ip < iLen ? in[ip++] : 65;
                    if (i0 <= 127 && i1 <= 127 && i2 <= 127 && i3 <= 127) {
                        int b0 = map2[i0];
                        int b1 = map2[i1];
                        int b2 = map2[i2];
                        int b3 = map2[i3];
                        if (b0 >= 0 && b1 >= 0 && b2 >= 0 && b3 >= 0) {
                            int o0 = b0 << 2 | b1 >>> 4;
                            int o1 = (b1 & 15) << 4 | b2 >>> 2;
                            int o2 = (b2 & 3) << 6 | b3;
                            out[op++] = (byte) o0;
                            if (op < oLen) {
                                out[op++] = (byte) o1;
                            }

                            if (op < oLen) {
                                out[op++] = (byte) o2;
                            }
                            continue;
                        }

                        throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
                    }

                    throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
                }

                return out;
            }
        }

        private Base64Coder() {
        }

        static {
            int i = 0;

            char c;
            for (c = 'A'; c <= 'Z'; map1[i++] = c++) {
                ;
            }

            for (c = 'a'; c <= 'z'; map1[i++] = c++) {
                ;
            }

            for (c = '0'; c <= '9'; map1[i++] = c++) {
                ;
            }

            map1[i++] = '+';
            map1[i++] = '/';
            map2 = new byte[128];

            for (i = 0; i < map2.length; ++i) {
                map2[i] = -1;
            }

            for (i = 0; i < 64; ++i) {
                map2[map1[i]] = (byte) i;
            }

        }
    }
}

