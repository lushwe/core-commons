package com.lushwe.core.common.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * 说明：AES加密测试
 *
 * @author Jack Liu
 * @date 2019-07-31 16:38
 * @since 0.1
 */
public class AESCBCUtilsTest {


    public static void main(String[] args) throws UnsupportedEncodingException {


        String key = "9230967890982316";
        String keyBase64 = Base64.getEncoder().encodeToString(key.getBytes("UTF-8"));


        String encryptToBase64 = AESCBCUtils.encryptToBase64("1", keyBase64);
        System.out.println("encryptToBase64-->" + encryptToBase64);
    }
}
