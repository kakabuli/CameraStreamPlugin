package com.kakabuli.voice.caePk.util;

public class AudioFormatUtil {
    //6mic [8ch,16bit] ->[8ch ,32bit]
    public static byte[] addCnForMutiMic(byte[] data) {
        int datasize = data.length;
        byte[] newdata = new byte[datasize * 2];// 乘以2是数据从16bit变为32bit；
        int j = 0;
        int k = 0;
        int index = 0;
        int step = datasize / 2;

        while (j < step) {// 除以2是两个字节作为一组数据，进行添加通道号处理；
            for (int i = 1; i < 9; i++) {
                k = 4 * j;
                index = 2 * j;
                newdata[k] = 00;
                newdata[k + 1] = (byte) i;
                newdata[k + 2] = data[index];
                newdata[k + 3] = data[index + 1];
                j++;
            }

        }
        data = null;
        return newdata;
    }

    //1mic通道适配,输入1通道16bits数据，适配成2通道数据 32bits
    public static byte[] adapeter1Mic(byte[] data) {
        int size = (data.length) * 4;
        byte[] cpy = new byte[size];
        int j = 0;

        while (j < data.length / 2) {
            cpy[8 * j + 0] = 0x00;
            cpy[8 * j + 1] = 0x00;
            cpy[8 * j + 2] = data[2 * j + 0];
            cpy[8 * j + 3] = data[2 * j + 1];

            cpy[8 * j + 4] = 0x00;
            cpy[8 * j + 5] = 0x01;
            cpy[8 * j + 6] = 0x00;
            cpy[8 * j + 7] = 0x00;

            j++;
        }
        return cpy;
    }

    //1mic通道适配,输入2通道 , 16bits数据，适配成2通道数据 32bits
    public static byte[] adapeter1Mic_2ch(byte[] data) {
        int size = (data.length) * 2;
        byte[] cpy = new byte[size];
        int j = 0;

        while (j < data.length / 4) {
            cpy[8 * j + 0] = 0x00;
            cpy[8 * j + 1] = 0x00;
            cpy[8 * j + 2] = data[4 * j + 0];
            cpy[8 * j + 3] = data[4 * j + 1];

            cpy[8 * j + 4] = 0x00;
            cpy[8 * j + 5] = 0x01;
            cpy[8 * j + 6] =  data[4 * j + 2];
            cpy[8 * j + 7] =  data[4 * j + 3];

            j++;
        }
        return cpy;
    }

    public static byte[] fetchChannel(byte[] data) {
        byte[] cpy = new byte[data.length / 2];
        int j = 0;
        while (j < data.length / 4) {
            cpy[2 * j] = data[4 * j + 0];
            cpy[2 * j + 1] = data[4 * j + 1];
            j++;
        }
        return cpy;
    }

    public static byte[] fetchSingleChannel(byte[] data) {
        byte[] cpy = new byte[data.length / 8];
        int j = 0;
        while (j < data.length / 16) {
            cpy[2 * j] = data[16 * j + 0];
            cpy[2 * j + 1] = data[16 * j + 1];
            j++;
        }
        return cpy;
    }

    public static byte[] channelAdapter(byte[] data) {
        // int size = (data.length/8)*4*2;
        byte[] cpy = new byte[data.length * 4];
        int j = 0;

        while (j < data.length / 4) {

            cpy[16 * j] = 00;
            cpy[16 * j + 1] = (byte) 1;
            cpy[16 * j + 2] = data[4 * j + 0];
            cpy[16 * j + 3] = data[4 * j + 1];

            cpy[16 * j + 4] = 00;
            cpy[16 * j + 5] = (byte) 2;
            cpy[16 * j + 6] = data[4 * j + 2];
            cpy[16 * j + 7] = data[4 * j + 3];

            cpy[16 * j + 8] = 00;
            cpy[16 * j + 9] = (byte) 3;
            cpy[16 * j + 10] = 0;
            cpy[16 * j + 11] = 0;

            cpy[16 * j + 12] = 00;
            cpy[16 * j + 13] = (byte) 4;
            cpy[16 * j + 14] = 0;
            cpy[16 * j + 15] = 0;

            j++;

        }
        return cpy;

    }

    //4mic通道适配,[8ch,16bit] -> [8ch ,16bit]
    public static byte[] adapter4Mic(byte[] data) {
        //  int size = ((data.length/8)*2)*6;
        int size = (data.length / 8) * 6;
        byte[] cpy = new byte[size];
        int j = 0;

        while (j < data.length / 16) {

            cpy[12 * j + 0] = data[16 * j + 0];
            cpy[12 * j + 1] = data[16 * j + 1];

            cpy[12 * j + 2] = data[16 * j + 2];
            cpy[12 * j + 3] = data[16 * j + 3];

            cpy[12 * j + 4] = data[16 * j + 4];
            cpy[12 * j + 5] = data[16 * j + 5];

            cpy[12 * j + 6] = data[16 * j + 6];
            cpy[12 * j + 7] = data[16 * j + 7];

            //通道7--》ref1
            cpy[12 * j + 8] = data[16 * j + 12];
            cpy[12 * j + 9] = data[16 * j + 13];

            //通道8 --》 ref2
            cpy[12 * j + 10] = data[16 * j + 14];
            cpy[12 * j + 11] = data[16 * j + 15];

            j++;
        }
        return cpy;
    }

