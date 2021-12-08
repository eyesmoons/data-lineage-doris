package com.eyesmoons.lineage.parser.tracer;

import com.eyesmoons.lineage.parser.model.ParseColumnNode;
import com.eyesmoons.lineage.parser.model.ParseTableNode;
import com.eyesmoons.lineage.parser.model.TreeNode;

/**
 * 构建字段血缘
 */
public interface ColumnLineageTracer {

    /**
     * 构建血缘关系
     * @param dbType            数据库类型
     * @param currentColumnNode 当前的Column
     * @param tableNode         表血缘树
     */
    void traceColumnLineageTree(String dbType, TreeNode<ParseColumnNode> currentColumnNode, TreeNode<ParseTableNode> tableNode);
}
