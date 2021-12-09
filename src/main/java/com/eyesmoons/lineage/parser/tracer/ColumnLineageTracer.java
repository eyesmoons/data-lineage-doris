package com.eyesmoons.lineage.parser.tracer;

import com.eyesmoons.lineage.model.parser.ParseColumnNode;
import com.eyesmoons.lineage.model.parser.ParseTableNode;
import com.eyesmoons.lineage.model.parser.TreeNode;

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
