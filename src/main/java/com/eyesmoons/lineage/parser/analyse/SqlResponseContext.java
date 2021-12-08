package com.eyesmoons.lineage.parser.analyse;

import com.eyesmoons.lineage.parser.model.ParseColumnNode;
import com.eyesmoons.lineage.parser.model.ParseTableNode;
import com.eyesmoons.lineage.parser.model.TreeNode;
import lombok.Data;

import java.util.List;

@Data
public class SqlResponseContext {

    // 语句类型 INSERT ..., CREATE TABLE AS...
    private String statementType;

    // 表血缘解析结果
    private TreeNode<ParseTableNode> lineageTableTree;

    // 字段血缘解析结果
    private List<TreeNode<ParseColumnNode>> lineageColumnTreeList;
}
