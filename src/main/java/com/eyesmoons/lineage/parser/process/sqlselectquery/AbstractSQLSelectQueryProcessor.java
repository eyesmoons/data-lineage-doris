package com.eyesmoons.lineage.parser.process.sqlselectquery;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.eyesmoons.lineage.parser.model.TableNode;
import com.eyesmoons.lineage.parser.model.TreeNode;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * SQLSelectQuery
 */
public abstract class AbstractSQLSelectQueryProcessor implements SQLSelectQueryProcessor {

    @Override
    public void process(String dbType, AtomicInteger sequence, TreeNode<TableNode> parent, SQLSelectQuery sqlSelectQuery) {}

    protected String getSubQueryTableSourceAlias(SQLObject sqlObject) {
        SQLObject parentObject = sqlObject.getParent().getParent();
        if (sqlObject.getParent() == null || parentObject == null) {
            return null;
        }
        if (parentObject instanceof SQLSubqueryTableSource) {
            SQLSubqueryTableSource sqlSubqueryTableSource = (SQLSubqueryTableSource) parentObject;
            return sqlSubqueryTableSource.getAlias();
        } else if (parentObject instanceof SQLSelectStatement
                || parentObject instanceof SQLCreateTableStatement) {
            throw new UnsupportedOperationException(parentObject.getClass().getName());
        } else {
            return null;
        }
    }
}
