/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.umgsai.dao.generator.dao;

import com.google.common.collect.Maps;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.umgsai.dao.generator.data.TableColumn;
import lombok.extern.slf4j.Slf4j;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author shangyidong
 * @version $Id: MySQLManager.java, v 0.1 2018年11月16日 下午9:50 shangyidong Exp $
 */
@Slf4j
public class MySQLManager {

    //sql注入待解决
    public static List<TableColumn> getTableColumnList(String host, String port, String username, String password, String dbName, String tableName) {
        String sql = String.format("select * from information_schema.columns where table_schema = '%s' and table_name = '%s';", dbName, tableName);

        Map<String, Object> map = execute(host, port, username, password, dbName, sql);
        ResultSet resultSet = (ResultSet) map.get("resultSet");
        List<TableColumn> tableColumnList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                String columnDataType = resultSet.getString("DATA_TYPE");
                String columnComment = resultSet.getString("COLUMN_COMMENT");
                String columnType = resultSet.getString("COLUMN_TYPE");
                tableColumnList.add(new TableColumn(columnName, columnType, columnDataType, columnComment));
            }
        } catch (SQLException e) {
            log.error("查询表结构异常！", e);
        } finally {
            close((ResultSet) map.get("resultSet"), (PreparedStatement) map.get("preparedStatement"), (Connection) map.get("connection"));
        }
        return tableColumnList;
    }

    public static List<String> getTableNameList(String host, String port, String username, String password, String dbName) {
        String sql = "show tables ; ";
        Map<String, Object> map = execute(host, port, username, password, dbName, sql);
        List<String> tableNameList = new ArrayList<>();
        String columnName = String.format("Tables_in_%s", dbName);
        try {
            ResultSet resultSet = (ResultSet) map.get("resultSet");
            while (resultSet.next()) {
                String tableName = resultSet.getString(columnName);
                tableNameList.add(tableName);
            }
        } catch (SQLException e) {
            log.error("查询表名异常！", e);
        } finally {
            close((ResultSet) map.get("resultSet"), (PreparedStatement) map.get("preparedStatement"), (Connection) map.get("connection"));
        }
        return tableNameList;
    }

    public static List<String> getDbNameList(String host, String port, String username, String password) {
        String sql = "show databases ; ";
        Map<String, Object> map = execute(host, port, username, password, "information_schema", sql);
        List<String> dbNameList = new ArrayList<>();
        try {
            ResultSet resultSet = (ResultSet) map.get("resultSet");
            while (resultSet.next()) {
                String tableName = resultSet.getString("Database");
                dbNameList.add(tableName);
            }
        } catch (SQLException e) {
            log.error("查询DB名异常！", e);
        } finally {
            close((ResultSet) map.get("resultSet"), (PreparedStatement) map.get("preparedStatement"), (Connection) map.get("connection"));
        }
        return dbNameList;
    }

    private static void close(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (Exception e) {
                log.error("关闭resultSet异常", e);
            }
        }
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (Exception e) {
                log.error("关闭preparedStatement异常", e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                log.error("关闭connection异常", e);
            }
        }
    }

    private static Map<String, Object> execute(String host, String port, String username, String password, String dbName, String sql) {
        Map<String, Object> map = Maps.newHashMap();
        Connection connection = getConnection(host, port, username, password, dbName);
        map.put("connection", connection);
        PreparedStatement preparedStatement;
        try {
            if (log.isInfoEnabled()) {
                log.info(String.format("执行SQL:%s", sql));
            }
            preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
            map.put("preparedStatement", preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            map.put("resultSet", resultSet);
        } catch (SQLException e) {
            log.error(String.format("执行SQL异常：%s", sql), e);
        }
        return map;
    }

    private static Connection getConnection(String host, String port, String username, String password, String dbName) {
    //private static Connection getConnection() {
        String driver = "com.mysql.jdbc.Driver";
        String url = String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8", host, port, dbName);

        Connection connection = null;
        try {
            Class.forName(driver);
            connection = (Connection) DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}