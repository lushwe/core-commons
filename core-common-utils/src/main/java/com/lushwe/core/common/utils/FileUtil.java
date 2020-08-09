package com.lushwe.core.common.utils;

import java.io.File;

/**
 * 说明：文件工具类
 *
 * @author Jack Liu
 * @date 2019-06-21 10:14
 * @since 1.0
 */
public class FileUtil {

    /**
     * 构造方法私有化
     */
    private FileUtil() {

    }


    public static String read(String filePath) {

        File file = new File(filePath);

        return read(file);
    }

    public static String read(File file) {


        return "";
    }

    public static String write(String filePath) {

        File file = new File(filePath);

        return write(file);
    }

    public static String write(File file) {


        return "";
    }
}
