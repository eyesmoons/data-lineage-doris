package com.eyesmoons.lineage.parser.tracer;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.stat.TableStat;
import com.eyesmoons.lineage.exception.ParserException;
import com.eyesmoons.lineage.model.parser.ParseColumnNode;
import com.eyesmoons.lineage.model.parser.ParseTableNode;
import com.eyesmoons.lineage.model.parser.TreeNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 默认字段血缘生成逻辑
 */
@Slf4j
public class DefaultColumnLineageTracer implements ColumnLineageTracer {

    private static final String hostUrl = "172.22.224.101:6033";

    private static final String db = "tms";

    private static final String user = "shengyu";

    private static final String password = "j1sYxLGcEDhu";

    /**
     * Long tableId 节点ID
     * List<TreeNode<TableNode>> 某一节点最近的节点
     */
    private final Map<Long, List<TreeNode<ParseTableNode>>> recentTreeNodeMap = new HashMap<>();

    /**
     * 构建字段来源的字段血缘
     *
     * @param currentColumnNode 当前的字段节点
     * @param tableNode         表节点
     */
    @Override
    public void traceColumnLineageTree(String dbType, TreeNode<ParseColumnNode> currentColumnNode, TreeNode<ParseTableNode> tableNode) {
        // 当前字段向下检索列的来源, 后面需定位当前列所在的节点
        ParseColumnNode currentColumn = currentColumnNode.getValue();
        // 根据AST构造关系，这里来源表最多一层，所以判断来源是否有值，如果有值，那么以来源字段构建检索
        if (CollectionUtils.isNotEmpty(currentColumn.getSourceColumns())) {
            // 来源字段
            List<ParseColumnNode> sourceColumnList = currentColumn.getSourceColumns();
            // 遍历存入能够直接取到的字段
            sourceColumnList.forEach(column -> {
                TreeNode<ParseColumnNode> middleColumnNode = new TreeNode<>();
                currentColumnNode.addChild(middleColumnNode);
                middleColumnNode.setValue(column);
                // 依旧以当前的表节点去向下检索来源字段
                this.traceColumnLineageTree(dbType, middleColumnNode, tableNode);
            });
            // 来源字段不为空，提前结束
            return;
        }
        // 字段肯定来源于下一级的节点去寻找, 构建离当前节点最近的别名节点
        List<TreeNode<ParseTableNode>> nearestTableNodeList = this.nearestTableNodes(tableNode);
        // 当前字段的定位表名
        String scanTableName = currentColumnNode.getValue().getTableName();
        // 字段名称为空修复
        if (StringUtils.isEmpty(scanTableName)) {
            scanTableName = this.repairMissingTableName(currentColumnNode.getValue(), dbType);
        }
        // 先遍历来源的表节点列表
        for (TreeNode<ParseTableNode> currentRecentlyTableNode : nearestTableNodeList) {
            ParseTableNode lineageTable = currentRecentlyTableNode.getValue();
            //  如果是叶子节点，直接返回表名作为别名
            String alias = Optional.ofNullable(lineageTable.getAlias()).orElse(lineageTable.getName());
            if (!alias.equals(scanTableName)) {
                // 下一次循环
                continue;
            }
            if (currentRecentlyTableNode.isLeaf()) {
                TreeNode<ParseColumnNode> endColumnNode = new TreeNode<>();
                endColumnNode.setValue(ParseColumnNode.builder()
                        .name(currentColumnNode.getValue().getName())
                        .tableName(scanTableName)
                        .owner(lineageTable)
                        // 记录节点ID
                        .tableTreeNodeId(currentRecentlyTableNode.getId().get())
                        .build());
                currentColumnNode.addChild(endColumnNode);
                return;
                // 1. 终止
            }
            // 定位的列名 先取列名，列名去不了取别名
            String scanColumnName = Optional.ofNullable(currentColumnNode.getValue().getName()).orElse(currentColumnNode.getValue().getAlias());
            // 获取当前中间节点的字段名
            List<ParseColumnNode> columns = currentRecentlyTableNode.getValue().getColumns();
            // 处理字段名为[*]的情况
            /*if (columns.size() == 1 && Objects.equals("*", columns.get(0).getName())) {
                String tableExpression = columns.get(0).getTableExpression();
                Set<TableStat.Name> tables = getTablesFromSql(tableExpression);
                tables.forEach(table -> {
                    String tableName = table.getName();
                    String targetDbName = tableName.split("\\.")[0];
                    String targetTblName = tableName.split("\\.")[1];

                    log.info("查询元数据：[{}]", tableName);
                    List<JSONObject> resultColumns = DorisJdbcUtil.executeQuery(hostUrl, db, user, password, "desc " + targetDbName + "." + targetTblName);
                    for (JSONObject column : resultColumns) {
                        ParseColumnNode node = new ParseColumnNode();
                        node.setName(column.getString("Field"));
                        node.setTableName(tableName);
                        columns.add(node);
                    }
                });
            }*/
            // 处理字段名为[*]的情况
            if (columns.size() == 1 && StringUtils.isBlank(columns.get(0).getName())) {
                columns.addAll(columns.get(0).getSourceColumns());
            }
            for (ParseColumnNode column : columns) {
                String name = Optional.ofNullable(column.getAlias()).orElse(column.getName());
                if (scanColumnName.equals(name)) {
                    TreeNode<ParseColumnNode> midColumnTree = new TreeNode<>();
                    currentColumnNode.addChild(midColumnTree);
                    midColumnTree.setValue(column);
                    // 继续向下遍历
                    this.traceColumnLineageTree(dbType, midColumnTree, currentRecentlyTableNode);
                    return;
                }
            }
        }
        // for 循环完之后还是找不到，判断长度是否为1并且为叶子节点
        this.possibleColumnSource(currentColumnNode, nearestTableNodeList);
    }

