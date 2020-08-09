package com.lushwe.core.common.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Hmac算法工具类
 *
 * @author Jack Liu
 * @date 2018/9/30 9:31
 */
public class HmacUtil {

    private static final String ALGORITHM = "HmacSHA1";

    private HmacUtil() {

    }

    public static String encodeToString(String data, String key) {
        return encodeToString(data, null, key);
    }

    public static String encodeToString(String data, String charsetName, String key) {
        byte[] bytes = encode(data, charsetName, key);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] encode(String data, String key) {
        return encode(data, null, key);
    }

    public static byte[] encode(String data, String charsetName, String key) {

        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(signingKey);
            if (charsetName == null || charsetName.length() == 0) {
                return mac.doFinal(data.getBytes());
            }
            return mac.doFinal(data.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("不支持编码[" + charsetName + "]", e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("不支持算法[" + ALGORITHM + "]", e);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("无效Key[" + key + "]", e);
        }
    }
}
