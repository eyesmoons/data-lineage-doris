package com.eyesmoons.lineage.parser.analyse;

import com.eyesmoons.lineage.parser.model.ColumnNode;
import com.eyesmoons.lineage.parser.model.TableNode;
import com.eyesmoons.lineage.parser.model.TreeNode;
import lombok.Data;

import java.util.List;

@Data
public class SqlResponseContext {

    // 语句类型 INSERT ..., CREATE TABLE AS...
    private String statementType;

    // 表血缘解析结果
    private TreeNode<TableNode> lineageTableTree;

    // 字段血缘解析结果
    private List<TreeNode<ColumnNode>> lineageColumnTreeList;
}
