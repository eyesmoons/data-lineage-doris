package com.eyesmoons.lineage.event.util;

import com.eyesmoons.lineage.event.domain.model.BaseNodeEntity;
import com.eyesmoons.lineage.event.domain.model.RelationNode;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.Comparator;
import java.util.List;

public class LineageUtil {


    public static String genRelationNodePk(RelationNode relationNode) {
        // sourceNodePkList：x,y
        // targetNodePk: z
        // md5(targetNodePk + sourceNodePkList排序后使用下划线'_'连接)
        List<String> sourceNodePkList = relationNode.getSourceNodePkList();
        if (CollectionUtils.isEmpty(sourceNodePkList)) {
            return DigestUtils.md5DigestAsHex(relationNode.getTargetNodePk().getBytes());
        }
        sourceNodePkList.sort(Comparator.naturalOrder());
        String key = relationNode.getTargetNodePk() + "_" + String.join("_", sourceNodePkList);
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 填充dataSource信息字段
     * @param source BaseNodeEntity FieldNode|TableNode
     * @param target RelationNode
     */
    public static void fillingRelationNode(BaseNodeEntity source, RelationNode target) {
        target.setDataSourceName(source.getDataSourceName());
    }
}
