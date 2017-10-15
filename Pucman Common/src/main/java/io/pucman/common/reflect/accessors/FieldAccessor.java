package io.pucman.common.reflect.accessors;

import io.pucman.common.generic.GenericUtil;
import io.pucman.common.reflect.ReflectUtil;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Wrapper for reflected fields.
 * @see ReflectUtil#get(Class, String, ReflectUtil.Type).
 * @param <T> - return type of the field.
 */
public class FieldAccessor<T>
{
    private Field field;


    public FieldAccessor(Field field)
    {
        this.field = field;
        this.field.setAccessible(true);
    }

    /**
     * Ascertain the value defined in this field.
     * @param instance - instance that this will be invoked on.
     * @return T
     */
    @SneakyThrows
    T get(Object instance)
    {
        return GenericUtil.cast(this.field.get(instance));
    }

    /**
     * Set a new value to this field.
     * @param instance - instance that this will be invoked on.
     * @param value - value the field will be set to.
     */
    @SneakyThrows
    void set(Object instance, T value)
    {
        this.field.set(instance, value);
    }

    /**
     * @return all the annotations accessible in this class.
     */
    Annotation[] getPublicAnnotations()
    {
        return this.field.getAnnotations();
    }

    /**
     * @return all annotations accessible only in this class.
     */
    Annotation[] getPrivateAnnotations()
    {
        return this.field.getDeclaredAnnotations();
    }

    /**
     * To check if the field has the annotation.
     * @param annotation - annotation to check.
     * @return whether it has that annotation.
     */
    boolean hasAnnotation(Class<? extends Annotation> annotation)
    {
        return this.field.isAnnotationPresent(annotation);
    }

    /**
     * Get an annotation, returns null if annotation is not found.
     * @param annotation - the annotation.
     * @return the annotation.
     */
    Annotation getAnnotation(Class<? extends Annotation> annotation, ReflectUtil.Type annotationType)
    {
        return annotationType == ReflectUtil.Type.PUBLIC ? this.field.getAnnotation(annotation) : this.field.getDeclaredAnnotation(annotation);
    }

    /**
     * @return type of field it is.
     */
    Class<?> getType()
    {
        return this.field.getType();
    }

    /**
     * @return the field.
     */
    Field get()
    {
        return this.field;
    }

    /**
     * @return field identifier.
     */
    String getName()
    {
        return this.field.getName();
    }
}