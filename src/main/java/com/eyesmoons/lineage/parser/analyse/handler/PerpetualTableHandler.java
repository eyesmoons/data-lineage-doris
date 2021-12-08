package com.eyesmoons.lineage.parser.analyse.handler;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.eyesmoons.lineage.parser.analyse.SqlRequestContext;
import com.eyesmoons.lineage.parser.analyse.SqlResponseContext;
import com.eyesmoons.lineage.parser.constant.PriorityConstants;
import com.eyesmoons.lineage.parser.model.ParseTableNode;
import com.eyesmoons.lineage.parser.model.TreeNode;
import com.eyesmoons.lineage.parser.process.ProcessorRegister;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 1. 表血缘的解析
 */
@Order(PriorityConstants.LITTLE_HIGH)
@Component
public class PerpetualTableHandler implements IHandler {

    @Override
    public void handleRequest(SqlRequestContext request, SqlResponseContext response) {
        verify(request);
        handleTableRelation(request, response);
    }

    public void handleTableRelation(SqlRequestContext sqlContext, SqlResponseContext response) {
        AtomicInteger sequence = new AtomicInteger();
        TreeNode<ParseTableNode> root = new TreeNode<>();
        SQLStatement statement;
        try {
            statement = SQLUtils.parseSingleStatement(sqlContext.getSql(), sqlContext.getDbType().toLowerCase());
        } catch (Exception e) {
            throw new ParserException("statement parser error：", e);
        }
        response.setStatementType(statement.getDbType().getClass().getSimpleName().toUpperCase());
        // 处理
        ProcessorRegister.getStatementProcessor(statement.getClass()).process(sqlContext.getDbType(), sequence, root, statement);
        // save
        response.setLineageTableTree(root);
    }

    private void verify(SqlRequestContext sqlContext) {
        if (Objects.isNull(sqlContext)) {
            throw new ParserException("sql is null");
        }
        if (StringUtils.isEmpty(sqlContext.getSql())) {
            throw new ParserException("sql content is empty");
        }
        if (StringUtils.isEmpty(sqlContext.getDbType())) {
            throw new ParserException("sql type is empty");
        }
    }
}
