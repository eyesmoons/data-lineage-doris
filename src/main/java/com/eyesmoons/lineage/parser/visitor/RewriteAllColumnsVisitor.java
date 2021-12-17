package com.eyesmoons.lineage.parser.visitor;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.fastjson.JSONObject;
import com.eyesmoons.lineage.utils.DorisJdbcUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
public class RewriteAllColumnsVisitor extends MySqlASTVisitorAdapter {

    private static final String hostUrl = "172.22.224.101:6033";
    private static final String user = "shengyu";
    private static final String password = "j1sYxLGcEDhu";

    @Override
    public boolean visit(MySqlInsertStatement statement) {
        handleSelectQuery(statement.getQuery().getQuery());
        return true;
    }

    private void handleSelectQuery(SQLSelectQuery query) {
        if (query instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock) query;
            SQLTableSource source = selectQueryBlock.getFrom();
            // SQLExprTableSource 直接改写
            if (source instanceof SQLExprTableSource) {
                handleSQLExprTableSource(selectQueryBlock, (SQLExprTableSource) source);
            } else {
                rewriteAllColumnExpr(source);
            }
        } else if (query instanceof SQLUnionQuery) {
            List<SQLSelectQuery> relations = ((SQLUnionQuery) query).getRelations();
            if (CollectionUtils.isNotEmpty(relations)) {
                relations.forEach(this::handleSelectQuery);
            }
        }
    }

    private void rewriteAllColumnExpr(SQLTableSource source) {
        // SQLSubQueryTableSource
        if (source instanceof SQLSubqueryTableSource) {
            SQLSubqueryTableSource subSource = (SQLSubqueryTableSource) source;
            SQLSelectQuery selectQuery = subSource.getSelect().getQuery();
            SQLTableSource tableSource = ((MySqlSelectQueryBlock) selectQuery).getFrom();
            // 继续递归，直到没有join
            rewriteAllColumnExpr(tableSource);
            // 找到最终的子查询，查询元数据，改写字段为(*)的语句
            SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock) selectQuery;
            SQLTableSource from = selectQueryBlock.getFrom();
            if (from instanceof SQLExprTableSource) {
                handleSQLExprTableSource(selectQueryBlock, (SQLExprTableSource) from);
            }
        // SQLJoinTableSource
        } else if (source instanceof SQLJoinTableSource) {
            SQLJoinTableSource joinTableSource = (SQLJoinTableSource) source;
            rewriteAllColumnExpr(joinTableSource.getLeft());
            rewriteAllColumnExpr(joinTableSource.getRight());
        // SQLUnionQueryTableSource
        } else if (source instanceof SQLUnionQueryTableSource) {
            SQLUnionQueryTableSource unionQueryTableSource = (SQLUnionQueryTableSource) source;
            List<SQLSelectQuery> relations = unionQueryTableSource.getUnion().getRelations();
            if (CollectionUtils.isNotEmpty(relations)) {
                relations.forEach(this::handleSelectQuery);
            }
        }
    }

    private void handleSQLExprTableSource(SQLSelectQueryBlock selectQueryBlock, SQLExprTableSource source) {
        List<SQLSelectItem> selectList = selectQueryBlock.getSelectList();
        // 如果查询字段为[*]
        if (selectList.size() == 1 && Objects.equals("*", selectList.get(0).getExpr().toString())) {
            String dbNme = source.getSchema();
            String tableName = source.getTableName();
            String alias = source.getAlias();
            log.info("查询元数据：[{}]", dbNme + "." + tableName);
            List<JSONObject> resultColumns = DorisJdbcUtil.executeQuery(hostUrl, dbNme, user, password, "desc " + dbNme + "." + tableName);
            selectList.removeIf(sqlSelectItem -> Objects.equals("*", sqlSelectItem.getExpr().toString()));
            for (JSONObject column : resultColumns) {
                String field = column.getString("Field");
                if (StringUtils.isBlank(alias)) {
                    selectQueryBlock.addSelectItem(new SQLIdentifierExpr(field));
                } else {
                    selectQueryBlock.addSelectItem(new SQLPropertyExpr(alias, field));
                }
            }
        }
    }
}
