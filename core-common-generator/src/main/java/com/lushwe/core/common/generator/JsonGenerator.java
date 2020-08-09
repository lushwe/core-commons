package com.lushwe.core.common.generator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * 说明：对象默认值JSON生成器
 *
 * @author Jack Liu
 * @date 2019-07-04 14:39
 * @since 1.0
 */
public class JsonGenerator {


    /**
     * 根据Class生成对应对象JSON字符串
     *
     * @param clazz
     * @param <T>
     * @throws Exception
     */
    public static <T> String createJson(Class<T> clazz) throws Exception {

        T instance = clazz.newInstance();

        Method[] methods = clazz.getMethods();

        for (Method method : methods) {

            if (isObjectMethod(method)) {
                continue;
            }

            if (method.getName().startsWith("set")) {
                if (method.getParameterTypes()[0] == Date.class) {
                    method.invoke(instance, new Date());
                }
            }
        }

        int features = 0;
        features |= SerializerFeature.WriteNullListAsEmpty.getMask();
        features |= SerializerFeature.WriteNullStringAsEmpty.getMask();
        features |= SerializerFeature.WriteNullNumberAsZero.getMask();
        features |= SerializerFeature.WriteNullBooleanAsFalse.getMask();
        features |= SerializerFeature.PrettyFormat.getMask();

        return JSON.toJSONString(instance, features);
    }

    private static boolean isObjectMethod(Method method) {
        return "equals".equals(method.getName())
                || "toString".equals(method.getName())
                || "hashCode".equals(method.getName())
                || "wait".equals(method.getName())
                || "getClass".equals(method.getName())
                || "notify".equals(method.getName())
                || "notifyAll".equals(method.getName());
    }
}
