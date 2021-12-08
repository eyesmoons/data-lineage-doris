package com.eyesmoons.lineage.parser.model;

import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据血缘解析时字段节点
 */
@Builder
public class ParseColumnNode {

    /**
     * 列所属的表
     */
    private ParseTableNode owner;
    /**
     * 表
     */
    private String tableName;
    /**
     * 名称
     */
    private String name;
    /**
     * 别名
     */
    private String alias;
    /**
     * 来源列
     */
    private final List<ParseColumnNode> sourceColumns = new ArrayList<>();
    /**
     * 此节点表达式
     */
    private String expression;

    /**
     * 字段所在的表树Id
     */
    private Long tableTreeNodeId;

    /**
     * 表的表达式
     */
    private String tableExpression;

    /**
     * 字段是否为常量
     */
    @Builder.Default
    private boolean isConstant = false;

    public ParseColumnNode(ParseTableNode owner, String tableName, String name, String alias, String expression, Long tableTreeNodeId, String tableExpression, boolean isConstant) {
        this.owner = owner;
        this.tableName = tableName;
        this.name = name;
        this.alias = alias;
        this.expression = expression;
        this.tableTreeNodeId = tableTreeNodeId;
        this.tableExpression = tableExpression;
        this.isConstant = isConstant;
    }

    public ParseColumnNode() {}

    public ParseTableNode getOwner() {
        return owner;
    }

    public void setOwner(ParseTableNode owner) {
        this.owner = owner;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getName() {
        return name;
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

    public List<ParseColumnNode> getSourceColumns() {
        return sourceColumns;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Long getTableTreeNodeId() {
        return tableTreeNodeId;
    }

    public void setTableTreeNodeId(Long tableTreeNodeId) {
        this.tableTreeNodeId = tableTreeNodeId;
    }

    public String getTableExpression() {
        return tableExpression;
    }

    public void setTableExpression(String tableExpression) {
        this.tableExpression = tableExpression;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public void setConstant(boolean constant) {
        isConstant = constant;
    }

    @Override
    public String toString() {
        return "ParseColumnNode{" +
                "owner=" + owner +
                ", tableName='" + tableName + '\'' +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", sourceColumns=" + sourceColumns +
                ", expression='" + expression + '\'' +
                ", tableTreeNodeId=" + tableTreeNodeId +
                ", tableExpression='" + tableExpression + '\'' +
                ", isConstant=" + isConstant +
                '}';
    }
}
