package com.lushwe.core.common.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * java根据库表结构自动生成java实体,在网上找了相关资料，已经有很多大侠实现了这功能。 但是并没有一个使我自己满意的，要么生成的变量不规范，要么就是格式很乱。
 * 所以我参照网上一些已有的方法，自己动手改进了下，实现了可以生成规范的字段名及 get/set方法，只不过这里有个前提，就是在新建表字段时如果字段名较长，那么必须遵守用"_"隔开。
 * 其实只有这样才能规范，毕竟电脑也不是万能的，不可能根据你想要的格式自动生成规范的变量名及get/set方法。
 */
public class MySqlEntityGenerator {

    private String packageOutPath = "";                                     // 指定实体生成所在包的路径
    private String version;                                                 // 版本
    private String pk;                                                      // 主键
    private String author;                                                  // 作者
    private String tableName;                                               // 表名
    private String tableNameStr;                                               // 表
    private Map<String, String> colComments;                                // 列名注释

    private Table table;

    private boolean f_util = false;                                   // 是否需要导入包java.util.*
    private boolean f_sql = false;                                   // 是否需要导入包java.sql.*

    // 数据库连接
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://127.0.0.1:1531/dev";
    private static final String NAME = "root";
    private static final String PASS = "123456";

    private static final String path = "/Users/Dev/generator";

    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    String packageOfDAO = "com.lushwe.dal.dao.";
    String packageOfDO = "com.lushwe.dal.model.";
    String packageOfQO = "com.lushwe.dal.qo.";
    String packageOfCondition = "com.lushwe.model.plugin.page.PageQueryCondition";

    static class Table {

        /**
         * 表名
         */
        String name;

        /**
         * 表列数组
         */
        Column[] columns;

        /**
         * 表注释
         */
        String comment;

        public Table(String name, Column[] columns) {
            this.name = name;
            this.columns = columns;
        }
    }

    static class Column {

        /**
         * 列类型
         */
        String jdbcType;

        /**
         *
         */
        String javaType;

        /**
         * 列名称
         */
        String name;

        /**
         * 列注释
         */
        String comment;

    }

