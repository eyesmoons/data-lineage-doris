package com.eyesmoons.lineage.parser.process.tablesource;

import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.eyesmoons.lineage.model.parser.ParseTableNode;
import com.eyesmoons.lineage.model.parser.TreeNode;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * TableSource 处理
 */
public interface TableSourceProcessor {

    /**
     * TableSource 的处理
     * @param dbType         数据库类型
     * @param sequence       序列
     * @param parent         父节点
     * @param sqlTableSource SQLTableSource 子类
     */
    void process(String dbType, AtomicInteger sequence, TreeNode<ParseTableNode> parent, SQLTableSource sqlTableSource);
}
