package com.eyesmoons.lineage.parser.tracer;

/**
 * 字段血缘工厂
 */
public class ColumnLineageTracerFactory {

    private ColumnLineageTracerFactory() {}

    /**
     * 获取默认的字段解析器
     * @return ColumnLineageTracer
     */
    public static ColumnLineageTracer getDefaultTracer() {
        return new DefaultColumnLineageTracer();
    }
}