    //4mic通道适配,[8ch,16bit] ->[6ch ,32bit]
    public static byte[] adapter4Mic32bit(byte[] data) {
        //  int size = ((data.length/8)*2)*6;
        int size = (data.length / 8) * 6 * 2;
        byte[] cpy = new byte[size];
        int j = 0;
        while (j < data.length / 16) {
            cpy[24 * j + 0] = 0x00;
            cpy[24 * j + 1] = 0x01;
            cpy[24 * j + 2] = data[16 * j + 0];
            cpy[24 * j + 3] = data[16 * j + 1];

            cpy[24 * j + 4] = 0x00;
            cpy[24 * j + 5] = 0x02;
            cpy[24 * j + 6] = data[16 * j + 2];
            cpy[24 * j + 7] = data[16 * j + 3];

            cpy[24 * j + 8] = 0x00;
            cpy[24 * j + 9] = 0x03;
            cpy[24 * j + 10] = data[16 * j + 4];
            cpy[24 * j + 11] = data[16 * j + 5];

            cpy[24 * j + 12] = 0x00;
            cpy[24 * j + 13] = 0x04;
            cpy[24 * j + 14] = data[16 * j + 6];
            cpy[24 * j + 15] = data[16 * j + 7];

            //通道7--》ref1
            cpy[24 * j + 16] = 0x00;
            cpy[24 * j + 17] = 0x05;
            cpy[24 * j + 18] = data[16 * j + 12];
            cpy[24 * j + 19] = data[16 * j + 13];

            //通道8 --》 ref2
            cpy[24 * j + 20] = 0x00;
            cpy[24 * j + 21] = 0x06;
            cpy[24 * j + 22] = data[16 * j + 14];
            cpy[24 * j + 23] = data[16 * j + 15];

            j++;
        }
        return cpy;
    }


    //4mic通道适配,[6ch,16bit] ->[6ch ,32bit]
    public static byte[] adapter4Mic6Ch32bit(byte[] data) {
        //  int size = ((data.length/8)*2)*6;
        int size = data.length * 2;
        byte[] cpy = new byte[size];
        int j = 0;
        while (j < data.length / 12) {
            //通道1
            cpy[24 * j + 0] = 0x00;
            cpy[24 * j + 1] = 0x01;
            cpy[24 * j + 2] = data[12 * j + 4];
            cpy[24 * j + 3] = data[12 * j + 5];

            cpy[24 * j + 4] = 0x00;
            cpy[24 * j + 5] = 0x02;
            cpy[24 * j + 6] = data[12 * j + 6];
            cpy[24 * j + 7] = data[12 * j + 7];

            cpy[24 * j + 8] = 0x00;
            cpy[24 * j + 9] = 0x03;
            cpy[24 * j + 10] = data[12 * j + 8];
            cpy[24 * j + 11] = data[12 * j + 9];

            cpy[24 * j + 12] = 0x00;
            cpy[24 * j + 13] = 0x04;
            cpy[24 * j + 14] = data[12 * j + 10];
            cpy[24 * j + 15] = data[12 * j + 11];

            //通道5--》ref1
            cpy[24 * j + 16] = 0x00;
            cpy[24 * j + 17] = 0x05;
            cpy[24 * j + 18] = data[12 * j + 0];
            cpy[24 * j + 19] = data[12 * j + 1];

            //通道6 --》 ref2
            cpy[24 * j + 20] = 0x00;
            cpy[24 * j + 21] = 0x06;
            cpy[24 * j + 22] = data[12 * j + 2];
            cpy[24 * j + 23] = data[12 * j + 3];

            j++;
        }
        return cpy;
    }


