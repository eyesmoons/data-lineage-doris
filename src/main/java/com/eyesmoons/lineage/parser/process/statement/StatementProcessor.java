package com.eyesmoons.lineage.parser.process.statement;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.eyesmoons.lineage.model.parser.ParseTableNode;
import com.eyesmoons.lineage.model.parser.TreeNode;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Statement 语句解析。
 * 判断属于哪种模式的语句后进行处理
 * eg:
 * MySqlInsertStatement: mysql insert
 * SQLCreateViewStatement: create view as
 */
public interface StatementProcessor {

    /**
     * SQLStatement 处理
     * @param dbType    数据库类型
     * @param sequence  序列
     * @param root      当前表节点
     * @param statement SQLStatement
     */
    void process(String dbType, AtomicInteger sequence, TreeNode<ParseTableNode> root, SQLStatement statement);
}
