package com.lushwe.core.common.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 说明：AES ECB
 *
 * @author Jack Liu
 * @date 2019-07-31 17:03
 * @since 0.1
 */
public class AESECBUtils {

    private static final String ENCODING = "UTF-8";

    private static final String ALGORITHM = "AES";

    /**
     * 加解密算法/工作模式/填充方式
     */
    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * AES的密钥长度
     */
    private static final Integer SECRET_KEY_LENGTH = 256;

    private AESECBUtils() {

    }

    /**
     * 加密
     *
     * @param data 待加密数据（字节数组）
     * @param key  加密秘钥（字节数组）
     * @return 加密后字节数组
     */
    public static byte[] encrypt(byte[] data, byte[] key) {
        try {
            // 创建密码器
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

            SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
            // 初始化
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            // 加密
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("encrypt fail!", e);
        }
    }

    /**
     * 解密
     *
     * @param data 待解密数据（字节数组）
     * @param key  解密密钥（字节数组）
     * @return 解密后字节数组
     */
    public static byte[] decrypt(byte[] data, byte[] key) {
        try {
            // 创建密码器
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

            SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            // 解密
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("decrypt fail!", e);
        }
    }

    /**
     * 获取加密Key
     *
     * @return
     */
    public static String getSecretKey() {
        try {
            //生成指定算法密钥的生成器
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);

            keyGenerator.init(SECRET_KEY_LENGTH, new SecureRandom());
            //生成密钥
            SecretKey secretKey = keyGenerator.generateKey();

            byte[] secretKeyEncoded = secretKey.getEncoded();

            byte[] encode = Base64.getEncoder().encode(secretKeyEncoded);

            return new String(encode, ENCODING);

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("getSecretKey fail!", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("getSecretKey fail!", e);
        }
    }

    /**
     * 加密
     *
     * @param data 待加密数据（字符串）
     * @param key  加密秘钥（Base64字符串）
     * @return 加密后Base64字符串
     */
    public static String encryptToBase64(String data, String key) {
        try {
            byte[] datas = data.getBytes(ENCODING);
            byte[] keys = Base64.getDecoder().decode(key.getBytes(ENCODING));
            byte[] encryptByte = encrypt(datas, keys);
            return new String(Base64.getEncoder().encode(encryptByte), ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("encryptToBase64 fail!", e);
        }
    }

    /**
     * 解密
     *
     * @param data 待解密数据（Base64字符串）
     * @param key  解密秘钥（Base64字符串）
     * @return 解密后Base64字符串
     */
    public static String decryptFromBase64(String data, String key) {
        try {
            byte[] datas = Base64.getDecoder().decode(data.getBytes());
            byte[] keys = Base64.getDecoder().decode(key.getBytes(ENCODING));
            byte[] decryptByte = decrypt(datas, keys);
            return new String(decryptByte, ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("decryptFromBase64 fail!", e);
        }
    }

    /**
     * 测试方法
     *
     * @param args
     */
    public static void main(String[] args) {

        String keyBase64 = getSecretKey();
        System.out.println("秘钥[Base64]-->" + keyBase64);

        String str = "Hello World";
        System.out.println("待加密字符串-->" + str);

        String encryptStr = encryptToBase64(str, keyBase64);
        System.out.println("加密后字符串-->" + encryptStr);

        String decryptStr = decryptFromBase64(encryptStr, keyBase64);
        System.out.println("解密后字符串-->" + decryptStr);

    }
}
