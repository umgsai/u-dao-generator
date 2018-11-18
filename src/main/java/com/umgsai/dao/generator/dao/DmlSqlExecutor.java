/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.umgsai.dao.generator.dao;

import com.mysql.jdbc.PreparedStatement;
import com.umgsai.dao.generator.data.DataResult;
import com.umgsai.dao.generator.data.ErrorCode;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author shangyidong
 * @version $Id: DmlSqlExecutor.java, v 0.1 2018年11月18日 上午11:41 shangyidong Exp $
 */
@Slf4j
public class DmlSqlExecutor implements SqlExecutor {

    @Override
    public DataResult execute(PreparedStatement preparedStatement) {
        try {
            int count = preparedStatement.executeUpdate();
            return DataResult.successResult(count);
        } catch (Exception e) {
            String errorMsg = String.format("执行SQL失败！%s", e.getMessage());
            log.error(errorMsg, e);
            return DataResult.failResult(ErrorCode.DML_ERROR.name(), errorMsg);
        }
    }
}