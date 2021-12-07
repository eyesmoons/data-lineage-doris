package com.eyesmoons.lineage.event.util;

import com.eyesmoons.lineage.event.domain.model.BaseNodeEntity;
import com.eyesmoons.lineage.event.domain.model.ProcessNode;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.Comparator;
import java.util.List;

public class LineageUtil {


    public static String genProcessNodePk(ProcessNode processNode) {
        // sourceNodePkList：x,y
        // targetNodePk: z
        // md5(targetNodePk + sourceNodePkList排序后使用下划线'_'连接)
        List<String> sourceNodePkList = processNode.getSourceNodePkList();
        if (CollectionUtils.isEmpty(sourceNodePkList)) {
            return DigestUtils.md5DigestAsHex(processNode.getTargetNodePk().getBytes());
        }
        sourceNodePkList.sort(Comparator.naturalOrder());
        String key = processNode.getTargetNodePk() + "_" + String.join("_", sourceNodePkList);
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 填充dataSource信息字段
     * @param source BaseNodeEntity FieldNode|TableNode
     * @param target ProcessNode
     */
    public static void fillingProcessNode(BaseNodeEntity source, ProcessNode target) {
        target.setDataSourceName(source.getDataSourceName());
    }
}
