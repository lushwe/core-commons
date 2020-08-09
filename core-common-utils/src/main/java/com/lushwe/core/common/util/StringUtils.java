package com.lushwe.core.common.util;

import java.util.regex.Pattern;

/**
 * 说明：字符串工具类
 *
 * @author Jack Liu
 * @date 2019-06-21 10:16
 * @since 1.0
 */
public class StringUtils {

    /**
     * 数字
     */
    private static final Pattern PATTERN_NUM = Pattern.compile("^[0-9]*$");


    /**
     * 是否为数字
     *
     * @param str 待验证字符串
     * @return 是否为数字
     * @should
     */

    public static boolean isNumber(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        return PATTERN_NUM.matcher(str).matches();
    }
}
