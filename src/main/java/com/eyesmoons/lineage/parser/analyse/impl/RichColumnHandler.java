package com.eyesmoons.lineage.parser.analyse.impl;

import com.eyesmoons.lineage.parser.analyse.SqlRequestContext;
import com.eyesmoons.lineage.parser.analyse.SqlResponseContext;
import com.eyesmoons.lineage.parser.analyse.IHandler;
import com.eyesmoons.lineage.contants.PriorityConstants;
import com.eyesmoons.lineage.model.parser.ParseTableNode;
import com.eyesmoons.lineage.model.parser.TreeNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 3. 填充字段信息
 */
@Order(PriorityConstants.LITTLE_LOW - 10)
@Component
@Slf4j
public class RichColumnHandler implements IHandler {

    @Override
    public void handleRequest(SqlRequestContext request, SqlResponseContext response) {
        fillingTableExpression(response.getLineageTableTree());
    }

    /**
     * Tree<Table> 填充Column的TableExpression 字段
     * @param root 当前表关系树节点
     */
    public void fillingTableExpression(TreeNode<ParseTableNode> root) {
        root.getValue().getColumns().forEach(columnNode -> columnNode.setTableExpression(root.getValue().getExpression()));
        if (root.isLeaf()) {
            return;
        }
        root.getChildList().forEach(this::fillingTableExpression);
    }
}
