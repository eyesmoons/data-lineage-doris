package com.eyesmoons.lineage.parser.analyse.impl;

import com.alibaba.fastjson.JSONObject;
import com.eyesmoons.lineage.exception.CustomException;
import com.eyesmoons.lineage.utils.DorisJdbcUtil;
import com.eyesmoons.lineage.parser.analyse.SqlRequestContext;
import com.eyesmoons.lineage.parser.analyse.SqlResponseContext;
import com.eyesmoons.lineage.parser.analyse.IHandler;
import com.eyesmoons.lineage.contants.PriorityConstants;
import com.eyesmoons.lineage.model.parser.ParseColumnNode;
import com.eyesmoons.lineage.model.parser.ParseTableNode;
import com.eyesmoons.lineage.model.parser.TreeNode;
import com.eyesmoons.lineage.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class FirstTableSupplementHandler implements IHandler {

    private static final String hostUrl = PropertiesUtil.getPropValue("doris.hostUrl");
    private static final String user = PropertiesUtil.getPropValue("doris.user");
    private static final String password = PropertiesUtil.getPropValue("doris.password");

    @Override
    public void handleRequest(SqlRequestContext request, SqlResponseContext response) {
        if (whetherHandle(response)) {
            TreeNode<ParseTableNode> root = response.getLineageTableTree();
            ParseTableNode parseTableNode = root.getValue();
            String targetTableName = parseTableNode.getName();
            String targetTableDb = parseTableNode.getDbName();
            log.info("查询元数据：[{}.{}]", targetTableDb, targetTableName);
            List<JSONObject> resultColumns = DorisJdbcUtil.executeQuery(hostUrl, targetTableDb, user, password, "desc " + targetTableDb + "." + targetTableName);
            List<ParseColumnNode> parseColumnNodeList = resultColumns.stream().map(this::convert2ColumnNode).collect(Collectors.toList());
            List<ParseColumnNode> childColumnList = this.findFirstHaveColumnTableNode(root).getValue().getColumns();
            if (parseColumnNodeList.size() != childColumnList.size()) {
                throw new CustomException("解析SQL错误，来源表字段和目标表字段数量不一致，来源表：" + childColumnList.size() + "目标表：" + parseColumnNodeList.size());
            }
            for (int i = 0; i < childColumnList.size(); i++) {
                parseColumnNodeList.get(i).setOwner(parseTableNode);
                parseColumnNodeList.get(i).getSourceColumns().add(childColumnList.get(i));
            }
            parseTableNode.getColumns().addAll(parseColumnNodeList);
        }
    }

    boolean whetherHandle(SqlResponseContext response) {
        return CollectionUtils.isEmpty(response.getLineageTableTree().getValue().getColumns());
    }

    private ParseColumnNode convert2ColumnNode(JSONObject jsonObject) {
        ParseColumnNode parseColumnNode = new ParseColumnNode();
        parseColumnNode.setName(jsonObject.getString("Field"));
        return parseColumnNode;
    }

    /**
     * 找到第一个有字段的节点
     *
     * @param root TableNode
     * @return TreeNode<TableNode>
     */
    private TreeNode<ParseTableNode> findFirstHaveColumnTableNode(TreeNode<ParseTableNode> root) {
        if (!org.springframework.util.CollectionUtils.isEmpty(root.getValue().getColumns())) {
            return root;
        }
        if (org.springframework.util.CollectionUtils.isEmpty(root.getChildList()) || root.getChildList().size() != 1) {
            throw new CustomException("node found more");
        }
        // 第一个有字段的节点，其父级仅有一个子元素
        return root.getChildList().get(0);
    }
}
