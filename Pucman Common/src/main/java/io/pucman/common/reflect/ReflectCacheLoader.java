package io.pucman.common.reflect;

import com.google.common.cache.CacheLoader;
import com.google.common.collect.Lists;
import io.pucman.common.exception.DeveloperException;
import io.pucman.common.reflect.accessors.FieldAccessor;
import io.pucman.common.reflect.accessors.ConstructorAccessor;
import io.pucman.common.reflect.accessors.MethodAccessor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;

/**
 * @see ReflectUtil;
 * @param <T> - generic type.
 */
@ParametersAreNonnullByDefault
public class ReflectCacheLoader<T> extends CacheLoader<String, Object>
{

    @Override
    public Object load(String key) throws Exception
    {
        System.out.println("Key: " + key);
        String[] args = key.split(";");

        if (args[0].equals("M")) {
            if (!ReflectUtil.exists(args[1])) return null;
            Class<?> clazz = ReflectUtil.getClass(args[1]);
            LinkedList<Class<?>> parameters = Lists.newLinkedList();

            if (parameters.size() == 5) {
                for (String param : args[4].split("/")) {
                    if (!ReflectUtil.exists(param)) throw new DeveloperException("Parameter " + param + " does not exist.");
                    parameters.add(ReflectUtil.getClass(param));
                }
            }

            Method method = args[3].equals("DECLARED") ? clazz.getDeclaredMethod(args[2], parameters.toArray(new Class[parameters.size()])) : clazz.getMethod(args[2], parameters.toArray(new Class[parameters.size()]));
            return new MethodAccessor<T>(method);
        }

        if (args[0].equals("F")) {
            if (!ReflectUtil.exists(args[1])) return null;
            Class<?> clazz = ReflectUtil.getClass(args[1]);

            Field field = args[3].equals("DECLARED") ? clazz.getDeclaredField(args[2]) : clazz.getField(args[2]);
            return new FieldAccessor<T>(field);
        }

        if (args[0].equals("C")) {
            if (!ReflectUtil.exists(args[1])) return null;
            Class<?> clazz = ReflectUtil.getClass(args[1]);
            LinkedList<Class<?>> parameters = Lists.newLinkedList();

            for (String param : args[3].split("/")) {
                if (!ReflectUtil.exists(param)) throw new DeveloperException("Parameter " + param + " does not exist.");
                parameters.add(ReflectUtil.getClass(param));
            }

            Constructor constructor = args[2].equals("DECLARED") ?
                    clazz.getDeclaredConstructor(parameters.toArray(new Class[parameters.size()])) :
                    clazz.getConstructor(parameters.toArray(new Class[parameters.size()]));
            return new ConstructorAccessor<T>(constructor);
        }

        throw new DeveloperException("Nothing was returned when parsing " + key + ".");
    }
}