    private void possibleColumnSource(TreeNode<ParseColumnNode> currentColumnNode, List<TreeNode<ParseTableNode>> nearestTableNodeList) {
        String columnName = currentColumnNode.getValue().getName();
        // 如果是叶子节点
        if (CollectionUtils.isNotEmpty(nearestTableNodeList) && nearestTableNodeList.size() == 1 && nearestTableNodeList.get(0).isLeaf()) {
            // 2. 终止
            TreeNode<ParseColumnNode> endColumnNode = new TreeNode<>();
            currentColumnNode.addChild(endColumnNode);
            endColumnNode.setValue(ParseColumnNode.builder()
                            // 最后是取真实字段名
                            .name(currentColumnNode.getValue().getName())
                            .tableName(nearestTableNodeList.get(0).getValue().getName())
                            .owner(nearestTableNodeList.get(0).getValue())
                            .build());
        } else {
            // 兜底：记录找不到的信息
            String tableName = currentColumnNode.getValue().getTableName();
            log.warn("字段[{}]来源未知", StringUtils.isBlank(tableName) ? columnName : (tableName + "." + columnName));
        }
    }

    /**
     * 递归找到原始SQL
     */
    private String findOriginSql(TreeNode<ParseColumnNode> parent) {
        if (Objects.nonNull(parent.getParent())) {
            return findOriginSql(parent.getParent());
        }
        return parent.getValue().getTableExpression();
    }

    /**
     * 查找表血缘树最近的节点
     *
     * @param currentNode 当前的🌲 节点
     * @return List<TreeNode < TableNode>>
     */
    private List<TreeNode<ParseTableNode>> nearestTableNodes(TreeNode<ParseTableNode> currentNode) {
        List<TreeNode<ParseTableNode>> hitTreeNodeList = recentTreeNodeMap.get(currentNode.getId().get());
        if (CollectionUtils.isNotEmpty(hitTreeNodeList)) {
            return hitTreeNodeList;
        }
        List<TreeNode<ParseTableNode>> nearestTableNodeList = new ArrayList<>();
        this.nearestTableNodes(currentNode, nearestTableNodeList);
        // 放入缓存
        recentTreeNodeMap.put(currentNode.getId().get(), nearestTableNodeList);
        return nearestTableNodeList;
    }

    /**
     * 查询离当前节点最近的节点
     *
     * @param currentNode          当前节点
     * @param nearestTableNodeList 存储当前的最近节点
     */
    private void nearestTableNodes(TreeNode<ParseTableNode> currentNode, List<TreeNode<ParseTableNode>> nearestTableNodeList) {
        // 找完所有的节点都没有找到，那么从查询的中断节点里面去寻找，如果别名为空 找下一个节点，如果匹配到别名就停止并返回
        if (currentNode.isLeaf()) {
            nearestTableNodeList.add(currentNode);
            return;
        }
        // 如果找不到就找子节点
        currentNode.getChildList().forEach(node -> {
            // 子节点，找到就结束
            if (node.getValue().getAlias() != null || node.getValue().getIsVirtualTemp() == null) {
                nearestTableNodeList.add(node);
                // 当本身是别名节点时不往下走
                return;
            }
            // 找不到继续向下
            this.nearestTableNodes(node, nearestTableNodeList);
        });
    }

    /**
     * 修复缺失的表名
     * for example:
     * 1. select distinct a1,b1 from table1 V
     * 2. select distinct a1,b1 from table1,table2 X
     * 第一种情况识别可以，第二种会取第一个表的字段，建议在SQL书写时，指定出对应别名
     *
     * @param parseColumnNode columnNode
     * @param dbType     dbType
     * @return 表名
     */
    private String repairMissingTableName(ParseColumnNode parseColumnNode, String dbType) {
        if (StringUtils.isEmpty(parseColumnNode.getTableExpression())) {
            // throw new ParserException("repair missing table, table expression can't null.");
            return null;
        }
        SQLStatement stmt = SQLUtils.parseSingleStatement(parseColumnNode.getTableExpression(), dbType);
        MySqlSchemaStatVisitor mysqlSchemaStatVisitor = new MySqlSchemaStatVisitor();
        stmt.accept(mysqlSchemaStatVisitor);
        return mysqlSchemaStatVisitor.getTables().keySet().stream().findFirst().orElseThrow(() -> new ParserException("repair missing table failed,column expression[%s].", parseColumnNode.getExpression())).getName();
    }

    private Set<TableStat.Name> getTablesFromSql(String sql) {
        SQLStatementParser parser = new MySqlStatementParser(sql);
        SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) parser.parseSelect();
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        sqlSelectStatement.accept(visitor);
        Map<TableStat.Name, TableStat> tables = visitor.getTables();
        return tables.keySet();
    }
}
