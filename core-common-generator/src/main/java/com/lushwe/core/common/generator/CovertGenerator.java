package com.lushwe.core.common.generator;

import java.lang.reflect.Field;

/**
 * 说明：对象转换生成器
 *
 * @author Jack Liu
 * @date 2019-07-03 16:17
 * @since 1.0
 */
public class CovertGenerator {

    public static <T> void createCovertFile(Class<T> clazz) {

        int lastIndex = clazz.getName().lastIndexOf(".");
        String className = clazz.getName().substring(lastIndex + 1);

        String name = className
                .replace("Request", "")
                .replace("Response", "")
                .replace("BO", "")
                .replace("DO", "");

        Field[] fields = clazz.getDeclaredFields();


        System.out.println("==========" + firstToLowerCase(name) + "RequestToBO==========");
        StringBuilder sb = new StringBuilder();
        sb.append("public static " + name + " " + firstToLowerCase(name) + "RequestToBO(" + name + "Request " + firstToLowerCase(name) + "Request" + "){").append("\r\n");
        sb.append(getTabStr(1)).append("if (" + firstToLowerCase(name) + "Request" + " == null) {").append("\r\n");
        sb.append(getTabStr(2)).append("return null;").append("\r\n");
        sb.append(getTabStr(1)).append("}").append("\r\n");

        sb.append(getTabStr(1)).append(name + " " + firstToLowerCase(name) + " = new " + name + "();").append("\r\n");
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            sb.append(getTabStr(1)).append(firstToLowerCase(name) + ".set" + firstToUpperCase(fieldName) + "(" + firstToLowerCase(name) + "Request.get" + firstToUpperCase(fieldName) + "());").append("\r\n");
        }
        sb.append(getTabStr(1)).append("return " + firstToLowerCase(name) + ";").append("\r\n");
        sb.append("}").append("\r\n");
        System.out.println(sb.toString());


        System.out.println("==========" + firstToLowerCase(name) + "RequestListToBOList==========");
        sb = new StringBuilder();
        sb.append("public static List<" + name + "> " + firstToLowerCase(name) + "RequestListToBOList(List<" + name + "Request> " + firstToLowerCase(name) + "RequestList" + "){").append("\r\n");
        sb.append(getTabStr(1)).append("if (CollectionUtils.isEmpty(" + firstToLowerCase(name) + "RequestList)" + ") {").append("\r\n");
        sb.append(getTabStr(2)).append("return Lists.newArrayList();").append("\r\n");
        sb.append(getTabStr(1)).append("}").append("\r\n");

        sb.append(getTabStr(1)).append("List<" + name + "> " + firstToLowerCase(name) + "List = new ArrayList<>" + "();").append("\r\n");

        sb.append(getTabStr(1)).append("for (" + name + "Request " + firstToLowerCase(name) + "Request : " + firstToLowerCase(name) + "RequestList) {").append("\r\n");
        sb.append(getTabStr(2)).append(firstToLowerCase(name) + "List.add(" + firstToLowerCase(name) + "RequestToBO(" + firstToLowerCase(name) + "Request));").append("\r\n");
        sb.append(getTabStr(1)).append("}").append("\r\n");

        sb.append(getTabStr(1)).append("return " + firstToLowerCase(name) + "List;").append("\r\n");
        sb.append("}").append("\r\n");
        System.out.println(sb.toString());


        System.out.println("==========" + firstToLowerCase(name) + "ResponseFromBO==========");
        sb = new StringBuilder();
        sb.append("public static " + name + "Response " + firstToLowerCase(name) + "ResponseFromBO(" + name + " " + firstToLowerCase(name) + "){").append("\r\n");
        sb.append(getTabStr(1)).append("if (" + firstToLowerCase(name) + " == null) {").append("\r\n");
        sb.append(getTabStr(2)).append("return null;").append("\r\n");
        sb.append(getTabStr(1)).append("}").append("\r\n");

