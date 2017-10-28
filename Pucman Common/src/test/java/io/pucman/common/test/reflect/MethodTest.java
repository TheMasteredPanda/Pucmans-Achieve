package io.pucman.common.test.reflect;

import io.pucman.common.exception.TryUtil;
import io.pucman.common.reflect.ReflectUtil;
import io.pucman.common.reflect.accessors.ConstructorAccessor;
import io.pucman.common.reflect.accessors.MethodAccessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MethodTest
{
    @Test
    public void getMethod()
    {
        MethodAccessor<String> accessor = TryUtil.sneaky(() -> ReflectUtil.wrap(ReflectClass.class.getMethod("getText")), MethodAccessor.class);
        Assertions.assertEquals(accessor, ReflectUtil.get(ReflectClass.class, "getText", ReflectUtil.Type.DECLARED, new Class[0]));
        accessor = null;
    }

    @Test
    public void invokeMethod()
    {
        ConstructorAccessor<ReflectClass> constructorAccessor = ReflectUtil.get(ReflectClass.class, ReflectUtil.Type.DECLARED, String.class);
        ReflectClass clazz = constructorAccessor.call("hello");
        MethodAccessor<String> accessor = ReflectUtil.get(ReflectClass.class, "getText", ReflectUtil.Type.DECLARED, new Class[0]);
        Assertions.assertEquals("hello", accessor.call(clazz));
        constructorAccessor = null;
        clazz = null;
        accessor = null;
    }
}
