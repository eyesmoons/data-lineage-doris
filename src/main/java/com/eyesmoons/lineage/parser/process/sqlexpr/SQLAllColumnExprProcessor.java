package com.eyesmoons.lineage.parser.process.sqlexpr;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.statement.SQLUseStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.parser.process.SqlExprContent;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * select * 的处理
 */
@SQLObjectType(clazz = SQLAllColumnExpr.class)
@Slf4j
public class SQLAllColumnExprProcessor implements SQLExprProcessor {

    @Override
    public void process(String dbType, SQLExpr expr, SqlExprContent content) {
        SQLAllColumnExpr sqlAllColumnExpr = (SQLAllColumnExpr) expr;
        log.info("处理字段为[*]表达式:{}", sqlAllColumnExpr);
        content.addItem(SqlExprContent.builder().name("*").build());
        /*String originSql = findOriginSql(sqlAllColumnExpr.getParent());
        Map<String, TreeMap<String, List<String>>> tableAndField = getTableAndField(originSql, "");
        tableAndField.get("select").forEach((tbl, columns) -> columns.forEach(column -> {
            if (!Objects.equals("*", column)) {
                content.addItem(SqlExprContent.builder().name(column).build());
            }
        }));*/
    }

    private String findOriginSql(SQLObject parent) {
        if (Objects.equals("*", parent.toString())) {
            return findOriginSql(parent.getParent());
        }
        if (Objects.nonNull(parent.getParent())) {
            return findOriginSql(parent.getParent());
        }
        return parent.toString();
    }

    public static Map<String, TreeMap<String, List<String>>> getTableAndField(String sql, String database) {
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        Map<String, TreeMap<String, List<String>>> result = new HashMap<>();

        TreeMap<String, List<String>> selectSet = new TreeMap<>();
        TreeMap<String, List<String>> updateSet = new TreeMap<>();
        TreeMap<String, List<String>> insertSet = new TreeMap<>();
        TreeMap<String, List<String>> deleteSet = new TreeMap<>();

        List<String> updateList = new ArrayList<>();
        List<String> insertList = new ArrayList<>();
        List<String> deletetList = new ArrayList<>();

        for (SQLStatement stmt : stmts) {
            SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(stmts, JdbcConstants.MYSQL);
            if (stmt instanceof SQLUseStatement) {
                database = ((SQLUseStatement) stmt).getDatabase().getSimpleName();
            }
            stmt.accept(statVisitor);
            Map<TableStat.Name, TableStat> tables = statVisitor.getTables();
            Collection<TableStat.Column> columns = statVisitor.getColumns();
            //解析表名，字段
            if (Objects.nonNull(tables)) {
                for (Map.Entry<TableStat.Name, TableStat> table : tables.entrySet()) {
                    TableStat.Name tableName = table.getKey();
                    TableStat stat = table.getValue();
                    if (stat.getCreateCount() > 0 || stat.getInsertCount() > 0) {
                        String insert = tableName.getName();
                        if (insert.contains(".")) {
                            String[] split = insert.split("\\.");
                            insert = split[1];
                        }
                        columns.stream().filter(column -> Objects.equals(column.getTable().toLowerCase(), tableName.getName().toLowerCase())).forEach(column -> {
                            insertList.add(column.getName().toLowerCase());
                        });
                        insertSet.put(insert, insertList);
                    } else if (stat.getSelectCount() > 0) {
                        String select = tableName.getName();
                        if (!select.contains("."))
                            select = database + "." + select;
                        List<String> stringList = new ArrayList<>();
                        for (TableStat.Column column : columns) {
                            if (Objects.equals(column.getTable().toLowerCase(), tableName.getName().toLowerCase())) {
                                stringList.add(column.getName());
                            }
                        }
                        selectSet.put(select, stringList);
                    } else if (stat.getUpdateCount() > 0) {
                        String update = tableName.getName();
                        if (!update.contains("."))
                            update = database + "." + update;
                        columns.stream().filter(column -> Objects.equals(column.getTable().toLowerCase(), tableName.getName().toLowerCase())).forEach(column -> {
                            updateList.add(column.getName().toLowerCase());
                        });
                        updateSet.put(update, updateList);
                    } else if (stat.getDeleteCount() > 0) {
                        String delete = tableName.getName();
                        if (!delete.contains("."))
                            delete = database + "." + delete;
                        columns.stream().filter(column -> Objects.equals(column.getTable().toLowerCase(), tableName.getName().toLowerCase())).forEach(column -> {
                            deletetList.add(column.getName().toLowerCase());
                        });
                        deleteSet.put(delete, deletetList);
                    }
                }
            }
        }

        result.put("select", selectSet);
        result.put("insert", insertSet);
        result.put("update", updateSet);
        result.put("delete", deleteSet);
        return result;
    }
}
