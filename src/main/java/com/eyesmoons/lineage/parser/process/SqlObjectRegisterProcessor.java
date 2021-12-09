package com.eyesmoons.lineage.parser.process;

import com.alibaba.druid.sql.ast.SQLObject;
import com.eyesmoons.lineage.annotation.SQLObjectType;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * 注册后置处理器
 */
@Component
@ConditionalOnClass(SQLObject.class)
public class SqlObjectRegisterProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        Class<?> cla = bean.getClass();
        SQLObjectType sqlObjectType = cla.getAnnotation(SQLObjectType.class);
        if (sqlObjectType == null) {
            return bean;
        }
        Class<?> clazz = sqlObjectType.clazz();
        ProcessorRegister.register(clazz, bean);
        return bean;
    }
}
