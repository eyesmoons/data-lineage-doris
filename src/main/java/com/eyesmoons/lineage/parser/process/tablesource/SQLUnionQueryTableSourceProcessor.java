package com.eyesmoons.lineage.parser.process.tablesource;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQueryTableSource;
import com.alibaba.druid.sql.parser.ParserException;
import com.eyesmoons.lineage.parser.anotation.SQLObjectType;
import com.eyesmoons.lineage.parser.model.ColumnNode;
import com.eyesmoons.lineage.parser.model.TableNode;
import com.eyesmoons.lineage.parser.model.TreeNode;
import com.eyesmoons.lineage.parser.process.ProcessorRegister;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SQLUnionQueryTableSource
 * eg:
 * select t.a from (
 * select t1.a1 from table1 t1
 * union
 * select t2.b from table2 t2
 * union all
 * select t3.c from table3 t3
 * )t
 */
@SQLObjectType(clazz = SQLUnionQueryTableSource.class)
public class SQLUnionQueryTableSourceProcessor implements TableSourceProcessor {

    @Override
    public void process(String dbType, AtomicInteger sequence, TreeNode<TableNode> parent, SQLTableSource sqlTableSource) {
        SQLUnionQueryTableSource sqlUnionQueryTableSource = (SQLUnionQueryTableSource) sqlTableSource;
        // union的特殊处理
        // 提取获取后面的union字段，
        // 提取为新的column 和 source
        String alias = sqlUnionQueryTableSource.getAlias();
        if (StringUtils.isEmpty(alias)) {
            throw new ParserException("别名不能为空!");
        }
        List<SQLSelectQuery> relations = sqlUnionQueryTableSource.getUnion().getRelations();
        // 通过union子查询构建字段来源，解析一个子查询拿到字段即可，然后解析出对应的表
        TreeNode<TableNode> temp = new TreeNode<>();
        relations.forEach(sqlSelectQuery -> ProcessorRegister.getSQLSelectQueryProcessor(sqlSelectQuery.getClass()).process(dbType, sequence, temp, sqlSelectQuery));
        //  前提：第一层就能解析到真正的表
        List<TreeNode<TableNode>> childList = temp.getChildList();
        // 列名肯定是相同的，取第一个构建列名
        TreeNode<TableNode> lineageTableTreeNode = childList.get(0);
        // 构建union字段
        List<ColumnNode> unionColumnList = new ArrayList<>();
        lineageTableTreeNode.getValue().getColumns().forEach(lineageColumn -> {
            String columnName = Optional.ofNullable(lineageColumn.getAlias()).orElse(lineageColumn.getName());
            unionColumnList.add(ColumnNode.builder().name(columnName).tableName(alias).build());
        });

        // 构建union字段的来源字段
        for (TreeNode<TableNode> child : childList) {
            TableNode value = child.getValue();
            List<ColumnNode> columns = value.getColumns();
            String tableName = Optional.ofNullable(value.getAlias()).orElse(value.getName());
            for (int i = 0; i < columns.size(); i++) {
                ColumnNode column = columns.get(i);
                ColumnNode newColumn = ColumnNode.builder()
                        .alias(Optional.ofNullable(column.getAlias()).orElse(column.getName()))
                        .expression(column.getExpression())
                        .tableName(tableName).build();
                unionColumnList.get(i).getSourceColumns().add(newColumn);
            }
        }
        TableNode lineageTable = new TableNode();
        lineageTable.setAlias(alias);
        lineageTable.getColumns().addAll(unionColumnList);
        lineageTable.setExpression(SQLUtils.toSQLString(sqlUnionQueryTableSource));
        TreeNode<TableNode> proxyNode = new TreeNode<>();
        proxyNode.setValue(lineageTable);
        parent.addChild(proxyNode);
        // 子查询继续递归
        relations.forEach(item ->ProcessorRegister.getSQLSelectQueryProcessor(item.getClass()).process(dbType, sequence, proxyNode, item));
    }
}
