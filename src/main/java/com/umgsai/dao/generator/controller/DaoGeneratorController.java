/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.umgsai.dao.generator.controller;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.umgsai.dao.generator.dao.MySQLManager;
import com.umgsai.dao.generator.data.JavaClass;
import com.umgsai.dao.generator.data.TableColumn;
import com.umgsai.dao.generator.util.ConvertUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

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
        List<TableColumn> tableColumnList = MySQLManager.getTableColumnList(host, port, username, password, dbName, tableName);
        return tableColumnList;
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
}