package io.pucman.common.reflect.accessors;

import com.sun.tools.javah.Gen;
import io.pucman.common.generic.GenericUtil;
import io.pucman.common.reflect.ReflectUtil;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

/**
 * Wrapper for reflected constructors.
 * @see io.pucman.common.reflect.ReflectUtil#get(Class, ReflectUtil.Type, Class[]).
 * @param <T> - return type of the constructor.
 */
public class ConstructorAccessor<T>
{
    private Constructor<T> constructor;

    public ConstructorAccessor(Constructor<T> constructor)
    {
        this.constructor = constructor;
    }

    /**
     * Creates a new instance of the class.
     * @param parameters - init arguments.
     * @return new instance.
     */
    @SneakyThrows
    public T call(Object... parameters)
    {
        return GenericUtil.cast(this.constructor.newInstance(parameters));
    }

    /**
     * @return all annotations accessible in the class.
     */
    public Annotation[] getPublicAnnotations()
    {
        return constructor.getAnnotations();
    }


    /**
     * @return all annotations accessible only in this class.
     */
    public Annotation[] getPrivateAnnotations()
    {
        return constructor.getDeclaredAnnotations();
    }

    /**
     * @return parameter annotations.
     */
    public Annotation[][] getParameterAnnotations()
    {
        return constructor.getParameterAnnotations();
    }

    /**
     * @return parameters
     */
    public Parameter[] getParameters()
    {
        return constructor.getParameters();
    }

    /**
     * To check if the constructor has the annotation.
     * @param annotation - the annotation
     * @return if it has the annotation.
     */
    public boolean hasAnnotation(Class<? extends Annotation> annotation)
    {
        return constructor.isAnnotationPresent(annotation);
    }

    /**
     * Get an annotation, returns null if no annotation is not found.
     * @param annotation - the annotation.
     * @return the annotation
     */
    private Annotation getAnnotation(Class<? extends Annotation> annotation, ReflectUtil.Type annotationType)
    {
        return annotationType == ReflectUtil.Type.PUBLIC ? this.constructor.getAnnotation(annotation) : this.constructor.getDeclaredAnnotation(annotation);
    }

    /**
     * @return the constructor.
     */
    public Constructor<T> get()
    {
        return this.constructor;
    }
}
