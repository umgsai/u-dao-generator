/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.umgsai.dao.generator.data;

import lombok.Data;

import java.util.List;

/**
 *
 * @author shangyidong
 * @version $Id: TreeNode.java, v 0.1 2018年11月17日 上午10:17 shangyidong Exp $
 */
@Data
public class TreeNode {

    private String name;

    private String icon;

    private boolean open;

    private List<TreeNode> children;
}