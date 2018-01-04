package io.pucman.sql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used when creating a template class to be referenced when creating a table.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table
{
    /**
     * Table name.
     */
    String value();

    /**
     * name of the field that doubles as the primary key for the table.
     */
    String primaryKey();

    /**
     * Array of unique keys that will be referenced when creating the table.
     */
    String[] uniqueKeys();
}
