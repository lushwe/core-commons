package com.lushwe.core.common.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * MD5算法工具类
 *
 * @author Jack Liu
 * @date 2018/9/30 9:16
 */
public class MD5Util {

    private static final String ALGORITHM = "MD5";

    private MD5Util() {

    }

    public static String encodeToString(String data) {
        return encodeToString(data, null);
    }

    public static String encodeToString(String data, String charsetName) {
        byte[] bytes = encode(data, charsetName);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] encode(String data) {
        return encode(data, null);
    }

    public static byte[] encode(String data, String charsetName) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            if (charsetName == null || charsetName.length() == 0) {
                return md.digest(data.getBytes());
            }
            return md.digest(data.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("不支持编码[" + charsetName + "]", e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("不支持算法[" + ALGORITHM + "]", e);
        }
    }
}
