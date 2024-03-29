package com.eyesmoons.lineage.parser.process.statement;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.fastjson.JSONObject;
import com.eyesmoons.lineage.model.parser.ParseTableNode;
import com.eyesmoons.lineage.model.parser.TreeNode;
import com.eyesmoons.lineage.parser.process.ProcessorRegister;
import com.eyesmoons.lineage.parser.process.SqlExprContent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * AbstractStatement
 */
@Slf4j
public abstract class AbstractStatementProcessor implements StatementProcessor {

    @Override
    public void process(String dbType, AtomicInteger sequence, TreeNode<ParseTableNode> root, SQLStatement statement) {
        this.doProcess(dbType, sequence, root, statement);
        this.after(dbType, sequence, root, statement);
    }

    protected void doProcess(String dbType, AtomicInteger sequence, TreeNode<ParseTableNode> root, SQLStatement statement) {}

    protected void constructRootNode(String dbType, TreeNode<ParseTableNode> root, SQLStatement statement, SQLExprTableSource sqlExprTableSource) {
        SQLExpr sqlExpr = sqlExprTableSource.getExpr();
        SqlExprContent content = new SqlExprContent();
        ProcessorRegister.getSQLExprProcessor(sqlExpr.getClass()).process(dbType, sqlExpr, content);
        String tableName = content.getName();
        String dbName = content.getOwner();
        ParseTableNode parseTableNode = ParseTableNode.builder()
                .dbName(dbName)
                .name(tableName)
                .isVirtualTemp(false)
                .build();
        root.setValue(parseTableNode);
        try {
            parseTableNode.setExpression(SQLUtils.toSQLString(statement));
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    protected void after(String dbType, AtomicInteger sequence, TreeNode<ParseTableNode> root, SQLStatement statement) {
        log.debug("解析完成，AST语法树信息：{} \n 源SQL：{}", JSONObject.toJSONString(root), statement);
    }
}
