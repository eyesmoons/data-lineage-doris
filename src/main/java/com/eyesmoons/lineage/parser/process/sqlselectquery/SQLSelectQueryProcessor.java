package com.eyesmoons.lineage.parser.process.sqlselectquery;

import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.eyesmoons.lineage.parser.model.TableNode;
import com.eyesmoons.lineage.parser.model.TreeNode;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * SQLSelectQuery 处理
 */
public interface SQLSelectQueryProcessor {

    /**
     * SQLSelectQuery 处理
     * @param dbType         数据库类型
     * @param sequence       节点主键
     * @param parent         传入的节点
     * @param sqlSelectQuery SQLSelectQuery子类
     */
    void process(String dbType, AtomicInteger sequence, TreeNode<TableNode> parent, SQLSelectQuery sqlSelectQuery);

}
