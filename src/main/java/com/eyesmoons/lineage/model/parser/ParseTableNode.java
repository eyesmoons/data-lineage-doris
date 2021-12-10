package com.eyesmoons.lineage.model.parser;

import com.eyesmoons.lineage.utils.StringUtil;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 数据血缘解析时表节点
 */
@Builder
public class ParseTableNode {

    /**
     * 数据库名
     */
    private String dbName;
    /**
     * 表名
     */
    private String name;
    /**
     * 别名
     */
    private String alias;
    /**
     * 是否为虚拟表
     */
    @Builder.Default
    private Boolean isVirtualTemp = false;

    /**
     * 特殊类型节点的处理
     */
    private String queryType;
    /**
     * 字段列表
     */
    private final List<ParseColumnNode> columns = new ArrayList<>();
    /**
     * 表达式
     */
    private String expression;

    public String getDbName() {
        return StringUtils.isBlank(dbName) ? "" : dbName.trim().replace("`","");
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getName() {
        return StringUtils.isBlank(name) ? "" : name.trim().replace("`","");
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Boolean getIsVirtualTemp() {
        return isVirtualTemp;
    }

    public void setIsVirtualTemp(Boolean virtualTemp) {
        isVirtualTemp = virtualTemp;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public List<ParseColumnNode> getColumns() {
        return columns;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public ParseTableNode() {}

    public ParseTableNode(String dbName, String name, String alias, Boolean isVirtualTemp, String queryType, String expression) {
        this.dbName = dbName;
        this.name = name;
        this.alias = alias;
        this.isVirtualTemp = isVirtualTemp;
        this.queryType = queryType;
        this.expression = expression;
    }
}
