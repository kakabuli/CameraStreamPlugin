package com.kakabuli.camerastream.utils;

import com.tencent.mmkv.MMKV;

public class MMKVUtils {

    public static void setMMKV(String id , String key, int value){
        MMKV kv = MMKV.mmkvWithID(id);
        kv.encode(key, value);
    }

    public static void setMMKV(String id , String key, String value){
        MMKV kv = MMKV.mmkvWithID(id);
        kv.encode(key, value);
    }

    public static void setMMKV(String id , String key, boolean value){
        MMKV kv = MMKV.mmkvWithID(id);
        kv.encode(key, value);
    }

    public static void setMMKV(String id , String key, long value){
        MMKV kv = MMKV.mmkvWithID(id);
        kv.encode(key, value);
    }

    public static void setMMKV(String id , String key, float value){
        MMKV kv = MMKV.mmkvWithID(id);
        kv.encode(key, value);
    }

    public static void setMMKV(String id , String key, byte[] value){
        MMKV kv = MMKV.mmkvWithID(id);
        kv.encode(key, value);
    }

    public static void setMMKV(String id , String key, double value){
        MMKV kv = MMKV.mmkvWithID(id);
        kv.encode(key, value);
    }

    public static void setMMKV(String key, int value){
        MMKV kv = MMKV.defaultMMKV();
        kv.encode(key, value);
    }

    public static void setMMKV(String key, String value){
        MMKV kv = MMKV.defaultMMKV();
        kv.encode(key, value);
    }

    public static void setMMKV(String key, boolean value){
        MMKV kv = MMKV.defaultMMKV();
        kv.encode(key, value);
    }

    public static void setMMKV(String key, long value){
        MMKV kv = MMKV.defaultMMKV();
        kv.encode(key, value);
    }

    public static void setMMKV(String key, float value){
        MMKV kv = MMKV.defaultMMKV();
        kv.encode(key, value);
    }

    public static void setMMKV(String key, byte[] value){
        MMKV kv = MMKV.defaultMMKV();
        kv.encode(key, value);
    }

    public static void setMMKV(String key, double value){
        MMKV kv = MMKV.defaultMMKV();
        kv.encode(key, value);
    }

    public static String getStringMMKV(String id , String key, String value){
        MMKV kv = MMKV.mmkvWithID(id);
        return kv.decodeString(key,value);
    }

    public static String getStringMMKVById(String id, String key){
        MMKV kv = MMKV.mmkvWithID(id);
        return kv.decodeString(key);
    }

    public static String getStringMMKV(String key, String value){
        MMKV kv = MMKV.defaultMMKV();
        return kv.decodeString(key,value);
    }

    public static String getStringMMKV(String key){
        MMKV kv = MMKV.defaultMMKV();
        return kv.decodeString(key);
    }

    public static boolean getBoolMMKV(String id , String key, boolean value){
        MMKV kv = MMKV.mmkvWithID(id);
        return kv.decodeBool(key,value);
    }

    public static boolean getBoolMMKV(String id, String key){
        MMKV kv = MMKV.mmkvWithID(id);
        return kv.decodeBool(key);
    }

    public static boolean getBoolMMKV(String key, boolean value){
        MMKV kv = MMKV.defaultMMKV();
        return kv.decodeBool(key,value);
    }

    public static boolean getBoolMMKV(String key){
        MMKV kv = MMKV.defaultMMKV();
        return kv.decodeBool(key);
    }

    public static long getLongMMKV(String id , String key, long value){
        MMKV kv = MMKV.mmkvWithID(id);
        return kv.decodeLong(key,value);
    }

    public static long getLongMMKV(String id, String key){
        MMKV kv = MMKV.mmkvWithID(id);
        return kv.decodeLong(key);
    }

    public static long getLongMMKV(String key, long value){
        MMKV kv = MMKV.defaultMMKV();
        return kv.decodeLong(key,value);
    }

    public static long getLongMMKV(String key){
        MMKV kv = MMKV.defaultMMKV();
        return kv.decodeLong(key);
    }

    public static double getDoubleMMKV(String id , String key, double value){
        MMKV kv = MMKV.mmkvWithID(id);
        return kv.decodeDouble(key,value);
    }

    public static double getDoubleMMKV(String id, String key){
        MMKV kv = MMKV.mmkvWithID(id);
        return kv.decodeDouble(key);
    }

    public static double getDoubleMMKV(String key, double value){
        MMKV kv = MMKV.defaultMMKV();
        return kv.decodeDouble(key,value);
    }

    public static double getDoubleMMKV(String key){
        MMKV kv = MMKV.defaultMMKV();
        return kv.decodeDouble(key);
    }

    public static float getFloatMMKV(String id , String key, float value){
        MMKV kv = MMKV.mmkvWithID(id);
        return kv.decodeFloat(key,value);
    }

    public static float getFloatMMKV(String id, String key){
        MMKV kv = MMKV.mmkvWithID(id);
        return kv.decodeFloat(key);
    }

    public static float getFloatMMKV(String key, float value){
        MMKV kv = MMKV.defaultMMKV();
        return kv.decodeFloat(key,value);
    }

    public static float getFloatMMKV(String key){
        MMKV kv = MMKV.defaultMMKV();
        return kv.decodeFloat(key);
    }

    public static byte[] getBytesMMKV(String id , String key, byte[] value){
        MMKV kv = MMKV.mmkvWithID(id);
        return kv.decodeBytes(key,value);
    }

    public static byte[] getBytesMMKV(String id, String key){
        MMKV kv = MMKV.mmkvWithID(id);
        return kv.decodeBytes(key);
    }

    public static byte[] getBytesMMKV(String key, byte[] value){
        MMKV kv = MMKV.defaultMMKV();
        return kv.decodeBytes(key,value);
    }

    public static byte[] getBytesMMKV(String key){
        MMKV kv = MMKV.defaultMMKV();
        return kv.decodeBytes(key);
    }

    public static int getIntMMKV(String id , String key, int value){
        MMKV kv = MMKV.mmkvWithID(id);
        return kv.decodeInt(key,value);
    }

    public static int getIntMMKV(String id, String key){
        MMKV kv = MMKV.mmkvWithID(id);
        return kv.decodeInt(key);
    }

    public static int getIntMMKV(String key, int value){
        MMKV kv = MMKV.defaultMMKV();
        return kv.decodeInt(key,value);
    }

    public static int getIntMMKV(String key){
        MMKV kv = MMKV.defaultMMKV();
        return kv.decodeInt(key);
    }


    public static void clearAllById(String key){
        MMKV mmkv = MMKV.mmkvWithID(key);
        String[] str = mmkv.allKeys();
        mmkv.removeValuesForKeys(str);
    }

    public static void clearAll(){
        MMKV mmkv = MMKV.defaultMMKV();
        String[] str = mmkv.allKeys();
        mmkv.removeValuesForKeys(str);
    }

    public static boolean removeKeyById(String id, String key){
        MMKV mmkv = MMKV.mmkvWithID(id);
        mmkv.removeValueForKey(key);
        return mmkv.containsKey(key);
    }

    public static boolean removeKey(String key){
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.removeValueForKey(key);
        return mmkv.containsKey(key);
    }

    public static boolean containsKeyById(String id, String key){
        MMKV mmkv = MMKV.mmkvWithID(id);
        return mmkv.containsKey(key);
    }

    public static boolean containsKey(String key){
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.containsKey(key);
    }
}
