/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.umgsai.dao.generator.controller;

import com.umgsai.dao.generator.dao.MySQLManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author shangyidong
 * @version $Id: DaoGeneratorController.java, v 0.1 2018年11月16日 下午8:51 shangyidong Exp $
 */
@Controller
public class DaoGeneratorController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @ResponseBody
    @RequestMapping("/getTableColumnList")
    public Object getTableColumnList(String host, String port, String username, String password, String dbName, String tableName) {
        return MySQLManager.getTableColumnList(host, port, username, password, dbName, tableName);
        //List<JavaClass> javaClassList = Lists.transform(tableColumnList, new Function<TableColumn, JavaClass>() {
        //    @Nullable
        //    @Override
        //    public JavaClass apply(@Nullable TableColumn tableColumn) {
        //        return ConvertUtil.convertToJavaClass(tableColumn);
        //    }
        //});
        //Map<String, Object> map = Maps.newHashMap();
        //map.put("tableColumnList", tableColumnList);
        //map.put("javaClassList", javaClassList);
        //return map;
    }



    @ResponseBody
    @RequestMapping("/getTableNameList")
    public Object getTableNameList(String host, String port, String username, String password, String dbName) {
        return MySQLManager.getTableNameList(host, port, username, password, dbName);
    }

    @ResponseBody
    @RequestMapping("/getDbNameList")
    public Object getDbNameList(String host, String port, String username, String password) {
        return MySQLManager.getDbNameList(host, port, username, password);
    }


    @ResponseBody
    @RequestMapping("/exeSql")
    public Object exeSql(String host, String port, String username, String password, String dbName, String sql) {
        return MySQLManager.executeSql(host, port, username, password, dbName, sql);
    }

}