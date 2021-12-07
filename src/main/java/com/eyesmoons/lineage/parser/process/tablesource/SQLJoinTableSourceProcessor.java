package com.eyesmoons.lineage.parser.process.tablesource;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.eyesmoons.lineage.parser.anotation.SQLObjectType;
import com.eyesmoons.lineage.parser.contants.ParserConstant;
import com.eyesmoons.lineage.parser.model.TableNode;
import com.eyesmoons.lineage.parser.model.TreeNode;
import com.eyesmoons.lineage.parser.process.ProcessorRegister;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * SQLJoinTableSource
 * eg:
 * table1
 * LEFT JOIN
 * (SELECT t2.a1, t2.a2 FROM table2 t2) temp1
 * ON
 * t1.a1 = temp1.a1
 */
@SQLObjectType(clazz = SQLJoinTableSource.class)
public class SQLJoinTableSourceProcessor implements TableSourceProcessor {

    @Override
    public void process(String dbType, AtomicInteger sequence, TreeNode<TableNode> parent, SQLTableSource sqlTableSource) {
        TableNode proxyTable = TableNode.builder()
                .isVirtualTemp(true)
                .expression(SQLUtils.toSQLString(sqlTableSource))
                .name(ParserConstant.TEMP_TABLE_PREFIX + sequence.incrementAndGet())
                .alias(sqlTableSource.getAlias())
                .build();

        TreeNode<TableNode> proxyNode = TreeNode.of(proxyTable);
        parent.addChild(proxyNode);

        SQLJoinTableSource sqlJoinTableSource = (SQLJoinTableSource) sqlTableSource;
        ProcessorRegister.getTableSourceProcessor(sqlJoinTableSource.getLeft().getClass()).process(dbType, sequence, proxyNode, sqlJoinTableSource.getLeft());
        ProcessorRegister.getTableSourceProcessor(sqlJoinTableSource.getRight().getClass()).process(dbType, sequence, proxyNode, sqlJoinTableSource.getRight());
    }
}
