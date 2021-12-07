package com.eyesmoons.lineage.event.handler.sql;

import com.alibaba.fastjson.JSONObject;
import com.eyesmoons.lineage.common.exception.CommonException;
import com.eyesmoons.lineage.event.metadata.DorisJdbcUtil;
import com.eyesmoons.lineage.parser.analyse.SqlRequestContext;
import com.eyesmoons.lineage.parser.analyse.SqlResponseContext;
import com.eyesmoons.lineage.parser.analyse.handler.IHandler;
import com.eyesmoons.lineage.parser.constant.PriorityConstants;
import com.eyesmoons.lineage.parser.model.ColumnNode;
import com.eyesmoons.lineage.parser.model.TableNode;
import com.eyesmoons.lineage.parser.model.TreeNode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 2. 补全首节点column信息，在解析时针对字段不包含列名的情况
 *  create table xxx as select
 *  insert into table1 select a, b from table2
 */
@Order(PriorityConstants.LITTLE_HIGH + 1)
@Component
public class FirstTableSupplementHandler implements IHandler {

    private static final String hostUrl = "10.220.146.10:6033";

    private static final String db = "demo";

    private static final String user = "root";

    private static final String password = "";

    @Override
    public void handleRequest(SqlRequestContext request, SqlResponseContext response) {
        if (whetherHandle(response)) {
            TreeNode<TableNode> root = response.getLineageTableTree();
            TableNode tableNode = root.getValue();
            String targetTableName = tableNode.getName();
            String targetTableDb = tableNode.getDbName();
            List<JSONObject> resultColumns = DorisJdbcUtil.executeQuery(hostUrl, db, user, password, "desc " + targetTableDb + "." + targetTableName);
            List<ColumnNode> columnNodeList = resultColumns.stream().map(this::convert2ColumnNode).collect(Collectors.toList());
            List<ColumnNode> childColumnList = this.findFirstHaveColumnTableNode(root).getValue().getColumns();
            if (columnNodeList.size() != childColumnList.size()) {
                throw new CommonException("解析SQL错误，来源表字段和目标表字段数量不一致，来源表：" + childColumnList.size() + "目标表：" + columnNodeList.size());
            }
            for (int i = 0; i < childColumnList.size(); i++) {
                columnNodeList.get(i).setOwner(tableNode);
                columnNodeList.get(i).getSourceColumns().add(childColumnList.get(i));
            }
            tableNode.getColumns().addAll(columnNodeList);
        }
    }

    boolean whetherHandle(SqlResponseContext response) {
        return CollectionUtils.isEmpty(response.getLineageTableTree().getValue().getColumns());
    }

    private ColumnNode convert2ColumnNode(JSONObject jsonObject) {
        ColumnNode columnNode = new ColumnNode();
        columnNode.setName(jsonObject.getString("Field"));
        return columnNode;
    }

    /**
     * 找到第一个有字段的节点
     *
     * @param root TableNode
     * @return TreeNode<TableNode>
     */
    private TreeNode<TableNode> findFirstHaveColumnTableNode(TreeNode<TableNode> root) {
        if (!org.springframework.util.CollectionUtils.isEmpty(root.getValue().getColumns())) {
            return root;
        }
        if (org.springframework.util.CollectionUtils.isEmpty(root.getChildList()) || root.getChildList().size() != 1) {
            throw new CommonException("node found more");
        }
        // 第一个有字段的节点，其父级仅有一个子元素
        return root.getChildList().get(0);
    }
}
