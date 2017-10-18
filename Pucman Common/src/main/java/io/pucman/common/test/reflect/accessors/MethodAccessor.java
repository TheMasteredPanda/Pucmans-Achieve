package io.pucman.common.test.reflect.accessors;

import io.pucman.common.generic.GenericUtil;
import io.pucman.common.test.reflect.ReflectUtil;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Wrapper for handling reflected methods.
 * @see io.pucman.common.test.reflect.ReflectUtil#get(Class, String, ReflectUtil.Type, Class[]) .
 * @param <T> - return type of the method.
 */
public class MethodAccessor<T>
{
    private Method method;

    public MethodAccessor(Method method)
    {
        this.method = method;
    }

    /**
     * Method used to invoke the reflected method.
     * @param instance - object it will invoke the method on.
     * @param parameters - parameters of the method.
     * @return T
     */
    @SneakyThrows
    public T call(Object instance, Object... parameters)
    {
        return GenericUtil.cast(this.method.invoke(instance, parameters));
    }

    /**
     * @return all annotations accessible in this class.
     */
    public Annotation[] getPublicAnnotations()
    {
        return this.method.getAnnotations();
    }

    /**
     * @return all annotations accessible only in this class.
     */
    public Annotation[] getPrivateAnnotations()
    {
        return this.method.getDeclaredAnnotations();
    }

    /**
     * To check if the method has an annotation.
     * @param annotation - the annotation you want it to check for.
     * @return if the method had the annotation.
     */
    public boolean hasAnnotation(Class<? extends Annotation> annotation)
    {
        return this.method.isAnnotationPresent(annotation);
    }

    /**
     * Gets an annotation, returns null if the annotation is not found.
     * @param annotation - the annotation.
     * @return the annotation.
     */
    public Object getAnnotation(Class<? extends Annotation> annotation, ReflectUtil.Type annotationType)
    {
        return annotationType == ReflectUtil.Type.PUBLIC ? this.method.getAnnotation(annotation) : this.method.getDeclaredAnnotation(annotation);
    }

    public Annotation[][] getAnnotatedParameters()
    {
        return this.method.getParameterAnnotations();
    }

    /**
     * @return the parameters of this method.
     */
    public Parameter[] getParameters()
    {
        return this.method.getParameters();
    }

    /**
     * @return the name of the method.
     */
    public String getName()
    {
        return this.method.getName();
    }

    /**
     * @return method.
     */
    public Method get()
    {
        return this.method;
    }
}