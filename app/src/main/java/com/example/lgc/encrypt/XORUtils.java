package com.example.lgc.encrypt;

/**
 * 异或加解密
 * Created by LGC on 2017/5/2.
 */

public class XORUtils {
    /**
     * 固定key的方式
     */
    public static byte[] encrypt1(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        int len = bytes.length;
        int key = 0x12;
        for (int i = 0; i < len; i++) {
            bytes[i] ^= key;
        }
        return bytes;
    }

    /**
     * 不固定key的加密方式 (推荐使用不固定key)
     */
    public static byte[] encrypt(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        int len = bytes.length;
        int key = 0x12;
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) (bytes[i] ^ key);
            key = bytes[i];
        }
        return bytes;
    }

    /**
     * 对应不固定key的解密方式
     */
    public static byte[] decrypt(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        int len = bytes.length;
        int key = 0x12;
        for (int i = len - 1; i > 0; i--) {
            bytes[i] = (byte) (bytes[i] ^ bytes[i - 1]);
        }
        bytes[0] = (byte) (bytes[0] ^ key);
        return bytes;
    }



    private void test1() {
        byte[] bytes = encrypt1("whoislcj".getBytes());//加密
        String str1 = new String(encrypt(bytes));//解密
    }

    private void test2() {
        byte[] bytes = encrypt("whoislcj".getBytes());//加密
        String str1 = new String(decrypt(bytes));//解密
    }
}
