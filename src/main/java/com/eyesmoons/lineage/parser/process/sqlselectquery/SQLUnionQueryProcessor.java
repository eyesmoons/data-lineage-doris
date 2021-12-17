package com.eyesmoons.lineage.parser.process.sqlselectquery;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import com.eyesmoons.lineage.contants.ParserConstant;
import com.eyesmoons.lineage.model.parser.ParseTableNode;
import com.eyesmoons.lineage.model.parser.TreeNode;
import com.eyesmoons.lineage.parser.process.ProcessorRegister;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SQLUnionQuery 处理
 * use case:
 * create view view_test as
 * select a1,a2,a3 from table_a
 * union
 * select b1,b2,b3 from table_b
 * union all
 * select c1,c2,c3 from table_c
 */
@SQLObjectType(clazz = SQLUnionQuery.class)
@Slf4j
public class SQLUnionQueryProcessor extends AbstractSQLSelectQueryProcessor {

    @Override
    public void process(String dbType, AtomicInteger sequence, TreeNode<ParseTableNode> parent, SQLSelectQuery sqlSelectQuery) {
        ParseTableNode proxyTable = ParseTableNode.builder()
                .isVirtualTemp(true)
                .expression(SQLUtils.toSQLString(sqlSelectQuery))
                .name(ParserConstant.TEMP_TABLE_PREFIX + sequence.incrementAndGet())
                .alias(this.getSubQueryTableSourceAlias(sqlSelectQuery))
                .queryType(ParserConstant.DealType.TABLE_QL_UNION_QUERY)
                .build();
        TreeNode<ParseTableNode> proxyNode = TreeNode.of(proxyTable);
        parent.addChild(proxyNode);
        // TODO 考虑字段合并到 proxyTable 的字段
        List<SQLSelectQuery> selectQueryList = ((SQLUnionQuery) sqlSelectQuery).getRelations();
        if (CollectionUtils.isNotEmpty(selectQueryList)) {
            selectQueryList.forEach(item -> ProcessorRegister.getSQLSelectQueryProcessor(item.getClass()).process(dbType, sequence, proxyNode, item));
        }
    }
}