    private void init() {
        try {
            // 查要生成实体类的表
            String sql = "select * from " + tableName;
            String sql2 = "SELECT column_name, column_comment FROM information_schema.columns WHERE table_name = '" + tableName + "'";
            Class.forName(DRIVER);
            // 创建连接
            Connection conn = DriverManager.getConnection(URL, NAME, PASS);
            Statement statement = conn.createStatement();
            Statement statement2 = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            ResultSet rs2 = statement2.executeQuery(sql2);

            ResultSetMetaData rsmd = rs.getMetaData();

            int size = rsmd.getColumnCount(); // 统计列


            colComments = new HashMap<>(size);
            while (rs2.next()) {
                colComments.put(rs2.getString(1), rs2.getString(2));
            }

            Column[] columns = new Column[size];

            table = new Table(tableName, columns);

            for (int i = 0; i < size; i++) {

                Column column = new Column();
                String columnTypeName = rsmd.getColumnTypeName(i + 1);
                column.jdbcType = columnTypeName.replaceAll("UNSIGNED", "").replaceAll(" ", "");
                if (column.jdbcType.equals("INT")) {
                    column.jdbcType = "INTEGER";
                }
                column.javaType = rsmd.getColumnClassName(i + 1);
                column.name = rsmd.getColumnName(i + 1);
                column.comment = colComments.get(column.name);
                columns[i] = column;


//                String tableName = rsmd.getTableName(i + 1);
//                String columnLabel = rsmd.getColumnLabel(i + 1);
//                String columnClassName = rsmd.getColumnClassName(i + 1);
//                String columnName = rsmd.getColumnName(i + 1);
//                String columnTypeName = rsmd.getColumnTypeName(i + 1);

                if (columns[i].jdbcType.equalsIgnoreCase("date") || columns[i].jdbcType.equalsIgnoreCase("timestamp")) {
                    f_util = true;
                }
                if (columns[i].jdbcType.equalsIgnoreCase("blob") || columns[i].jdbcType.equalsIgnoreCase("char")) {
                    f_sql = true;
                }
            }

            statement.close();
            statement2.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDOFile(String name) throws Exception {

        System.out.println("==========DO==========");

        String className = name + "DO";

        StringBuilder sb = new StringBuilder();

        sb.append("import lombok.Data;").append("\r\n");
        sb.append("\r\n");
        sb.append("import java.util.Date;").append("\r\n");
        sb.append("\r\n");

        sb.append("/**").append("\r\n");
        sb.append(" * 说明：" + this.tableNameStr).append("\r\n");
        sb.append(" * ").append("\r\n");
        sb.append(" * @author " + this.author).append("\r\n");
        sb.append(" * @date " + df.format(new Date())).append("\r\n");
        sb.append(" * @since " + this.version).append("\r\n");
        sb.append(" */").append("\r\n");
        sb.append("@Data").append("\r\n");
        sb.append("public class " + name + "DO {").append("\r\n");

        for (int i = 0; i < table.columns.length; i++) {
            sb.append("\r\n");
            sb.append("/**").append("\r\n");
            sb.append(" * " + table.columns[i].comment).append("\r\n");
            sb.append(" */").append("\r\n");
            sb.append("private " + getJavaType(table.columns[i].jdbcType) + " " + defineVar(table.columns[i].name)).append(";").append("\r\n");
        }

        sb.append("}");

        System.out.println(sb.toString());

        writeFile(sb.toString(), "/model", className + ".java");
    }

    private void createDTOFile(String name) throws Exception {

        String className = name + "DTO";

        System.out.println("==========DTO==========");
        StringBuilder sb = new StringBuilder();

        sb.append("import lombok.Data;").append("\r\n");
        sb.append("\r\n");
        sb.append("import java.util.Date;").append("\r\n");
        sb.append("\r\n");

        sb.append("/**").append("\r\n");
        sb.append(" * 说明：" + this.tableNameStr).append("\r\n");
        sb.append(" * ").append("\r\n");
        sb.append(" * @author " + this.author).append("\r\n");
        sb.append(" * @date " + df.format(new Date())).append("\r\n");
        sb.append(" * @since " + this.version).append("\r\n");
        sb.append(" */").append("\r\n");
        sb.append("@Data").append("\r\n");
        sb.append("public class " + className + " {").append("\r\n");

        for (int i = 0; i < table.columns.length; i++) {
            sb.append("\r\n");
            sb.append("/**").append("\r\n");
            sb.append(" * " + table.columns[i].comment).append("\r\n");
            sb.append(" */").append("\r\n");
            sb.append("private " + getJavaType(table.columns[i].jdbcType) + " " + defineVar(table.columns[i].name)).append(";").append("\r\n");
        }

        sb.append("}");

        System.out.println(sb.toString());

        writeFile(sb.toString(), "/dto", className + ".java");
    }

    private void createQOFile(String name) throws Exception {

        String className = name + "QO";

        System.out.println("==========QO==========");
        StringBuilder sb = new StringBuilder();

        sb.append("import lombok.Data;").append("\r\n");
        sb.append("\r\n");
        sb.append("import " + packageOfDO + name + "DO;").append("\r\n");
        sb.append("\r\n");

        sb.append("/**").append("\r\n");
        sb.append(" * 说明：" + this.tableNameStr).append("\r\n");
        sb.append(" * ").append("\r\n");
        sb.append(" * @author " + this.author).append("\r\n");
        sb.append(" * @date " + df.format(new Date())).append("\r\n");
        sb.append(" * @since " + this.version).append("\r\n");
        sb.append(" */").append("\r\n");
        sb.append("@Data").append("\r\n");

        sb.append("public class " + className + " extends " + name + "DO {").append("\r\n");
//        for (int i = 0; i < table.columns.length; i++) {
//            sb.append("private " + getJavaType(table.columns[i].jdbcType) + " " + defineVar(table.columns[i].fileName)).append(";").append("\r\n");
//        }

        sb.append("}");

        System.out.println(sb.toString());

        writeFile(sb.toString(), "/qo", className + ".java");
    }

    private void createDAOFile(String name, String[] methodNames) throws IOException {

        String className = name + "DAO";

        System.out.println("==========DAO==========");
        StringBuilder sb = new StringBuilder();


        sb.append("import " + packageOfCondition + ";").append("\r\n");
        sb.append("\r\n");
        sb.append("import " + packageOfDO + name + "DO;").append("\r\n");
        sb.append("\r\n");
        sb.append("import " + packageOfQO + name + "QO;").append("\r\n");
        sb.append("\r\n");
        sb.append("\r\n");
        sb.append("import java.util.List;").append("\r\n");
        sb.append("\r\n");

        sb.append("\r\n");
        sb.append("/**").append("\r\n");
        sb.append(" * 说明：" + this.tableNameStr).append("\r\n");
        sb.append(" * ").append("\r\n");
        sb.append(" * @author " + this.author).append("\r\n");
        sb.append(" * @date " + df.format(new Date())).append("\r\n");
        sb.append(" * @since " + this.version).append("\r\n");
        sb.append(" */").append("\r\n");
        sb.append("public interface " + className + " {").append("\r\n");

        for (String methodName : methodNames) {
            switch (methodName) {
                case "insert":
                    sb.append("\r\n");
                    sb.append(getTabStr(1)).append("/**").append("\r\n");
                    sb.append(getTabStr(1)).append(" * 新增").append("\r\n");
                    sb.append(getTabStr(1)).append(" * ").append("\r\n");
                    sb.append(getTabStr(1)).append(" * @param " + firstToLowerCase(name) + "DO").append("\r\n");
                    sb.append(getTabStr(1)).append(" * @return ").append("\r\n");
                    sb.append(getTabStr(1)).append(" */").append("\r\n");
                    sb.append(getTabStr(1)).append("int insert(" + name + "DO " + firstToLowerCase(name) + "DO);").append("\r\n");
                    break;
                case "update":
                    sb.append("\r\n");
                    sb.append(getTabStr(1)).append("/**").append("\r\n");
                    sb.append(getTabStr(1)).append(" * 更新").append("\r\n");
                    sb.append(getTabStr(1)).append(" * ").append("\r\n");
                    sb.append(getTabStr(1)).append(" * @param " + firstToLowerCase(name) + "DO").append("\r\n");
                    sb.append(getTabStr(1)).append(" * @return ").append("\r\n");
                    sb.append(getTabStr(1)).append(" */").append("\r\n");
                    sb.append(getTabStr(1)).append("int update(" + name + "DO " + firstToLowerCase(name) + "DO);").append("\r\n");
                    break;
                case "findById":
                    sb.append("\r\n");
                    sb.append(getTabStr(1)).append("/**").append("\r\n");
                    sb.append(getTabStr(1)).append(" * 根据主键查询").append("\r\n");
                    sb.append(getTabStr(1)).append(" * ").append("\r\n");
                    sb.append(getTabStr(1)).append(" * @param " + defineVar(pk)).append("\r\n");
                    sb.append(getTabStr(1)).append(" * @return ").append("\r\n");
                    sb.append(getTabStr(1)).append(" */").append("\r\n");
                    sb.append(getTabStr(1)).append(name + "DO findByCode" + "(String " + defineVar(pk) + ");").append("\r\n");
                    break;
                case "findListByQO":
                    sb.append("\r\n");
                    sb.append(getTabStr(1)).append("/**").append("\r\n");
                    sb.append(getTabStr(1)).append(" * 查询列表").append("\r\n");
                    sb.append(getTabStr(1)).append(" * ").append("\r\n");
                    sb.append(getTabStr(1)).append(" * @param " + firstToLowerCase(name) + "QO").append("\r\n");
                    sb.append(getTabStr(1)).append(" * @return ").append("\r\n");
                    sb.append(getTabStr(1)).append(" */").append("\r\n");
                    sb.append(getTabStr(1)).append("List<" + name + "DO> findListByQO(" + name + "QO " + firstToLowerCase(name) + "QO);").append("\r\n");
                    break;
                case "findListByPage":
                    sb.append("\r\n");
                    sb.append(getTabStr(1)).append("/**").append("\r\n");
                    sb.append(getTabStr(1)).append(" * 分页查询").append("\r\n");
                    sb.append(getTabStr(1)).append(" * ").append("\r\n");
                    sb.append(getTabStr(1)).append(" * @param " + "condition").append("\r\n");
                    sb.append(getTabStr(1)).append(" * @return ").append("\r\n");
                    sb.append(getTabStr(1)).append(" */").append("\r\n");
                    sb.append(getTabStr(1)).append("List<" + name + "DO> findListByPage(PageQueryCondition<" + name + "QO> " + "condition);").append("\r\n");
                    break;
            }
        }

        sb.append("}");

        System.out.println(sb.toString());

        writeFile(sb.toString(), "/dao", className + ".java");
    }

    private String firstToLowerCase(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1, str.length());
    }

    private void createMapperFile(String name, String[] methodNames)
            throws Exception {
        System.out.println("==========Mapper==========");


        String packageOfDAO = "com.lushwe.dal.dao." + name + "DAO";
        String packageOfDO = "com.lushwe.dal.model." + name + "DO";
        String packageOfQO = "com.lushwe.dal.qo." + name + "QO";
        String packageOfCondition = "com.lushwe.model.plugin.page." + "PageQueryCondition";

        StringBuilder sb = new StringBuilder();
        //
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>").append("\r\n");
        sb.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >").append("\r\n");
        sb.append("\r\n");
        // NAMESPACE
        sb.append("<mapper namespace=\"" + packageOfDAO + "\">").append("\r\n");
        sb.append("\r\n");
        // resultMap
        sb.append(getTabStr(1)).append("<resultMap id=\"BaseResultMap\" taskType=\"" + packageOfDO + "\">").append("\r\n");
        for (int i = 0; i < table.columns.length; i++) {
            sb.append(getTabStr(2)).append("<result column=\"" + table.columns[i].name + "\" property=\"" + defineVar(table.columns[i].name) + "\" jdbcType=\"" + table.columns[i].jdbcType.toUpperCase() + "\" />");
            sb.append("\r\n");
        }
        sb.append(getTabStr(1)).append("</resultMap>").append("\r\n");
        sb.append("\r\n");

        // Base_Column_List
        sb.append(getTabStr(1)).append("<sql id=\"Base_Column_List\">").append("\r\n");
        for (int i = 0; i < table.columns.length; i++) {
            String colName = table.columns[i].name;
            if (i < table.columns.length - 1) {
                colName = colName + ",";
            }
            sb.append(getTabStr(2)).append(colName).append("\r\n");
        }
        sb.append(getTabStr(1)).append("</sql>").append("\r\n");
        sb.append("\r\n");

        // Where_Clause_QO
        sb.append(getTabStr(1)).append("<sql id=\"Where_Clause_QO\">").append("\r\n");
        for (int i = 0; i < table.columns.length; i++) {
            if (isDefault(table.columns[i].name)) {
                continue;
            }

            sb.append(getTabStr(2)).append("<if test=\"" + defineVar(table.columns[i].name) + " != null\">").append("\r\n");
            sb.append(getTabStr(3)).append("AND " + table.columns[i].name + " = #{" + defineVar(table.columns[i].name) + "}").append("\r\n");
            sb.append(getTabStr(2)).append("</if>").append("\r\n");
        }
        sb.append(getTabStr(1)).append("</sql>").append("\r\n");
        sb.append("\r\n");

        // Where_Clause
        sb.append(getTabStr(1)).append("<sql id=\"Where_Clause\">").append("\r\n");
        for (int i = 0; i < table.columns.length; i++) {
            if (isDefault(table.columns[i].name)) {
                continue;
            }

            sb.append(getTabStr(2)).append("<if test=\"data." + defineVar(table.columns[i].name) + " != null\">").append("\r\n");
            sb.append(getTabStr(3)).append("AND " + table.columns[i].name + " = #{data." + defineVar(table.columns[i].name) + "}").append("\r\n");
            sb.append(getTabStr(2)).append("</if>").append("\r\n");
        }
        sb.append(getTabStr(1)).append("</sql>").append("\r\n");
        sb.append("\r\n");
        //

        for (String methodName : methodNames) {
            switch (methodName) {
                case "insert":
//                    sb.append(getTabStr(1)).append("<!-- 新增 -->").append("\r\n");
                    sb.append(getTabStr(1)).append("<insert id=\"insert\" parameterType=\"" + packageOfDO + "\">").append("\r\n");
                    sb.append(getTabStr(2)).append("INSERT INTO " + tableName).append("\r\n");

                    sb.append(getTabStr(2)).append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">").append("\r\n");
                    for (int i = 0; i < table.columns.length; i++) {
                        if (isDefault(table.columns[i].name)) {
                            continue;
                        }

                        sb.append(getTabStr(3)).append("<if test=\"" + defineVar(table.columns[i].name) + " != null\">").append("\r\n");
                        sb.append(getTabStr(4)).append(table.columns[i].name).append(",").append("\r\n");
                        sb.append(getTabStr(3)).append("</if>").append("\r\n");
                    }
                    sb.append(getTabStr(2)).append("</trim>").append("\r\n");

                    sb.append(getTabStr(2)).append("<trim prefix=\"VALUES(\" suffix=\")\" suffixOverrides=\",\">").append("\r\n");
                    for (int i = 0; i < table.columns.length; i++) {
                        if (isDefault(table.columns[i].name)) {
                            continue;
                        }

                        String colName = "#{" + defineVar(table.columns[i].name) + "}";

                        sb.append(getTabStr(3)).append("<if test=\"" + defineVar(table.columns[i].name) + " != null\">").append("\r\n");
                        sb.append(getTabStr(4)).append(colName).append(",").append("\r\n");
                        sb.append(getTabStr(3)).append("</if>").append("\r\n");
                    }
                    sb.append(getTabStr(2)).append("</trim>").append("\r\n");

                    sb.append(getTabStr(1)).append("</insert>").append("\r\n");
                    sb.append("\r\n");
                    break;
                case "update":
                    //
//                    sb.append(getTabStr(1)).append("<!-- 更新 -->").append("\r\n");
                    sb.append(getTabStr(1)).append("<update id=\"update\" parameterType=\"" + packageOfDO + "\">").append("\r\n");
                    sb.append(getTabStr(2)).append("UPDATE " + tableName).append("\r\n");
                    sb.append(getTabStr(2)).append("<trim prefix=\"SET\" suffixOverrides=\",\">").append("\r\n");
                    for (int i = 0; i < table.columns.length; i++) {
                        if (isDefault(table.columns[i].name)) {
                            continue;
                        }

                        sb.append(getTabStr(3)).append("<if test=\"" + defineVar(table.columns[i].name) + " != null\">").append("\r\n");
                        sb.append(getTabStr(4)).append(table.columns[i].name + " = #{" + defineVar(table.columns[i].name) + "},").append("\r\n");
                        sb.append(getTabStr(3)).append("</if>").append("\r\n");
                    }

                    sb.append(getTabStr(2)).append("</trim>").append("\r\n");
                    sb.append(getTabStr(2)).append("WHERE del_flag = 0 AND ").append(pk + " = #{" + defineVar(pk) + "}").append("\r\n");
                    sb.append(getTabStr(1)).append("</update>").append("\r\n");
                    sb.append("\r\n");
                    break;
                case "findById":
                    //
//                    sb.append(getTabStr(1)).append("<!-- 查询对象 -->").append("\r\n");
                    sb.append(getTabStr(1)).append("<select id=\"findByCode" + "\" resultMap=\"BaseResultMap\" parameterType=\"string\">").append("\r\n");
                    sb.append(getTabStr(2)).append("SELECT <include refid=\"Base_Column_List\" />").append("\r\n");
                    sb.append(getTabStr(2)).append("  FROM " + tableName).append("\r\n");
                    sb.append(getTabStr(2)).append(" WHERE del_flag = 0 AND ").append(pk + " = #{" + defineVar(pk) + "}").append("\r\n");
                    sb.append(getTabStr(1)).append("</select>").append("\r\n");
                    sb.append("\r\n");
                    break;
                case "findListByQO":
                    //
//                    sb.append(getTabStr(1)).append("<!-- 查询列表 -->").append("\r\n");
                    sb.append(getTabStr(1)).append("<select id=\"findListByQO\" resultMap=\"BaseResultMap\" parameterType=\"" + packageOfQO
                            + "\">").append("\r\n");
                    sb.append(getTabStr(2)).append("SELECT <include refid=\"Base_Column_List\" />").append("\r\n");
                    sb.append(getTabStr(2)).append("  FROM " + tableName).append("\r\n");
                    sb.append(getTabStr(2)).append(" WHERE del_flag = 0 ").append("\r\n");

                    sb.append(getTabStr(2)).append("<if test=\"_parameter != null\">").append("\r\n");
                    sb.append(getTabStr(3)).append("<include refid=\"Where_Clause_QO\"/>").append("\r\n");
                    sb.append(getTabStr(2)).append("</if>").append("\r\n");

                    sb.append(getTabStr(1)).append("</select>").append("\r\n");
                    sb.append("\r\n");
                    break;
                case "findListByPage":
                    //
//                    sb.append(getTabStr(1)).append("<!-- 分页查询列表 -->").append("\r\n");
                    sb.append(getTabStr(1)).append("<select id=\"findListByPage\" resultMap=\"BaseResultMap\" parameterType=\""
                            + packageOfCondition + "\">").append("\r\n");
                    sb.append(getTabStr(2)).append("SELECT <include refid=\"Base_Column_List\" />").append("\r\n");
                    sb.append(getTabStr(2)).append("  FROM " + tableName).append("\r\n");
                    sb.append(getTabStr(2)).append(" WHERE del_flag = 0 ").append("\r\n");

                    sb.append(getTabStr(2)).append("<if test=\"_parameter != null\">").append("\r\n");
                    sb.append(getTabStr(3)).append("<include refid=\"Where_Clause\"/>").append("\r\n");
                    sb.append(getTabStr(2)).append("</if>").append("\r\n");

                    sb.append(getTabStr(1)).append("</select>").append("\r\n");
                    sb.append("\r\n");
                    break;
            }
        }

        //
        sb.append("</mapper>");

        System.out.println(sb.toString());

        writeFile(sb.toString(), "/mapper", name + "Mapper.xml");
    }

