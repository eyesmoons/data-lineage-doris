package com.eyesmoons.lineage.parser.process.tablesource;

import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.eyesmoons.lineage.parser.anotation.SQLObjectType;
import com.eyesmoons.lineage.parser.model.ParseTableNode;
import com.eyesmoons.lineage.parser.model.TreeNode;
import com.eyesmoons.lineage.parser.process.ProcessorRegister;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * SQLSubQueryTableSource
 * eg: select t1.a1 from (select a1 from table1) t1
 */
@SQLObjectType(clazz = SQLSubqueryTableSource.class)
public class SQLSubQueryTableSourceProcessor implements TableSourceProcessor {

    @Override
    public void process(String dbType, AtomicInteger sequence, TreeNode<ParseTableNode> parent, SQLTableSource sqlTableSource) {
        SQLSelectQuery sqlSelectQuery = ((SQLSubqueryTableSource) sqlTableSource).getSelect().getQuery();
        ProcessorRegister.getSQLSelectQueryProcessor(sqlSelectQuery.getClass()).process(dbType, sequence, parent, sqlSelectQuery);
    }
}
