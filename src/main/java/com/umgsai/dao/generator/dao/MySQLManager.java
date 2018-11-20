/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.umgsai.dao.generator.dao;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.umgsai.dao.generator.data.DataResult;
import com.umgsai.dao.generator.data.ErrorCode;
import com.umgsai.dao.generator.data.SqlType;
import com.umgsai.dao.generator.data.TableColumn;
import com.umgsai.dao.generator.util.SqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    public static DataResult<List<TableColumn>> getTableColumnList(String host, String port, String username, String password,
                                                                   String dbName, String tableName) {
        Map<String, Object> map = Maps.newHashMap();

        try {
            String sql = String.format("select * from information_schema.columns where table_schema = '%s' and table_name = '%s';", dbName,
                    tableName);
            DataResult dataResult = execute(host, port, username, password, dbName, sql);
            if (!dataResult.isSuccess()) {
                return dataResult;
            }
            map.putAll((Map<String, Object>) dataResult.getData());
            ResultSet resultSet = (ResultSet) map.get("resultSet");
            List<TableColumn> tableColumnList = new ArrayList<>();
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                String columnDataType = resultSet.getString("DATA_TYPE");
                String columnComment = resultSet.getString("COLUMN_COMMENT");
                String columnType = resultSet.getString("COLUMN_TYPE");
                tableColumnList.add(new TableColumn(columnName, columnType, columnDataType, columnComment));
            }
            return DataResult.successResult(tableColumnList);
        } catch (SQLException e) {
            String errorMsg = String.format("查询表结构异常！dbName=%s，tableName=%s，%s", dbName, tableName, e.getMessage());
            log.error(errorMsg, e);
            return DataResult.failResult("", errorMsg);
        } finally {
            close((ResultSet) map.get("resultSet"), (PreparedStatement) map.get("preparedStatement"), (Connection) map.get("connection"));
        }
    }

    public static DataResult<List<String>> getTableNameList(String host, String port, String username, String password, String dbName) {
        Map<String, Object> map = Maps.newHashMap();

        try {
            String sql = "show tables ; ";
            DataResult dataResult = execute(host, port, username, password, dbName, sql);
            if (!dataResult.isSuccess()) {
                return dataResult;
            }
            map.putAll((Map<String, Object>) dataResult.getData());
            List<String> tableNameList = new ArrayList<>();
            String columnName = String.format("Tables_in_%s", dbName);
            ResultSet resultSet = (ResultSet) map.get("resultSet");
            while (resultSet.next()) {
                String tableName = resultSet.getString(columnName);
                tableNameList.add(tableName);
            }
            return DataResult.successResult(tableNameList);
        } catch (SQLException e) {
            String errorMsg = String.format("查询表名异常！%s", e.getMessage());
            log.error(errorMsg, e);
            return DataResult.failResult("", errorMsg);
        } finally {
            close((ResultSet) map.get("resultSet"), (PreparedStatement) map.get("preparedStatement"), (Connection) map.get("connection"));
        }
    }

    public static DataResult<List<String>> getDbNameList(String host, String port, String username, String password) {
        Map<String, Object> map = Maps.newHashMap();
        try {
            String sql = "show databases ; ";
            DataResult dataResult = execute(host, port, username, password, "", sql);
            if (!dataResult.isSuccess()) {
                return dataResult;
            }
            map.putAll((Map<String, Object>) dataResult.getData());
            List<String> dbNameList = new ArrayList<>();
            ResultSet resultSet = (ResultSet) map.get("resultSet");
            while (resultSet.next()) {
                String tableName = resultSet.getString("Database");
                dbNameList.add(tableName);
            }
            return DataResult.successResult(dbNameList);
        } catch (SQLException e) {
            String errorMsg = String.format("查询数据库列表异常！%s", e.getMessage());
            log.error(errorMsg, e);
            return DataResult.failResult("", errorMsg);
        } finally {
            close((ResultSet) map.get("resultSet"), (PreparedStatement) map.get("preparedStatement"), (Connection) map.get("connection"));
        }
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

    public static DataResult executeSql(String host, String port, String username, String password, String dbName,
                                        String sql) {
        Map<String, Object> map = Maps.newHashMap();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            SqlType sqlType = SqlUtil.getSqlType(sql);
            DataResult dataResult = execute(host, port, username, password, dbName, sql);
            if (!dataResult.isSuccess()) {
                return dataResult;
            }
            map = (Map<String, Object>) dataResult.getData();
            Map<String, Object> resultMap = Maps.newHashMap();
            switch (sqlType) {
                case DQL:
                    ResultSet resultSet = (ResultSet) map.get("resultSet");
                    //获取列名
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    List<String> columnNameList = Lists.newArrayList();
                    //int columnCount = 0;
                    for (int i = 0; i < columnCount; i++) {
                        columnNameList.add(metaData.getColumnName(i + 1));
                        //columnCount++;
                    }
                    resultSet.last();
                    int rowCount = resultSet.getRow();
                    Object data[][] = new Object[rowCount][columnCount];
                    int row = 0;
                    int column = 0;
                    resultSet.first();
                    do {
                        for (int i = 0; i < columnCount; i++) {
                            Object o = resultSet.getObject(i + 1);
                            if (o instanceof Date) {
                                data[row][i] = simpleDateFormat.format(o);
                            } else {
                                data[row][i] = o;
                            }
                        }
                        row++;
                    }while (resultSet.next());
                    //if (log.isInfoEnabled()) {
                    //    log.info(String.format(JSON.toJSONString(data)));
                    //}
                    //Map<String, Object> resultMap = Maps.newHashMap();
                    resultMap.put("columnNameList", columnNameList);
                    resultMap.put("data", data);
                    return DataResult.successResult(resultMap);
                case DML:
                    int count = (int) map.get("count");
                    resultMap.put("sqlType", sqlType);
                    resultMap.put("count", count);
                    resultMap.put("message", String.format("执行成功，影响行数：%d", count));
                    return DataResult.successResult(resultMap);
                case DCL:

                    break;

                case DDL:
                    break;
                case UNKNOMN:
                default:
                    return DataResult.failResult(ErrorCode.UNKNOWN_SQL_TYPE.name(), "不支持的SQL类型");
            }

            if (map.get("count") != null) {
                //insert update delete

            } else if (map.get("success") != null) {
                //除select insert update delete 之外的语句
            }
            List<String> dbNameList = new ArrayList<>();
            ResultSet resultSet = (ResultSet) map.get("resultSet");
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                log.info(resultSetMetaData.getColumnName(i));//id
                log.info(resultSetMetaData.getColumnType(i) + "");
                log.info(resultSetMetaData.getColumnType(i) + "");//java.sql.Types
                log.info(resultSetMetaData.getCatalogName(i));//sofatest
                log.info(resultSetMetaData.getColumnClassName(i));//java.lang.Integer
                log.info(resultSetMetaData.getColumnDisplaySize(i) + "");//1
                log.info(resultSetMetaData.getColumnLabel(i) + "");//id
                log.info(resultSetMetaData.getColumnTypeName(i) + "");//INT
                log.info(resultSetMetaData.getPrecision(i) + "");//11
                log.info(resultSetMetaData.getScale(i) + "");//0
                log.info(resultSetMetaData.getSchemaName(i));//
                log.info(resultSetMetaData.getTableName(i));//sofa_test
            }
            while (resultSet.next()) {
                String tableName = resultSet.getString("Database");
                dbNameList.add(tableName);
            }
            return DataResult.successResult(dbNameList);
        } catch (SQLException e) {
            String errorMsg = String.format("查询数据库列表异常！%s", e.getMessage());
            log.error(errorMsg, e);
            return DataResult.failResult("", errorMsg);
        } finally {
            close((ResultSet) map.get("resultSet"), (PreparedStatement) map.get("preparedStatement"), (Connection) map.get("connection"));
        }
    }

    private static DataResult<Map<String, Object>> execute(String host, String port, String username, String password, String dbName,
                                                           String sql) {
        Map<String, Object> map = Maps.newHashMap();
        DataResult dataResult = getConnection(host, port, username, password, dbName);
        if (!dataResult.isSuccess()) {
            return dataResult;
        }
        Connection connection = (Connection) dataResult.getData();
        map.put("connection", connection);
        PreparedStatement preparedStatement;
        //ResultSet resultSet;

        SqlType sqlType = SqlUtil.getSqlType(sql);
        try {
            if (log.isInfoEnabled()) {
                log.info(String.format("执行SQL:%s", sql));
            }
            preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
            String sqlWithLowerCase = StringUtils.lowerCase(sql);
            map.put("preparedStatement", preparedStatement);
            SqlExecutor sqlExecutor = null;
            switch (sqlType) {
                case DQL:
                    sqlExecutor = new DqlSqlExecutor();
                    DataResult dqlResult = sqlExecutor.execute(preparedStatement);
                    if (!dqlResult.isSuccess()) {
                        return dqlResult;
                    }
                    map.put("resultSet", dqlResult.getData());
                    return DataResult.successResult(map);
                case DML:
                    sqlExecutor = new DmlSqlExecutor();
                    DataResult dmlResult = sqlExecutor.execute(preparedStatement);
                    if (!dmlResult.isSuccess()) {
                        return dmlResult;
                    }
                    map.put("count", dmlResult.getData());
                    return DataResult.successResult(map);
                case DCL:

                    break;

                case DDL:
                    break;
                case UNKNOMN:
                default:
                    return DataResult.failResult(ErrorCode.UNKNOWN_SQL_TYPE.name(), "不支持的SQL类型");
            }

            if (StringUtils.startsWith(sqlWithLowerCase, "insert ") || StringUtils.startsWith(sqlWithLowerCase, "update ") || StringUtils
                    .startsWith(sqlWithLowerCase, "delete ")) {
                int count = preparedStatement.executeUpdate();
                map.put("count", count);
                return DataResult.successResult(map);
            }
            boolean success = preparedStatement.execute();
            map.put("success", success);
            return DataResult.successResult(map);
        } catch (SQLException e) {
            String errorMsg = String.format("执行SQL异常：%s。%s", sql, e.getMessage());
            log.error(errorMsg, e);
            return DataResult.failResult("", errorMsg);
        }

    }

    private static DataResult<Connection> getConnection(String host, String port, String username, String password, String dbName) {
        String driver = "com.mysql.jdbc.Driver";
        String url = String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8", host, port, dbName);
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = (Connection) DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            String errorMsg = String.format("建立数据库连接失败！%s", e.getMessage());
            log.error(errorMsg, e);
            return DataResult.failResult("", errorMsg);
        }
        return DataResult.successResult(connection);
    }
}