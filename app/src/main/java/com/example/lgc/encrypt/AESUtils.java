package com.example.lgc.encrypt;

import android.text.TextUtils;
import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密工具类
 * Created by LGC on 2017/4/27.
 */

public class AESUtils {
    private final static String HEX = "0123456789ABCDEF";
    private static final String CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";//AES是加密方式 CBC是工作模式 PKCS5Padding是填充模式
    private static final String AES = "AES";//AES 加密
    private static final String SHA1PRNG = "SHA1PRNG";// SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法

    /**
     * AES加密
     */
    public static String encrypt(String key, String data) {
        if (TextUtils.isEmpty(data)) {
            return data;
        }
        try {
            byte[] result = encrypt(key, data.getBytes());
            //return new String(result);//直接将经过加密后的字节数组利用String的API转成字符串只能得到乱码
            //return ByteToHex(result);//采用的byte[] 到 String 转换的方法都是将 byte[] 二进制利用16进制的char[]来表示
            return Base64.encodeToString(result, Base64.DEFAULT);//采用Android中Base64将AES加密后数据转成字符串同时实现(再次加密转化为暗文)
            //return Base64Encoder.encode(result);//采用Base64Encoder类来实现,效果和原生的Base64效果一样
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES加密
     */
    private static byte[] encrypt(String key, byte[] bytes) throws Exception {
        byte[] raw = getRawKey(key);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
        Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        byte[] encrypted = cipher.doFinal(bytes);
        return encrypted;
    }

    /**
     * 对密钥进行处理
     */
    private static byte[] getRawKey(String key) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(AES);
        //for android
        // 在4.2以上版本中，SecureRandom获取方式发生了改变
        SecureRandom sr = SecureRandom.getInstance(SHA1PRNG, "Crypto");
        // for Java
        // secureRandom = SecureRandom.getInstance(SHA1PRNG);
        sr.setSeed(key.getBytes());
        kgen.init(128, sr); //256 bits or 128 bits,192bits
        //AES中128位密钥版本有10个加密循环，192比特密钥版本有12个加密循环，256比特密钥版本则有14个加密循环。
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }


    /**
     * AES 解密
     */
    public static String decrypt(String key, String data) {
        if (TextUtils.isEmpty(data)) {
            return data;
        }
        try {
            //byte[] bytes = HexToByte(data);
            //byte[] bytes = Base64Decoder.decodeToBytes(data);
            byte[] bytes = Base64.decode(data, Base64.DEFAULT);
            byte[] result = decrypt(key, bytes);
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * AES 解密
     */
    private static byte[] decrypt(String key, byte[] data) throws Exception {
        byte[] raw = getRawKey(key);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
        Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        byte[] decrypted = cipher.doFinal(data);
        return decrypted;
    }


    /**
     * 动态生成密钥
     *
     * @return 生成随机数，可以当做动态的密钥 加密和解密的密钥必须一致，不然将不能解密
     */
    public static String generateKey() {

        try {
            SecureRandom secureRandom = SecureRandom.getInstance(SHA1PRNG, "Crypto");
            byte[] bytesKey = new byte[20];// 长度不能够小于8位字节 因为DES固定格式为64bits，即8bytes。
            secureRandom.nextBytes(bytesKey);
            String strKey = ByteToHex(bytesKey);
            return strKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 二进制转(16进制)字符串
     */
    private static String ByteToHex(byte[] bytesKey) {
        if (bytesKey == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (byte b : bytesKey) {
            sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
        }
        return sb.toString();
    }

    /**
     * (16进制)字符转二进制
     */
    private static byte[] HexToByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }
}
