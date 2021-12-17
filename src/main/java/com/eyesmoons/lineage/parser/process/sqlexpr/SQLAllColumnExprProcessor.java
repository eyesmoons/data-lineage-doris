package com.eyesmoons.lineage.parser.process.sqlexpr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.fastjson.JSONObject;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.parser.process.SqlExprContent;
import com.eyesmoons.lineage.utils.DorisJdbcUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * select * 的处理，
 * 查询元数据
 */
@SQLObjectType(clazz = SQLAllColumnExpr.class)
@Slf4j
public class SQLAllColumnExprProcessor implements SQLExprProcessor {

    private static final String hostUrl = "172.22.224.101:6033";

    private static final String db = "tms";

    private static final String user = "shengyu";

    private static final String password = "j1sYxLGcEDhu";

    @Override
    public void process(String dbType, SQLExpr expr, SqlExprContent content) {
        SQLAllColumnExpr sqlAllColumnExpr = (SQLAllColumnExpr) expr;
        String dbTable = ((MySqlSelectQueryBlock) sqlAllColumnExpr.getParent().getParent()).getFrom().toString();
        log.info("查询元数据：[{}]", dbTable);
        List<JSONObject> resultColumns = DorisJdbcUtil.executeQuery(hostUrl, db, user, password, "desc " + dbTable.split("\\.")[0] + "." + dbTable.split("\\.")[1]);
        for (JSONObject column : resultColumns) {
            String field = column.getString("Field");
            if (StringUtils.isNotBlank(field)) {
                content.addItem(SqlExprContent.builder().name(field).build());
            }
        }
    }
}
