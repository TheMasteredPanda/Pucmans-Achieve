package io.pucman.common.reflect.accessors;

import io.pucman.common.generic.GenericUtil;
import io.pucman.common.reflect.ReflectUtil;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Wrapper for handling reflected methods.
 * @see ReflectUtil#getMethod(Class, String, ReflectUtil.Type, Class[]).
 * @param <T> - return type of the method.
 */
public class MethodAccessor<T>
{
    private Method method;

    public MethodAccessor(Method method)
    {
        this.method = method;
        method.setAccessible(true);
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
        return GenericUtil.cast(method.invoke(instance, parameters));
    }

    /**
     * @return all annotations accessible in this class.
     */
    public Annotation[] getPublicAnnotations()
    {
        return method.getAnnotations();
    }

    /**
     * @return all annotations accessible only in this class.
     */
    public Annotation[] getPrivateAnnotations()
    {
        return method.getDeclaredAnnotations();
    }

    /**
     * To check if the method has an annotation.
     * @param annotation - the annotation you want it to check for.
     * @return if the method had the annotation.
     */
    public boolean hasAnnotation(Class<? extends Annotation> annotation)
    {
        return method.isAnnotationPresent(annotation);
    }

    /**
     * Gets an annotation, returns null if the annotation is not found.
     * @param annotation - the annotation.
     * @return the annotation.
     */
    public Object getAnnotation(Class<? extends Annotation> annotation, ReflectUtil.Type annotationType)
    {
        return annotationType == ReflectUtil.Type.PUBLIC ? method.getAnnotation(annotation) : method.getDeclaredAnnotation(annotation);
    }

    public Annotation[][] getAnnotatedParameters()
    {
        return method.getParameterAnnotations();
    }

    /**
     * @return the parameters of this method.
     */
    public Parameter[] getParameters()
    {
        return method.getParameters();
    }

    /**
     * @return the name of the method.
     */
    public String getName()
    {
        return method.getName();
    }

    /**
     * @return method.
     */
    public Method get()
    {
        return method;
    }
}