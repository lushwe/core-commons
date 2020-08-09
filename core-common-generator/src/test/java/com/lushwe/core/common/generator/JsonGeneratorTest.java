package com.lushwe.core.common.generator;

/**
 * 说明：TODO 写点注释吧
 *
 * @author Jack Liu
 * @date 2019-07-04 14:46
 * @since 1.0
 */
public class JsonGeneratorTest {


    public static void main(String[] args) throws Exception {

        String json = JsonGenerator.createJson(User.class);

        System.out.println(json);

    }
}
