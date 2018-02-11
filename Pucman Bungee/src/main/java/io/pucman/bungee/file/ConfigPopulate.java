package io.pucman.bungee.file;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to decorate fields that are meant to be populated with a value from
 * a configuration file. This is an easy, and simple, alternative to colouring, formatting, and populating fields
 * with config values.
 *
 * @see BaseFile
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigPopulate
{
    /**
     * The node path pointing to the value this field
     * will be populated with.
     * @return the node path.
     */
    String value();

    /**
     * Whether this field will be coloured or not.
     * This will only work on Strings and
     * TextComponents.
     * @return if true, it will be coloured, else false.
     */
    boolean colour() default false;

    /**
     * Whether this field should be formatted or not.
     * This will only work on Strings and TextCompo-
     * nents.
     * @return if true, it will be formatted, else false
     * @see ReplacementEntry
     */
    boolean format() default false;
}