    private boolean isDefault(String colName) {
        return ("id".equals(colName) || "del_flag".equals(colName) || "create_time".equals(colName) || "update_time".equals(colName));
    }

    private void writeFile(String content, String path, String fileName) throws IOException {
        File directory = new File(this.path + path);
        String outputPath = directory.getAbsolutePath() + "/" + fileName;
        FileWriter fw = new FileWriter(outputPath);
        PrintWriter pw = new PrintWriter(fw);
        pw.println(content);
        pw.flush();
        pw.close();
    }

    private static String getTabStr(int count) {
        String s = "";
        for (int i = 0; i < count; i++) {
            s = s + "    ";
        }
        return s;
    }

    private static String getJavaType(String colType) {

        switch (colType.toUpperCase()) {
            case "DATETIME":
            case "TIMESTAMP":
                return "Date";

            case "TINYINT":
            case "INTEGER":
                return "Integer";

            case "DOUBLE":
                return "Double";
            case "BIGINT":
                return "Long";

            case "VARCHAR":
            default:
                return "String";
        }
    }

    private static String getJavaTypeOf(String colType) {

        String javaType = getJavaType(colType);

        switch (javaType) {
            case "Date":
                return "java.util." + javaType;

            case "Integer":
            case "Double":
            case "Long":
            case "String":
            default:
                return "java.lang." + javaType;
        }
    }

