package io.pucman.common.test.reflect;

import io.pucman.common.exception.TryUtil;
import io.pucman.common.reflect.ReflectUtil;
import io.pucman.common.reflect.accessors.ConstructorAccessor;
import io.pucman.common.reflect.accessors.MethodAccessor;
import org.junit.Assert;
import org.junit.Test;

public class MethodTest
{
    @Test
    public void getMethod()
    {
        MethodAccessor<String> accessor = TryUtil.sneaky(() -> ReflectUtil.wrapMethod(ReflectClass.class.getMethod("getText")), MethodAccessor.class);
        Assert.assertEquals(accessor.getName(), ReflectUtil.getMethod(ReflectClass.class, "getText", ReflectUtil.Type.DECLARED, String.class).getName());
    }

    @Test
    public void invokeMethod()
    {
        ConstructorAccessor<ReflectClass> constructorAccessor = ReflectUtil.getConstructor(ReflectClass.class, ReflectUtil.Type.DECLARED, String.class);
        ReflectClass clazz = constructorAccessor.call("hello");
        if (clazz == null) {
            Assert.fail("Instance is null.");
        }
        MethodAccessor<String> accessor = ReflectUtil.getMethod(ReflectClass.class, "getText", ReflectUtil.Type.DECLARED, String.class);
        Assert.assertEquals("hello", accessor.call(clazz));
    }
}
