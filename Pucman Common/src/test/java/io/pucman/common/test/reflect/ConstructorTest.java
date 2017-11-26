package io.pucman.common.test.reflect;

import io.pucman.common.reflect.ReflectUtil;
import io.pucman.common.reflect.accessors.ConstructorAccessor;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

public class ConstructorTest
{
    @Test @SneakyThrows
    public void getConstructor()
    {
        ConstructorAccessor<ReflectClass> constructor = ReflectUtil.wrapConstructor(ReflectClass.class.getDeclaredConstructor(String.class));
        Assert.assertEquals(constructor.get().getName(), ReflectUtil.getConstructor(ReflectClass.class, ReflectUtil.Type.DECLARED, String.class).get().getName());
    }

    @Test
    public void createNewInstanceTest()
    {
        ConstructorAccessor<ReflectClass> clazz = ReflectUtil.getConstructor(ReflectClass.class, ReflectUtil.Type.DECLARED, String.class);
        ReflectClass instance1 = clazz.call("hello");
        Assert.assertEquals("hello", instance1.getText());
    }
}
