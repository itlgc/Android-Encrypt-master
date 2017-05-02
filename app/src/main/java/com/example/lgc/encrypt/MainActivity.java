package com.example.lgc.encrypt;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Android中数据安全之编码转换和加解密
 * Created by LGC on 2017/4/27.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
//    private String data = "这是一个测试编码和加解密的字符串数据";
    private String data = "Base64";
    @Bind(R.id.et_input)
    EditText etInput;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.iv_icon)
    ImageView ivIcon;
    private SharedPreferences sp;
    private String encodeBase64Str;
    private String encryptDES;
    private String encryptAES;
    private String DES_Key = "deskey";
    private String AES_Key = "aeskey";
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    private String encryptPubStr64;
    private String encryptPriStr64;
    private byte[] encryptXor;
    private String sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        etInput.setText(data);

        //获取公钥和私钥
        try {
            KeyPair keyPair=RSAUtils.generateRSAKeyPair(RSAUtils.DEFAULT_KEY_SIZE);
            // 公钥
            publicKey = (RSAPublicKey) keyPair.getPublic();
            // 私钥
            privateKey = (RSAPrivateKey) keyPair.getPrivate();

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @OnClick({R.id.btn_base64_str, R.id.btn_base64_icon, R.id.btn_md5_encrypt, R.id.btn_des_encrypt, R.id
            .btn_aes_encrypt,R.id.btn_rsa_encrypt_sign, R.id.btn_rsa_encrypt_public,R.id.btn_rsa_encrypt_private, R.id
            .btn_xor_encrypt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_base64_str:
                base64Str();
                break;
            case R.id.btn_base64_icon:
                base64Icon();
                break;
            case R.id.btn_md5_encrypt:
                md5Encrypt();
                break;
            case R.id.btn_des_encrypt:
                desEncrypt();
                break;
            case R.id.btn_aes_encrypt:
                aesEncrypt();
                break;
            case R.id.btn_rsa_encrypt_public:
                rsaEncryptPublic();
                break;
            case R.id.btn_rsa_encrypt_private:
                rsaEncryptPrivate();
                break;
            case R.id.btn_rsa_encrypt_sign:
                rsaSign();
                break;
            case R.id.btn_xor_encrypt:
                xorEncrypt();
                break;
        }
    }



    private boolean isXorEncrypt = true;
    /**
     * 异或加密算法
     */
    private void xorEncrypt() {
        if (isXorEncrypt) {
            encryptXor = XORUtils.encrypt(data.getBytes());
            Log.d("TAG:"+TAG,"----XOR异或加密: "+Base64.encodeToString(encryptXor,Base64.DEFAULT));
            tvContent.setText("XOR异或加密: "+Base64.encodeToString(encryptXor,Base64.DEFAULT));
        }else {
            byte[] decryptXor = XORUtils.decrypt(encryptXor);
            Log.d("TAG:" + TAG, "----XOR异或解密: " + new String(decryptXor));
            tvContent.setText("XOR异或解密: " + new String(decryptXor));
        }
        isXorEncrypt = !isXorEncrypt;
    }
    private boolean isRsaSign = true;
    /**
     * RSA(非对称) 私钥生成数字签名,公钥检验
     */
    private void rsaSign() {
        if (isRsaSign) {
            try {
                sign = RSAUtils.sign(data.getBytes(), privateKey.getEncoded());
                Log.d("TAG:"+TAG,"----RSA私钥生成签名: "+ sign);
                tvContent.setText("RSA私钥生成签名: "+ sign);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try {
                boolean verify = RSAUtils.verify(data.getBytes(), publicKey.getEncoded(), sign);
                Log.d("TAG:"+TAG,"----RSA公钥检验结果: "+ verify);
                tvContent.setText("RSA公钥检验结果: "+ verify);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isRsaSign = !isRsaSign;
    }

    private boolean isRsaEncryptPri = true;
    /**
     * RSA(非对称) 私钥加密,公钥解密
     */
    private void rsaEncryptPrivate() {
        if (isRsaEncryptPri) {
            try {
                byte[] encryptByPrivateKeyBytes = RSAUtils.encryptByPrivateKey(data.getBytes(),
                privateKey.getEncoded());

                encryptPriStr64 = Base64.encodeToString(encryptByPrivateKeyBytes,Base64.DEFAULT);
                Log.d("TAG:"+TAG,"----RSA私钥加密: "+encryptPriStr64);
                tvContent.setText("RSA私钥加密: "+encryptPriStr64);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("TAG:"+TAG,"----RSA私钥加密失败");
            }
        }else{
            try {
                byte[] decryptByPublicKeyBytes = RSAUtils.decryptByPublicKey(Base64.decode(encryptPriStr64,
                        Base64.DEFAULT),publicKey.getEncoded());
                String decryptStr = new String(decryptByPublicKeyBytes);
                Log.d("TAG:"+TAG,"----RSA公钥解密: "+decryptStr);
                tvContent.setText("RSA公钥解密: "+decryptStr);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("TAG:"+TAG,"----RSA公钥解密失败");
            }
        }
        isRsaEncryptPri = !isRsaEncryptPri;
    }

    private boolean isRsaEncryptPub = true;
    /**
     * RSA(非对称) 公钥加密,私钥解密
     */
    private void rsaEncryptPublic() {
        if (isRsaEncryptPub) {
            try {
                byte[] encryptByPublicKeyBytes = RSAUtils.encryptByPublicKey(data.getBytes(), publicKey.getEncoded());
                //对于加密而言,操作的都是字节数组,所以得到的字节数组不属于任何一种编码格式,所以要将其转换为String的话,可以利用Base64来实现
                encryptPubStr64 = Base64.encodeToString(encryptByPublicKeyBytes,Base64.DEFAULT);
                Log.d("TAG:"+TAG,"----RSA公钥加密: "+ encryptPubStr64);

                tvContent.setText("RSA公钥加密: "+ encryptPubStr64);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("TAG:"+TAG,"----RSA公钥加密失败");
            }
        }else{
            try {
                byte[] decryptByPrivateKeyBytes = RSAUtils.decryptByPrivateKey(Base64.decode(encryptPubStr64, Base64.DEFAULT), privateKey
                        .getEncoded());
                String decryptStr = new String(decryptByPrivateKeyBytes);
                Log.d("TAG:"+TAG,"----RSA私钥解密: "+decryptStr);
                tvContent.setText("RSA私钥解密: "+decryptStr);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("TAG:"+TAG,"----RSA私钥解密失败");
            }
        }

        isRsaEncryptPub = !isRsaEncryptPub;
    }

    private boolean isAesEncrypt = true;

    /**
     * AES 加解密
     */
    private void aesEncrypt() {
        tvContent.setText("");
        if (isAesEncrypt) {
            encryptAES = AESUtils.encrypt(AES_Key, data);
            Log.d("TAG:" + TAG, "----AES加密后: " + encryptAES);
            tvContent.setText("AES加密后: " + encryptAES);
        } else {
            String decryptAES = AESUtils.decrypt(AES_Key, encryptAES);
            Log.d("TAG:" + TAG, "----AES解密后: " + decryptAES);
            tvContent.setText("AES解密后: " + decryptAES);
        }

        isAesEncrypt = !isAesEncrypt;

    }

    private boolean isDesEncrypt = true;

    /**
     * DES 加解密
     */
    private void desEncrypt() {

        tvContent.setText("");
        if (isDesEncrypt) {
            encryptDES = DESUtils.encrypt(DES_Key, data);
            Log.d("TAG:" + TAG, "----DES加密后: " + encryptDES);
            tvContent.setText("DES加密后: " + encryptDES);
        } else {
            String decodeDES = DESUtils.decrypt(DES_Key, encryptDES);
            Log.d("TAG:" + TAG, "----DES解密后: " + decodeDES);
            tvContent.setText("DES解密后: " + decodeDES);
        }

        isDesEncrypt = !isDesEncrypt;
    }

    /**
     * MD5加密 (MD5加密不可逆)
     */
    private void md5Encrypt() {
        tvContent.setText("");
        String md5EncryptStr = MD5Utils.md5(data);
        Log.d("TAG:" + TAG, "----MD5加前原文: " + data);
        Log.d("TAG:" + TAG, "----MD5加密后: " + md5EncryptStr);
        tvContent.setText("MD5加密后: " + md5EncryptStr);

    }

    private boolean isBase64EncodeIcon = true;

    /**
     * 图片Base64编码解码
     */
    private void base64Icon() {
        ivIcon.setImageBitmap(null);
        tvContent.setText("");
        if (isBase64EncodeIcon) {
            //1. 准备图片,获取图片的bitmap对象
            Bitmap iconBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            //2. 把bitmap图片压缩输出
            /**bitmap压缩输出 * format:压缩格式* quality：压缩的质量  0-100* stream：输出流*/
            ByteArrayOutputStream baos = new ByteArrayOutputStream();//字节数组输出流
            iconBitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
            //3. 通过base64 byte[]转成String,之后可以key-value jsonString 等方式上传到服务器
            String iconBase64Str = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            Log.d("TAG:" + TAG, "----图片经base64编码后 :" + iconBase64Str);
            tvContent.setText("图片经base64编码后: " + iconBase64Str);
            //4. 通过SharedPreferences保存icon的字符串
            sp.edit().putString("icon", iconBase64Str).apply();
        } else {
            String iconStr = sp.getString("icon", "");
            //5. 通过Base64解码icon的字符串
            byte[] decodeIcon = Base64.decode(iconStr, Base64.DEFAULT);
            //6. 把字节数组解码为bitmap位图对象---->可以操作-完成图片的上传
            Bitmap iconBitmap = BitmapFactory.decodeByteArray(decodeIcon, 0, decodeIcon.length);
            ivIcon.setImageBitmap(iconBitmap);
        }
        isBase64EncodeIcon = !isBase64EncodeIcon;
    }


    private boolean isBase64Encode = true;

    /**
     * 字符串Base64编码解码
     */
    private void base64Str() {
        tvContent.setText("");
        if (isBase64Encode) {
            //编码
            encodeBase64Str = Base64.encodeToString(data.getBytes(), Base64.DEFAULT);
            Log.d("TAG:" + TAG, "----初始化数据明文:  " + data);
            Log.d("TAG:" + TAG, "----base64编码后暗文:  " + encodeBase64Str);
            tvContent.setText("base64编码后暗文: " + encodeBase64Str);
        } else {
            //解码
            byte[] decodeStr = Base64.decode(encodeBase64Str, Base64.DEFAULT);
            Log.d("TAG:" + TAG, "----base64解码后明文:  " + new String(decodeStr));
            tvContent.setText("base64解码后明文: " + new String(decodeStr));
        }
        isBase64Encode = !isBase64Encode;
    }


}
