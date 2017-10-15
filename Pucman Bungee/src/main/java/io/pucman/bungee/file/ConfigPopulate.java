package io.pucman.bungee.file;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see BaseFile
 * @see io.pucman.bungee.locale.Locale
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigPopulate
{
    String value();

    boolean color() default false;

    boolean format() default false;
}
