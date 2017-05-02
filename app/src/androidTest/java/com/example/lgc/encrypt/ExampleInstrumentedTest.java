package com.example.lgc.encrypt;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
    }
    @Test
    public void aesEncryptTest() {
        List<Person> personList = new ArrayList<>();
        int testMaxCount = 1000;//测试的最大数据条数
        //添加测试数据
        for (int i = 0; i < testMaxCount; i++) {
            Person person = new Person();
            person.setAge(i);
            person.setName(String.valueOf(i));
            personList.add(person);
        }
        //FastJson生成json数据
        String jsonData = JSON.toJSONString(personList);
        Log.e("MainActivity", "AES加密前json数据 ---->" + jsonData);
        Log.e("MainActivity", "AES加密前json数据长度 ---->" + jsonData.length());

        //生成一个动态key
        String secretKey = AESUtils.generateKey();
        Log.e("MainActivity", "AES动态secretKey ---->" + secretKey);

        //AES加密
        long start = System.currentTimeMillis();
        String encryStr = AESUtils.encrypt(secretKey, jsonData);
        long end = System.currentTimeMillis();
        Log.e("MainActivity", "AES加密耗时 cost time---->" + (end - start));
        Log.e("MainActivity", "AES加密后json数据 ---->" + encryStr);
        Log.e("MainActivity", "AES加密后json数据长度 ---->" + encryStr.length());

        //AES解密
        start = System.currentTimeMillis();
        String decryStr = AESUtils.decrypt(secretKey, encryStr);
        end = System.currentTimeMillis();
        Log.e("MainActivity", "AES解密耗时 cost time---->" + (end - start));
        Log.e("MainActivity", "AES解密后json数据 ---->" + decryStr);
    }
@Test
    public void rsaEncryptTest() {

        List<Person> personList=new ArrayList<>();
        int testMaxCount=100;//测试的最大数据条数
        //添加测试数据
        for(int i=0;i<testMaxCount;i++){
            Person person =new Person();
            person.setAge(i);
            person.setName(String.valueOf(i));
            personList.add(person);
        }
        //FastJson生成json数据

//        String jsonData=JsonUtils.objectToJsonForFastJson(personList);
        String jsonData = JSON.toJSONString(personList);

        Log.e("MainActivity","加密前json数据 ---->"+jsonData);
        Log.e("MainActivity","加密前json数据长度 ---->"+jsonData.length());



        KeyPair keyPair=RSAUtils.generateRSAKeyPair(RSAUtils.DEFAULT_KEY_SIZE);
        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        try {


        //公钥加密
        long start=System.currentTimeMillis();
        byte[] encryptBytes=    RSAUtils.encryptByPublicKeyForSpilt(jsonData.getBytes(),publicKey.getEncoded());
        long end=System.currentTimeMillis();
        Log.e("MainActivity","公钥加密耗时 cost time---->"+(end-start));
        String encryStr=Base64Encoder.encode(encryptBytes);
        Log.e("MainActivity","加密后json数据 --1-->"+encryStr);
        Log.e("MainActivity","加密后json数据长度 --1-->"+encryStr.length());
        //私钥解密
        start=System.currentTimeMillis();
        byte[] decryptBytes=  RSAUtils.decryptByPrivateKeyForSpilt(Base64Decoder.decodeToBytes(encryStr),privateKey.getEncoded());
        String decryStr=new String(decryptBytes);
        end=System.currentTimeMillis();
        Log.e("MainActivity","私钥解密耗时 cost time---->"+(end-start));
        Log.e("MainActivity","解密后json数据 --1-->"+decryStr);

        //私钥加密
        start=System.currentTimeMillis();
        encryptBytes=    RSAUtils.encryptByPrivateKeyForSpilt(jsonData.getBytes(),privateKey.getEncoded());
        end=System.currentTimeMillis();
        Log.e("MainActivity","私钥加密密耗时 cost time---->"+(end-start));
        encryStr=Base64Encoder.encode(encryptBytes);
        Log.e("MainActivity","加密后json数据 --2-->"+encryStr);
        Log.e("MainActivity","加密后json数据长度 --2-->"+encryStr.length());
        //公钥解密
        start=System.currentTimeMillis();
        decryptBytes=  RSAUtils.decryptByPublicKeyForSpilt(Base64Decoder.decodeToBytes(encryStr),publicKey.getEncoded());
        decryStr=new String(decryptBytes);
        end=System.currentTimeMillis();
        Log.e("MainActivity","公钥解密耗时 cost time---->"+(end-start));
        Log.e("MainActivity","解密后json数据 --2-->"+decryStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