    public static int detechChNum(byte ch1, byte ch2, byte ch3, byte ch4, byte ch5, byte ch6, byte ch7, byte ch8) {
        boolean isOk = false;
        int zeroIndex = -1;
        if (((char) ch1 & 0x0F) == 0 &&
                ((char) ch2 & 0x0F) == 1 &&
                ((char) ch3 & 0x0F) == 2 &&
                ((char) ch4 & 0x0F) == 3 && ((char) ch5 & 0x0F) == 4
        ) {
            isOk = true;
            zeroIndex = 0;
        } else {//不是正常顺序，获取当前第一个通道的位置
            if (((char) ch2 & 0x0F) == 0 && ((char) ch3 & 0x0F) == 1 && ((char) ch4 & 0x0F) == 2) {
                zeroIndex = 1;
            } else if (((char) ch3 & 0x0F) == 0 && ((char) ch4 & 0x0F) == 1 && ((char) ch5 & 0x0F) == 2) {
                zeroIndex = 2;
            } else if (((char) ch4 & 0x0F) == 0 && ((char) ch5 & 0x0F) == 1 && ((char) ch6 & 0x0F) == 2) {
                zeroIndex = 3;
            } else if (((char) ch5 & 0x0F) == 0 && ((char) ch6 & 0x0F) == 1 && ((char) ch7 & 0x0F) == 2) {
                zeroIndex = 4;
            } else if (((char) ch6 & 0x0F) == 0 && ((char) ch7 & 0x0F) == 1 && ((char) ch8 & 0x0F) == 2) {
                zeroIndex = 5;
            } else if (((char) ch7 & 0x0F) == 0 && ((char) ch8 & 0x0F) == 1) {
                zeroIndex = 6;
            } else if (((char) ch8 & 0x0F) == 0 && ((char) ch7 & 0x0F) == 7) {
                zeroIndex = 7;
            }
        }
        return zeroIndex;
    }

    //4mic通道适配, [8ch, 32bit] -> [6ch,32bit]
    public static byte[] ChTo4Mic32bit(byte[] data) {
        //  int size = ((data.length/8)*2)*6;
        int size = (data.length / 8) * 6;

        byte[] cpy = new byte[size];
        int j = 0;
        boolean isCheckChNum = false;
        int startIndex = -1;
        while (j < data.length / 32) {
            if (!isCheckChNum) {
                startIndex = detechChNum(data[32 * j + 1], data[32 * j + 5], data[32 * j + 9], data[32 * j + 13], data[32 * j + 17]
                        , data[32 * j + 21], data[32 * j + 25], data[32 * j + 29]);
                if (startIndex < 0) {
                    isCheckChNum = false;
                } else {
                    isCheckChNum = true;
                }
            }
            if (isCheckChNum && j != data.length / 32 - 1) {

                cpy[24 * j + 0] = data[32 * j + startIndex * 4 + 0];
                cpy[24 * j + 1] = data[32 * j + startIndex * 4 + 1];
                cpy[24 * j + 2] = data[32 * j + startIndex * 4 + 2];
                cpy[24 * j + 3] = data[32 * j + startIndex * 4 + 3];

                cpy[24 * j + 4] = data[32 * j + startIndex * 4 + 4];
                cpy[24 * j + 5] = data[32 * j + startIndex * 4 + 5];
                cpy[24 * j + 6] = data[32 * j + startIndex * 4 + 6];
                cpy[24 * j + 7] = data[32 * j + startIndex * 4 + 7];

                cpy[24 * j + 8] = data[32 * j + startIndex * 4 + 8];
                cpy[24 * j + 9] = data[32 * j + startIndex * 4 + 9];
                cpy[24 * j + 10] = data[32 * j + startIndex * 4 + 10];
                cpy[24 * j + 11] = data[32 * j + startIndex * 4 + 11];

                cpy[24 * j + 12] = data[32 * j + startIndex * 4 + 12];
                cpy[24 * j + 13] = data[32 * j + startIndex * 4 + 13];
                cpy[24 * j + 14] = data[32 * j + startIndex * 4 + 14];
                cpy[24 * j + 15] = data[32 * j + startIndex * 4 + 15];

                //通道7--》ref1
                cpy[24 * j + 16] = data[32 * j + startIndex * 4 + 16];
                cpy[24 * j + 17] = data[32 * j + startIndex * 4 + 17];
                cpy[24 * j + 18] = data[32 * j + startIndex * 4 + 18];
                cpy[24 * j + 19] = data[32 * j + startIndex * 4 + 19];

                //通道8 --》 ref2
                cpy[24 * j + 20] = data[32 * j + startIndex * 4 + 20];
                cpy[24 * j + 21] = data[32 * j + startIndex * 4 + 21];
                cpy[24 * j + 22] = data[32 * j + startIndex * 4 + 22];
                cpy[24 * j + 23] = data[32 * j + startIndex * 4 + 23];
            }

            j++;
        }
        return cpy;
    }

