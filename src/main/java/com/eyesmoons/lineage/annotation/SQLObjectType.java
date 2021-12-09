package com.eyesmoons.lineage.annotation;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * SQLObjectType
 */
@Component
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SQLObjectType {

    @Nullable Class<?> clazz();

    Class<?>[] parent() default {};
}
