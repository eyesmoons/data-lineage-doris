package com.eyesmoons.lineage.parser.analyse.handler;

import com.alibaba.druid.sql.parser.ParserException;
import com.eyesmoons.lineage.parser.analyse.SqlRequestContext;
import com.eyesmoons.lineage.parser.analyse.SqlResponseContext;
import com.eyesmoons.lineage.parser.constant.PriorityConstants;
import com.eyesmoons.lineage.parser.model.ParseColumnNode;
import com.eyesmoons.lineage.parser.model.ParseTableNode;
import com.eyesmoons.lineage.parser.model.TreeNode;
import com.eyesmoons.lineage.parser.tracer.ColumnLineageTracer;
import com.eyesmoons.lineage.parser.tracer.ColumnLineageTracerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 4. 字段血缘解析
 */
@Order(PriorityConstants.LITTLE_LOW)
@Component
public class LineageColumnHandler implements IHandler {

    @Override
    public void handleRequest(SqlRequestContext request, SqlResponseContext response) {
        handleColumnRelation(request, response);
    }

    private void handleColumnRelation(SqlRequestContext sqlContext, SqlResponseContext response) {
        TreeNode<ParseTableNode> lineageTableTree = response.getLineageTableTree();
        TreeNode<ParseTableNode> firstHaveColumnTableNode = this.findFirstHaveColumnTableNode(lineageTableTree);
        List<ParseColumnNode> rootColumns = firstHaveColumnTableNode.getValue().getColumns();
        if (CollectionUtils.isEmpty(rootColumns)) {
            throw new ParserException("node not found effective");
        }
        ColumnLineageTracer columnLineageTracer = ColumnLineageTracerFactory.getDefaultTracer();
        // 获取到字段血缘树
        List<TreeNode<ParseColumnNode>> lineageColumnTreeList = new ArrayList<>();
        rootColumns.stream().map(TreeNode::of).forEach(nodeTreeNode -> {
            lineageColumnTreeList.add(nodeTreeNode);
            columnLineageTracer.traceColumnLineageTree(sqlContext.getDbType(), nodeTreeNode, firstHaveColumnTableNode);
        });
        // save
        response.setLineageColumnTreeList(lineageColumnTreeList);
    }

    /**
     * 找到第一个有字段的节点
     *
     * @param root TableNode
     * @return TreeNode<TableNode>
     */
    private TreeNode<ParseTableNode> findFirstHaveColumnTableNode(TreeNode<ParseTableNode> root) {
        if (!CollectionUtils.isEmpty(root.getValue().getColumns())) {
            return root;
        }
        if (CollectionUtils.isEmpty(root.getChildList()) || root.getChildList().size() != 1) {
            throw new ParserException("node found more");
        }
        // 第一个有字段的节点，其父级仅有一个子元素
        return root.getChildList().get(0);
    }

}
