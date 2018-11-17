/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.umgsai.dao.generator.config;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 *
 * @author shangyidong
 * @version $Id: ConfigData.java, v 0.1 2018年11月16日 下午10:18 shangyidong Exp $
 */
public class ConfigData {

    public static String dbName = "";


    public static final String tablePreFix = "";

    public static Map<String, String> dataTypeMap = Maps.newHashMap();

    static {
        dataTypeMap.put("bigint", "long");
        dataTypeMap.put("int", "int");
        dataTypeMap.put("smallint", "int");
        dataTypeMap.put("varchar", "String");
        dataTypeMap.put("text", "String");
        dataTypeMap.put("mediumtext", "String");
        dataTypeMap.put("longtext", "String");
        dataTypeMap.put("char", "String");
        dataTypeMap.put("longblob", "String");
        dataTypeMap.put("datetime", "Date");
        dataTypeMap.put("date", "Date");
        dataTypeMap.put("timestamp", "Date");
        dataTypeMap.put("tinyint", "boolean");
        dataTypeMap.put("double", "double");
        dataTypeMap.put("decimal", "double");
        dataTypeMap.put("float", "float");
    }
}