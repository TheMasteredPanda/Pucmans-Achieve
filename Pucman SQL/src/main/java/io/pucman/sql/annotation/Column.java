package io.pucman.sql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Decorated above fields to differentiate between fields that will be
 * used when serializing data from a result set to an instance. 
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column
{
    /**
     * Column name.
     */
    String value();

    int digitRange() default 0;

    int decimalRange() default 0;
}
