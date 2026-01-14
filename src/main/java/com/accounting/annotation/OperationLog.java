package com.accounting.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    String value() default ""; // 操作名称
    OperationType operationType() default OperationType.OTHER;
    OperationModule module() default OperationModule.OTHER;
}
