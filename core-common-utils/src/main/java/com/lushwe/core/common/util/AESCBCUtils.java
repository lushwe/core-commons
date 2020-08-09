package com.lushwe.core.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * 说明：AES CBC
 *
 * @author Jack Liu
 * @date 2020-08-03 17:03
 * @since 0.1
 */
public class AESCBCUtils extends AESUtils {

    private static final String ENCODING = "UTF-8";
    private static final String ALGORITHM = "AES";

    /**
     * 加解密算法/工作模式/填充方式
     */
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * 填充向量
     */
    private static final String FILL_VECTOR = "1234560405060708";

    private AESCBCUtils() {

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
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(key);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
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
            SecretKeySpec skeySpec = new SecretKeySpec(key, ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(key);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("decrypt fail!", e);
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
            byte[] datas = Base64.getDecoder().decode(data.getBytes(ENCODING));
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
     * @throws UnsupportedEncodingException
     */
    public static void main(String[] args) throws UnsupportedEncodingException {

        // 秘钥（必须16字节，128比特）
        String key = "9230967890982316";
        String keyBase64 = Base64.getEncoder().encodeToString(key.getBytes(ENCODING));
        System.out.println("秘钥[Base64]-->" + keyBase64);

        String str = "Hello world";
        System.out.println("待加密字符串-->" + str);

        // 加密
        String encryptStr = encryptToBase64(str, keyBase64);
        System.out.println("加密后字符串-->" + encryptStr);

        // 解密
        String decryptStr = decryptFromBase64(encryptStr, keyBase64);
        System.out.println("解密后字符串-->" + decryptStr);

    }
}
