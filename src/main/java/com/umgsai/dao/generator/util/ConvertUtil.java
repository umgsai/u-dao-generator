/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.umgsai.dao.generator.util;

import com.google.common.base.CaseFormat;
import com.umgsai.dao.generator.config.ConfigData;
import com.umgsai.dao.generator.data.JavaClass;
import com.umgsai.dao.generator.data.TableColumn;
import org.springframework.util.StringUtils;

/**
 *
 * @author shangyidong
 * @version $Id: Converter.java, v 0.1 2018年11月16日 下午10:16 shangyidong Exp $
 */
public class ConvertUtil {

    public static JavaClass convertToJavaClass(TableColumn tableColumn) {
        String javaClassName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableColumn.getColumnName());
        if (!StringUtils.isEmpty(ConfigData.tablePreFix)) {
             javaClassName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableColumn.getColumnName().replace(
                    ConfigData.tablePreFix + "_", ""));
        }
         //String javaClassNameWithFirstLetterLowerCase = "";
         String javaDataType = ConfigData.dataTypeMap.get(tableColumn.getColumnDataType());
         String comment = tableColumn.getColumnComment();
         return new JavaClass(javaClassName, javaDataType, comment);
    }
}