package com.kakabuli.camerastream.utils;

import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David
 */
public class StringUtil {

    /**
     * 是否全为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }


    public static boolean isPhone(String phone) {
        Pattern pattern = Pattern.compile("^0?(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57])[0-9]{8}$");
        Matcher isPhone = pattern.matcher(phone);
        if (!isPhone.matches()) {
            return false;
        }
        return true;
    }


    /**
     * 是否全为字母
     *
     * @param str
     * @return
     */
    public static boolean isChar(String str) {
        Pattern pattern = Pattern.compile("[a-zA-Z]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }


    /**
     * 是否全为特殊字符
     *
     * @param string
     * @return
     */
    public static boolean isConSpeCharacters(String string) {
        if (string.replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\s*", "").length() == string.length()) {
            return true;
        }
        return false;
    }


    //string 为要判断的字符串
    public static boolean isContainSpeCharacters(String string) {
        if (string.replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\s*", "").length() == 0) {
            //不包含特殊字符
            return false;
        }
        return true;
    }


    public static void asciiToString(int num) {
        System.out.println(num + " -> " + (char) num);
    }
	
	
	/*public static void stringToAscii(String str) {
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			LogUtils.d("davi "+chars[i] + " -> " + (int) chars[i]);
			System.out.println(chars[i] + " -> " + (int) chars[i]);
		}
	}*/


    public static char convertIntToAscii(int a) {
        return (a >= 0 && a <= 255) ? (char) a : '\0';
    }


    public static int groupPsw(char s) {
        int temp = Integer.valueOf(s);
        int temp2 = 0;
        Log.e("StringUtil","groupPsw:" + temp);
        switch (temp) {
            case 0:
                temp2 = 30;
                break;
            case 1:
                temp2 = 31;
                break;
            case 2:
                temp2 = 32;
                break;
            case 3:
                temp2 = 33;
                break;
            case 4:
                temp2 = 34;
                break;
            case 5:
                temp2 = 35;
                break;
            case 6:
                temp2 = 36;
                break;
            case 7:
                temp2 = 37;
                break;
            case 8:
                temp2 = 38;
                break;
            case 9:
                temp2 = 39;
                break;
        }
        return temp2;
    }

    /**
     * 字符串转换为Ascii
     *
     * @param value
     * @return
     */
    public static String stringToAscii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sbu.append((int) chars[i]).append(",");
            } else {
                sbu.append((int) chars[i]);
            }
        }
        return sbu.toString();
    }

    //随机密码6-12位生成
    public static String makeRandomPassword() {
        Random random = new Random();
        //位数
        int randNum = random.nextInt(6) + 6;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < randNum; i++) {
            int number = random.nextInt(9);
            sb.append(number);
        }
        return sb.toString();
    }

    /**
     * 包含大小写字母及数字且在6-12位
     * 是否符合条件
     *
     * @param str
     * @return
     */
    public static boolean passwordJudge(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        boolean isLetter = false;//定义一个boolean值，用来表示是否包含字母
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) { //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            } else if (Character.isLetter(str.charAt(i))) { //用char包装类中的判断字母的方法判断每一个字符
                isLetter = true;
            }
        }
        String regex = "^[a-zA-Z0-9]{6,12}$";
        boolean isRight = isDigit && isLetter && str.matches(regex);
        return isRight;
    }

    /**
     * 包含数字且在6-12位
     * 是否符合条件
     *
     * @param str
     * @return
     */
    public static boolean randomJudge(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) { //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            }
        }
        String regex = "^[0-9]{6,12}$";
        boolean isRight = isDigit && str.matches(regex);
        return isRight;
    }

    /**
     * 判断名称为不为null,不为空,长度小于16位
     * true 符合约束条件
     */
    public static boolean nicknameJudge(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        int length = 0;
        try {
            length = str.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (length > 50) {
            return false;
        }
        return true;
    }

    /**
     * 检查密码是否简单
     * true 密码简单
     */
    public static boolean checkSimplePassword(String password) {
        char[] chars = password.toCharArray();
        boolean isSimple = true;
        for (int i = 0; i < chars.length - 1; i++) {  //是否全部相同
            if (chars[i] != chars[i + 1]) {
                isSimple = false;
                break;
            }
        }
        if (isSimple) {
            return isSimple;
        }
        isSimple = true;
        for (int i = 0; i < chars.length - 1; i++) {  //是否全部递增
            if (chars[i] != chars[i + 1] + 1) {
                isSimple = false;
                break;
            }
        }
        if (isSimple) {
            return isSimple;
        }
        isSimple = true;
        for (int i = 0; i < chars.length - 1; i++) {  //是否全部递减
            if (chars[i] != chars[i + 1] - 1) {
                isSimple = false;
                break;
            }
        }
        if (isSimple) {
            return isSimple;
        }
        return false;
    }

    /**
     * 判断特殊字符
     */
    public static boolean judgeSpecialCharacter(String data) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(data);
        if (m.find()) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 判断修改的昵称是否和之前的相同
     */
    public static boolean judgeNicknameWhetherSame(String originalNickname, String currentNickanme) {
        if (originalNickname != null) {
            if (currentNickanme.equals(originalNickname)) {
                return true;
            }
        }
        return false;
    }

    public static String getEdittextContent(EditText et) {
        return et.getText().toString().trim();
    }

    //每两个字符中间加空格
    public static String getFileAddSpace(String replace) {
        String regex = "(.{1})";
        replace = replace.replaceAll(regex, "$1 ");
        return replace;
    }

    //是否是局域网ip
    public static boolean isInner(String ip) {
        String reg = "(127[.]0[.]0[.]1)|"
                + "(localhost)|"
                + "(10[.]\\d{1,3}[.]\\d{1,3}[.]\\d{1,3})|"
                + "(172[.]((1[6-9])|(2\\d)|(3[01]))[.]\\d{1,3}[.]\\d{1,3})|"
                + "(192[.]168[.]\\d{1,3}[.]\\d{1,3})";
        Pattern p = Pattern.compile(reg);
        Matcher matcher = p.matcher(ip);
        return matcher.find();
    }

    /**
     * 大于5位则保留5位
     * 小于5位则直接显示
     */
    public static String getSubstringFive(String str) {
        String data = "";
        if (TextUtils.isEmpty(str)) {
            return data;
        }
        if (str.length() > 5) {
            data = str.substring(0, 5);
        } else {
            data = str;
        }
        return data;
    }


    public boolean isKaadasWifiQrCode(String result){
        if (TextUtils.isEmpty(result)){
            return false;
        }

        String rule = "kaadas_/^\\w+$/_WiFi_";
        return result.matches(rule);
    }

    public static String getFileNameNoEx(String filename) {

        if ((filename != null) && (filename.length() > 0)) {

            int dot = filename.lastIndexOf('.');

            if ((dot >-1) && (dot < (filename.length()))) {

                return filename.substring(0, dot);

            }

        }

        return filename;

    }

    public static String getByteBufferToString(ByteBuffer buffer) {
        Charset charset = null;
        CharsetDecoder decoder = null;
        CharBuffer charBuffer = null;
        try {
            charset = Charset.forName("UTF-8");
            decoder = charset.newDecoder();
//            charBuffer = decoder.decode(buffer);//用这个的话，只能输出来一次结果，第二次显示为空
            charBuffer = decoder.decode(buffer.asReadOnlyBuffer());
            return charBuffer.toString();
        } catch (Exception ex) {
            ex.printStackTrace(); return "";
        }
    }

    public static String getSubString(int num, String str){
        try {
            if(str.length() > num){
                return str.substring(0,num) + "...";
            }
        }catch (Exception e){

        }
        return str;
    }

    public static String getTimeToString(int time){
        if(time == 60){
            return "1分钟";
        }else if(time == 900){
            return "15分钟";
        }else if(time == 3600){
            return "1小时";
        }else if(time == 86400){
            return "1个自然日";
        }else {
            return "";
        }
    }

    public static String phoneToHide(String phone){
        StringBuilder stringBuilder = new StringBuilder();
        if(phone.matches("^[1]\\d{10}$")){
            return stringBuilder.append(phone.substring(0,3)).append("***").append(phone.substring(phone.length() - 3,phone.length())).toString();
        }else{
            if(phone.length() == 13){
                if(phone.substring(2,phone.length()).matches("^[1]\\d{10}$")){
                    return stringBuilder.append(phone.substring(0,5)).append("***").append(phone.substring(phone.length() - 3,phone.length())).toString();
                }
            }
        }
        return phone;
    }
}