    /**
     * 功能：生成实体类主体代码
     *
     * @param colnames
     * @param colTypes
     * @param colSizes
     * @return
     */
    private String parse(String className, String[] colnames, String[] colTypes, int[] colSizes) {
        StringBuffer sb = new StringBuffer();

        sb.append("package " + packageOutPath + ";\r\n");
        // 判断是否导入工具包
        if (f_util) {
            sb.append("import java.util.Date;\r\n");
        }
        if (f_sql) {
            sb.append("import java.sql.*;\r\n");
        }
        sb.append("\r\n");
        // // 注释部分
        // sb.append("   /**\r\n");
        // sb.append("    * " + initcap(tablename) + " 实体类\r\n");
        // sb.append("    * " + new Date() + " " + this.authorName + "\r\n");
        // sb.append("    */ \r\n");
        // 实体部分
        sb.append("\r\n\r\npublic class " + className + "{\r\n");
        processAllAttrs(sb);// 属性
        processAllMethod(sb);// get set方法
        sb.append("}\r\n");

        return sb.toString();
    }

    /**
     * 功能：生成所有属性
     *
     * @param sb
     */
    private void processAllAttrs(StringBuffer sb) {
        for (int i = 0; i < table.columns.length; i++) {
            sb.append("\tprivate " + sqlType2JavaType(table.columns[i].jdbcType) + " " + defineVar(table.columns[i].name) + ";\r\n");
        }

    }