    //2mic通道适配  [4ch,16bit] ->[4ch,32bit]
    public static byte[] addCnFor2Mic(byte[] data) {
        byte[] cpy = new byte[data.length * 2];
        int j = 0;

        //通道： mic1 mic2 ref ref
        while (j < data.length / 8) {
            cpy[16 * j] = 00;
            cpy[16 * j + 1] = (byte) 1;
            cpy[16 * j + 2] = data[8 * j + 0];
            cpy[16 * j + 3] = data[8 * j + 1];

            cpy[16 * j + 4] = 00;
            cpy[16 * j + 5] = (byte) 2;
            cpy[16 * j + 6] = data[8 * j + 2];
            cpy[16 * j + 7] = data[8 * j + 3];

            cpy[16 * j + 8] = 00;
            cpy[16 * j + 9] = (byte) 3;
            cpy[16 * j + 10] = data[8 * j + 4];
            cpy[16 * j + 11] = data[8 * j + 5];

            cpy[16 * j + 12] = 00;
            cpy[16 * j + 13] = (byte) 4;
            cpy[16 * j + 14] = data[8 * j + 6];
            cpy[16 * j + 15] = data[8 * j + 7];

            j++;
        }
        return cpy;
    }


    //2mic通道适配  [6ch,16bit] ->[4ch,32bit]
    public static byte[] add6ChFor2Mic(byte[] data) {
        byte[] cpy = new byte[data.length * 4 / 3];
        int j = 0;

        //通道： mic1 mic2 ref ref
        while (j < data.length / 12) {
            cpy[16 * j] = 00;
            cpy[16 * j + 1] = (byte) 1;
            cpy[16 * j + 2] = data[12 * j + 4];
            cpy[16 * j + 3] = data[12 * j + 5];

            cpy[16 * j + 4] = 00;
            cpy[16 * j + 5] = (byte) 2;
            cpy[16 * j + 6] = data[12 * j + 6];
            cpy[16 * j + 7] = data[12 * j + 7];

            cpy[16 * j + 8] = 00;
            cpy[16 * j + 9] = (byte) 3;
            cpy[16 * j + 10] = data[12 * j + 0];
            cpy[16 * j + 11] = data[12 * j + 1];

            cpy[16 * j + 12] = 00;
            cpy[16 * j + 13] = (byte) 4;
            cpy[16 * j + 14] = data[12 * j + 2];
            cpy[16 * j + 15] = data[12 * j + 3];

            j++;
        }
        return cpy;
    }


    //2mic通道适配  [8ch,16bit] ->[4ch,32bit]
    public static byte[] add6MicFor2Mic(byte[] data) {
        byte[] cpy = new byte[data.length];
        int j = 0;
        //通道： mic1 mic2 ref ref
        while (j < data.length / 16) {
            cpy[16 * j] = 00;
            cpy[16 * j + 1] = (byte) 1;
            cpy[16 * j + 2] = data[16 * j + 0];
            cpy[16 * j + 3] = data[16 * j + 1];

            cpy[16 * j + 4] = 00;
            cpy[16 * j + 5] = (byte) 2;
            cpy[16 * j + 6] = data[16 * j + 8];
            cpy[16 * j + 7] = data[16 * j + 9];

            cpy[16 * j + 8] = 00;
            cpy[16 * j + 9] = (byte) 3;
            cpy[16 * j + 10] = data[16 * j + 12];
            cpy[16 * j + 11] = data[16 * j + 13];

            cpy[16 * j + 12] = 00;
            cpy[16 * j + 13] = (byte) 4;
            cpy[16 * j + 14] = data[16 * j + 14];
            cpy[16 * j + 15] = data[16 * j + 15];

            j++;
        }
        return cpy;
    }

    /**
     * CAE降噪后的音频为小端对齐格式，低字节在低位，增益调整 aplication 层
     *
     * @param buffer
     * @return
     */
    public static byte[] audioDataHandle(byte[] buffer) {
        if (null == buffer) {
            return null;
        }

        int cnt = 0;
        short temp = 0;
        byte[] bufferT = new byte[buffer.length];
        // Log.e("splitShortBuf", "buffer.length:"+buffer.length);
        while (cnt < buffer.length) {
            temp = (short) (buffer[cnt + 1]);//获取short 高位
            temp = (short) ((temp << 8) | (buffer[cnt] & 0xff));//获取低位
            // Log.e("", "B1:  "+buffer[cnt]+" B2: "+buffer[cnt+1]+"  temp:"+temp);
            temp = GainUtil.GainUp(temp, 1.5f);//做音量调节处理
            if (temp > 32767) {
                temp = 32767;
            } else if (temp < -32768) {
                temp = -32768;
            }
            //拆分为byte后再写到文件中
            bufferT[cnt + 1] = (byte) ((temp >> 8) & 0xff); //高位
            bufferT[cnt] = (byte) (temp & 0xff);//低位
            //Log.e("", "out------> B1:  "+buffer[cnt]+" B2: "+buffer[cnt+1]+"  temp:"+temp+"!------------------------!");
            cnt += 2;
        }
        buffer = null;
        System.gc();
        //Log.e("splitShortBuf", "handle end!");
        return bufferT;
    }


}
