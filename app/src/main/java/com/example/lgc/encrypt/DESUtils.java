package com.example.lgc.encrypt;

import android.util.Base64;
import android.util.Log;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * DES加密工具类
 * Created by LGC on 2017/4/27.
 */

public class DESUtils {
    private static final String TAG = DESUtils.class.getSimpleName();
    private final static String HEX = "0123456789ABCDEF";
    private final static String TRANSFORMATION = "DES/CBC/PKCS5Padding";//DES是加密方式 CBC是工作模式 PKCS5Padding是填充模式
    private final static String IVPARAMETERSPEC = "01020304";////初始化向量参数，AES 为16bytes. DES 为8bytes.
    private final static String DES = "DES";//DES是加密方式
    private static final String SHA1PRNG = "SHA1PRNG";//// SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法



    public static String encrypt(String key, String data) {
        return encrypt(key, data.getBytes());
    }

    /**
     * DES算法，加密
     *
     * @param data 待加密字符串
     * @param key  加密私钥，长度不能够小于8位
     * @return 加密后的字节数组，一般结合Base64编码使用
     */
    public static String encrypt(String key, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            IvParameterSpec iv = new IvParameterSpec(IVPARAMETERSPEC.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, getRawKeyMethodOne(key), iv);
            byte[] bytes = cipher.doFinal(data);
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * DES算法，解密 获取编码后的值
     */
    public static String decrypt(String key, String data) {
        return decrypt(key, Base64.decode(data, Base64.DEFAULT));
    }

    /**
     * DES算法，解密
     * @param data 待解密字符串
     * @param key  解密私钥，长度不能够小于8位
     * @return 解密后的字节数组
     */
    public static String decrypt(String key, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            IvParameterSpec iv = new IvParameterSpec(IVPARAMETERSPEC.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, getRawKeyMethodOne(key), iv);
            byte[] original = cipher.doFinal(data);
            return new String(original);
        } catch (Exception e) {
            Log.d("TAG:"+TAG,"-------DES解码密码错误-------");
            return null;
        }
    }


    /**
     * 密钥处理方式  转换密钥(包括可以做随机处理)方式一 (针对DES)
     */
    private static Key getRawKeyMethodOne(String key) throws Exception {
        DESKeySpec dks = new DESKeySpec(key.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        return keyFactory.generateSecret(dks);
    }

    /**
     *  密钥处理方式 转换密钥(包括可以做随机处理)方式二 (同时适用于其他对称加密算法)
     */
    private static Key getRawKeyMethodTwo(String key) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(DES);
        //for android
        SecureRandom sr = SecureRandom.getInstance(SHA1PRNG, "Crypto");
        // for Java
        // secureRandom sr = SecureRandom.getInstance(SHA1PRNG);
        sr.setSeed(key.getBytes());
        kgen.init(64, sr); //DES固定格式为64bits，即8bytes。
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return new SecretKeySpec(raw, DES);//核心代码,前面都是对key进行处理,如果这个key之前就做过处理,只需这一步即可
    }
    /**
     * 动态生成密钥
     * @return 生成随机数，可以当做动态的密钥 加密和解密的密钥必须一致，不然将不能解密
     */
    public static String generateKey() {

        try {
            SecureRandom secureRandom = SecureRandom.getInstance(SHA1PRNG,"Crypto");
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
}