    /**
     * 功能：生成所有方法
     *
     * @param sb
     */
    private void processAllMethod(StringBuffer sb) {

        for (int i = 0; i < table.columns.length; i++) {
            sb.append("\n\tpublic void set" + transVar(table.columns[i].name) + "(" + sqlType2JavaType(table.columns[i].jdbcType) + " "
                    + defineVar(table.columns[i].name) + "){\r\n");
            sb.append("\t\tthis." + defineVar(table.columns[i].name) + "=" + defineVar(table.columns[i].name) + ";\r\n");
            sb.append("\t}\r\n");
            sb.append("\n\tpublic " + sqlType2JavaType(table.columns[i].jdbcType) + " get" + transVar(table.columns[i].name) + "(){\r\n");
            sb.append("\t\treturn " + defineVar(table.columns[i].name) + ";\r\n");
            sb.append("\t}\r\n");
        }

    }

    /**
     * 功能：将输入字符串的首字母改成大写
     *
     * @param str
     * @return
     */
    private String initcap(String str) {

        char[] ch = str.toLowerCase().toCharArray();
        System.out.println("6666 str = " + str.toString());
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }

        return new String(ch);
    }

    /**
     * 用于生成get/set方法时 功能：先将变量字母全变为小写，将第一个字母变为大写，将紧跟“_”后面一个字母大写，并去掉“_”.
     *
     * @param str
     * @return
     */
    private String transVar(String str) {
        System.out.println("111" + str);
        int index = 0;
        if (str.indexOf("_") != -1) {
            index = str.indexOf("_");
            str = str.replace("_", "");
        }
        System.out.println("222" + str);
        char[] ch = str.toLowerCase().toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
            if (index != 0) {
                ch[index] = (char) (ch[index] - 32);
            }
        }
        str = new String(ch);
        System.out.println("333" + str);
        return str;
    }

    /**
     * 用于定义变量名 功能：先将变量字母全变为小写，将紧跟“_”后面一个字母大写，并去掉“_”.
     *
     * @param str
     * @return
     */
    private String defineVarOf(String str) {
        int index = 0;
        if (str.indexOf("_") != -1) {
            index = str.indexOf("_");
            str = str.replace("_", "");
        }
        char[] ch = str.toLowerCase().toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z' && index != 0) {
            ch[index] = (char) (ch[index] - 32);
        }
        ch[0] = (char) (ch[0] - 32);
        str = new String(ch);
        return str;
    }

    /**
     * 用于定义变量名 功能：先将变量字母全变为小写，将紧跟“_”后面一个字母大写，并去掉“_”.
     *
     * @param str
     * @return
     */
    private String defineVar(String str) {
//        int index = 0;
//        while (str.indexOf("_") != -1) {
//            index = str.indexOf("_");
//            str = str.replaceFirst("_", "");
//            char[] ch = str.toLowerCase().toCharArray();
//            if (ch[0] >= 'a' && ch[0] <= 'z' && index != 0) {
//                ch[index] = (char) (ch[index] - 32);
//            }
//            str = new String(ch);
//        }
//        return str;

        Pattern linePattern = Pattern.compile("_(\\w)");
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 功能：获得列的数据类型
     *
     * @param sqlType
     * @return
     */
    private String sqlType2JavaType(String sqlType) {

        if (sqlType.equalsIgnoreCase("binary_double")) {
            return "double";
        } else if (sqlType.equalsIgnoreCase("binary_float")) {
            return "float";
        } else if (sqlType.equalsIgnoreCase("blob")) {
            return "byte[]";
        } else if (sqlType.equalsIgnoreCase("blob")) {
            return "byte[]";
        } else if (sqlType.equalsIgnoreCase("char") || sqlType.equalsIgnoreCase("nvarchar2")
                || sqlType.equalsIgnoreCase("varchar2")) {
            return "String";
        } else if (sqlType.equalsIgnoreCase("date") || sqlType.equalsIgnoreCase("timestamp")
                || sqlType.equalsIgnoreCase("timestamp with local time zone")
                || sqlType.equalsIgnoreCase("timestamp with time zone")) {
            return "Date";
        } else if (sqlType.equalsIgnoreCase("number") || sqlType.equalsIgnoreCase("bigint")) {
            return "Long";
        } else if (sqlType.equalsIgnoreCase("tinyint") || sqlType.equalsIgnoreCase("INTEGER")) {
            return "Integer";
        }

        return "String";
    }

    /**
     * 出口 TODO
     *
     * @param args
     */
    public static void main(String[] args) {

        String author = "Jack Liu";
        String version = "0.1";

        String[] tableNameStr = {

                "",
                "",
                "",
                "",
                "",
                ""

        };

        String[] pk = {
                "",
                "",
                "",
                "",
                "",
                ""
        };

        String[] tableNames = {
                "",
                "",
                "",
                "",
                "",
                ""
        };
        String[] ObjNames = {
                "",
                "",
                "",
                "",
                "",
                ""
        };

        String[][] methodNames = {
                {"", "", "", "findListByQO", ""},
                {"insert", "update", "findById", "findListByQO", "findListByPage"},
                {"insert", "update", "findById", "findListByQO", "findListByPage"},
                {"insert", "", "", "findListByQO", "findListByPage"},
                {"insert", "update", "", "findListByQO", "findListByPage"},
                {"", "", "", "findListByQO", "findListByPage"}
        };

        for (int i = 0; i < tableNames.length; i++) {

            System.out.println("=========【" + tableNames[i] + "】=========");

            MySqlEntityGenerator mysql = new MySqlEntityGenerator();
            mysql.author = author;
            mysql.version = version;

            mysql.tableName = tableNames[i];
            mysql.tableNameStr = tableNameStr[i];
            mysql.pk = pk[i];

            try {
                mysql.init();
                mysql.createDOFile(ObjNames[i]);
                mysql.createDTOFile(ObjNames[i]);
                mysql.createQOFile(ObjNames[i]);
                mysql.createDAOFile(ObjNames[i], methodNames[i]);
                mysql.createMapperFile(ObjNames[i], methodNames[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