        sb.append(getTabStr(1)).append(name + "Response " + firstToLowerCase(name) + "Response = new " + name + "Response();").append("\r\n");
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            sb.append(getTabStr(1)).append(firstToLowerCase(name) + "Response.set" + firstToUpperCase(fieldName) + "(" + firstToLowerCase(name) + ".get" + firstToUpperCase(fieldName) + "());").append("\r\n");
        }
        sb.append(getTabStr(1)).append("return " + firstToLowerCase(name) + "Response;").append("\r\n");

        sb.append("}").append("\r\n");
        System.out.println(sb.toString());


        System.out.println("==========" + firstToLowerCase(name) + "ResponseListFromBOList==========");
        sb = new StringBuilder();
        sb.append("public static List<" + name + "Response> " + firstToLowerCase(name) + "ResponseListFromBOList(List<" + name + "> " + firstToLowerCase(name) + "List" + "){").append("\r\n");
        sb.append(getTabStr(1)).append("if (CollectionUtils.isEmpty(" + firstToLowerCase(name) + "List)" + ") {").append("\r\n");
        sb.append(getTabStr(2)).append("return Lists.newArrayList();").append("\r\n");
        sb.append(getTabStr(1)).append("}").append("\r\n");

        sb.append(getTabStr(1)).append("List<" + name + "Response> " + firstToLowerCase(name) + "ResponseList = new ArrayList<>" + "();").append("\r\n");

        sb.append(getTabStr(1)).append("for (" + name + " " + firstToLowerCase(name) + " : " + firstToLowerCase(name) + "List) {").append("\r\n");
        sb.append(getTabStr(2)).append(firstToLowerCase(name) + "ResponseList.add(" + firstToLowerCase(name) + "ResponseFromBO(" + firstToLowerCase(name) + "));").append("\r\n");
        sb.append(getTabStr(1)).append("}").append("\r\n");

        sb.append(getTabStr(1)).append("return " + firstToLowerCase(name) + "ResponseList;").append("\r\n");
        sb.append("}").append("\r\n");
        System.out.println(sb.toString());


        System.out.println("==========" + firstToLowerCase(name) + "ToDO==========");
        sb = new StringBuilder();
        sb.append("public static " + name + "DO " + firstToLowerCase(name) + "ToDO(" + name + " " + firstToLowerCase(name) + "" + "){").append("\r\n");
        sb.append(getTabStr(1)).append("if (" + firstToLowerCase(name) + " == null) {").append("\r\n");
        sb.append(getTabStr(2)).append("return null;").append("\r\n");
        sb.append(getTabStr(1)).append("}").append("\r\n");

        sb.append(getTabStr(1)).append(name + "DO " + firstToLowerCase(name) + "DO = new " + name + "DO();").append("\r\n");
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            sb.append(getTabStr(1)).append(firstToLowerCase(name) + "DO.set" + firstToUpperCase(fieldName) + "(" + firstToLowerCase(name) + ".get" + firstToUpperCase(fieldName) + "());").append("\r\n");
        }
        sb.append(getTabStr(1)).append("return " + firstToLowerCase(name) + "DO;").append("\r\n");
        sb.append("}").append("\r\n");
        System.out.println(sb.toString());

        System.out.println("==========" + firstToLowerCase(name) + "ListToDOList==========");
        sb = new StringBuilder();
        sb.append("public static List<" + name + "DO> " + firstToLowerCase(name) + "ListToDOList(List<" + name + "> " + firstToLowerCase(name) + "List" + "){").append("\r\n");
        sb.append(getTabStr(1)).append("if (CollectionUtils.isEmpty(" + firstToLowerCase(name) + "List)" + ") {").append("\r\n");
        sb.append(getTabStr(2)).append("return Lists.newArrayList();").append("\r\n");
        sb.append(getTabStr(1)).append("}").append("\r\n");

        sb.append(getTabStr(1)).append("List<" + name + "DO> " + firstToLowerCase(name) + "DOList = new ArrayList<>" + "();").append("\r\n");

        sb.append(getTabStr(1)).append("for (" + name + " " + firstToLowerCase(name) + " : " + firstToLowerCase(name) + "List) {").append("\r\n");
        sb.append(getTabStr(2)).append(firstToLowerCase(name) + "DOList.add(" + firstToLowerCase(name) + "ToDO(" + firstToLowerCase(name) + "));").append("\r\n");
        sb.append(getTabStr(1)).append("}").append("\r\n");

        sb.append(getTabStr(1)).append("return " + firstToLowerCase(name) + "DOList;").append("\r\n");
        sb.append("}").append("\r\n");
        System.out.println(sb.toString());


        System.out.println("==========" + firstToLowerCase(name) + "FromDO==========");
        sb = new StringBuilder();
        sb.append("public static " + name + " " + firstToLowerCase(name) + "FromDO(" + name + "DO " + firstToLowerCase(name) + "DO){").append("\r\n");
        sb.append(getTabStr(1)).append("if (" + firstToLowerCase(name) + "DO == null) {").append("\r\n");
        sb.append(getTabStr(2)).append("return null;").append("\r\n");
        sb.append(getTabStr(1)).append("}").append("\r\n");

        sb.append(getTabStr(1)).append(name + " " + firstToLowerCase(name) + " = new " + name + "();").append("\r\n");
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            sb.append(getTabStr(1)).append(firstToLowerCase(name) + ".set" + firstToUpperCase(fieldName) + "(" + firstToLowerCase(name) + "DO.get" + firstToUpperCase(fieldName) + "());").append("\r\n");
        }
        sb.append(getTabStr(1)).append("return " + firstToLowerCase(name) + ";").append("\r\n");

        sb.append("}").append("\r\n");
        System.out.println(sb.toString());

        System.out.println("==========" + firstToLowerCase(name) + "ListFromDOList==========");
        sb = new StringBuilder();
        sb.append("public static List<" + name + "> " + firstToLowerCase(name) + "ListFromDOList(List<" + name + "DO> " + firstToLowerCase(name) + "DOList){").append("\r\n");
        sb.append(getTabStr(1)).append("if (CollectionUtils.isEmpty(" + firstToLowerCase(name) + "DOList)) {").append("\r\n");
        sb.append(getTabStr(2)).append("return Lists.newArrayList();").append("\r\n");
        sb.append(getTabStr(1)).append("}").append("\r\n");

        sb.append(getTabStr(1)).append("List<" + name + "> " + firstToLowerCase(name) + "List = new ArrayList<>" + "();").append("\r\n");

        sb.append(getTabStr(1)).append("for (" + name + "DO " + firstToLowerCase(name) + "DO : " + firstToLowerCase(name) + "DOList) {").append("\r\n");
        sb.append(getTabStr(2)).append(firstToLowerCase(name) + "List.add(" + firstToLowerCase(name) + "FromDO(" + firstToLowerCase(name) + "DO));").append("\r\n");
        sb.append(getTabStr(1)).append("}").append("\r\n");

        sb.append(getTabStr(1)).append("return " + firstToLowerCase(name) + "List;").append("\r\n");
        sb.append("}").append("\r\n");
        System.out.println(sb.toString());
    }


    private static String firstToUpperCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static String firstToLowerCase(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    private static String getTabStr(int count) {
        String s = "";
        for (int i = 0; i < count; i++) {
            s = s + "    ";
        }
        return s;
    }
}
