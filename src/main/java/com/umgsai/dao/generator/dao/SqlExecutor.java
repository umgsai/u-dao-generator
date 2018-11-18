/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.umgsai.dao.generator.dao;

import com.mysql.jdbc.PreparedStatement;
import com.umgsai.dao.generator.data.DataResult;

/**
 *
 * @author shangyidong
 * @version $Id: SqlExecutor.java, v 0.1 2018年11月18日 上午11:28 shangyidong Exp $
 */
public interface SqlExecutor {

    DataResult execute(PreparedStatement preparedStatement);
}