package io.pucman.common.reflect;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import io.pucman.common.exception.UtilException;
import io.pucman.common.reflect.accessors.ConstructorAccessor;
import io.pucman.common.reflect.accessors.FieldAccessor;
import io.pucman.common.reflect.accessors.MethodAccessor;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * For easy management of reflected objects.
 */
public final class ReflectUtil
{
    /**
     * All objects reflected will be stored here for 5 minutes,
     * after that it will be invalidated. This will remove the
     * process of creating a new instance of the same object
     * when manipulating it.
     */
    private static final LoadingCache<String, Object> REFLECT_CACHE = CacheBuilder.newBuilder().weakKeys().expireAfterAccess(5, TimeUnit.MINUTES).build(new ReflectCacheLoader());

    public ReflectUtil()
    {
        throw new UtilException();
    }

    /**
     * PUBLIC - Gets all objects up the entire class hierarchy.
     * DECLARED - Gets the object only in the class isn't being
     * invoked upon.
     */
    public enum Type
    {
        PUBLIC, DECLARED
    }


    /**
     * Gets a class.
     * @param clazz - class.
     * @return the class.
     */
    @SneakyThrows
    public static Class<?> getClass(String clazz)
    {
        return (Class<?>) REFLECT_CACHE.get(clazz);
    }

    /**
     * Checks if class exists.
     * @param clazz - class.
     * @return true if class exists, false is not.
     */
    public static boolean exists(String clazz)
    {
        return getClass(clazz) != null;
    }

    /**
     * Gets a method then wraps it up in a MethodAccessor.
     * @see MethodAccessor
     * @param clazz - class to audit for the method.
     * @param method - methods identifier.
     * @param methodType type of method, public or declared.
     * @param parameters - parameters of that method.
     * @return the wrapped method.
     */
    @SneakyThrows
    public static MethodAccessor get(Class<?> clazz, String method, Type methodType, Class<?>... parameters)
    {
        StringBuilder sb = new StringBuilder("M;").append(clazz.getName()).append(";").append(method).append(";").append(methodType.name()).append(";");

        for (int i = 0; i <= parameters.length; i++) {
            sb.append(parameters[i].getClass().getName());

            if (i + 1 != parameters.length) {
                sb.append("/");
            }
        }


        return (MethodAccessor) REFLECT_CACHE.get(sb.toString());
    }

    /**
     * Gets a field then wraps it in a FieldAccessor.
     * @see FieldAccessor
     * @param clazz - class to audit for the field.
     * @param field - fields identifier.
     * @param fieldType - type of field, public or declared.
     * @return the wrapped field.
     */
    @SneakyThrows
    public static FieldAccessor get(Class<?> clazz, String field, Type fieldType)
    {
        return (FieldAccessor) REFLECT_CACHE.get("F;" + clazz.getName() + ";" + field + ";" + fieldType.name());
    }

    /**
     * Gets a constructor then wraps it in a ConstructorAccessor.
     * @see ConstructorAccessor
     * @param clazz
     * @param constructorType
     * @param parameters
     * @return
     */
    @SneakyThrows
    public static ConstructorAccessor get(Class<?> clazz, Type constructorType, Class<?>... parameters)
    {
        StringBuilder sb = new StringBuilder("C;").append(clazz.getName()).append(";").append(constructorType.name()).append(";");

        for (int i = 0; i <= parameters.length; i++) {
            sb.append(parameters[i].getClass().getName());

            if (i + 1 != parameters.length) {
                sb.append("/");
            }
        }

        return (ConstructorAccessor) REFLECT_CACHE.get(sb.toString());
    }


    /**
     * For wrapping a method's reflected instance.
     * @param method - the method.
     * @return the wrapped method.
     */
    @SneakyThrows
    public static MethodAccessor wrap(Method method)
    {
        MethodAccessor accessor = new MethodAccessor(method);
        REFLECT_CACHE.put("F;" + method.getClass().getName() + ";" + method.getName() + ";" + (method.getDeclaringClass().getDeclaredMethod(method.getName(), method.getParameterTypes()) == null ? Type.PUBLIC : Type.DECLARED), accessor);
        return accessor;
    }


    /**
     * For wrapping a field's reflected instance.
     * @param field - the field.
     * @return the wrapped field.
     */

    @SneakyThrows
    public static FieldAccessor wrap(Field field)
    {
        FieldAccessor accessor = new FieldAccessor(field);
        REFLECT_CACHE.put("F;" + field.getClass().getName() + ";" + field.getName() + ";" + (field.getDeclaringClass().getDeclaredField(field.getName()) == null ? Type.PUBLIC : Type.DECLARED), accessor);
        return accessor;
    }

    /**
     * For wrapping a constructor's reflected instance.
     * @param constructor - the constructor.
     * @return the wrapped constructor.
     */
    @SneakyThrows
    public static ConstructorAccessor wrap(Constructor constructor)
    {
        ConstructorAccessor accessor = new ConstructorAccessor(constructor);
        REFLECT_CACHE.put("F;" + constructor.getClass().getName() + ";" + constructor.getName() + ";" + (constructor.getDeclaringClass().getDeclaredConstructor(constructor.getParameterTypes()) == null ? Type.PUBLIC : Type.DECLARED), accessor);
        return accessor;
    }
}
