package io.pucman.common.test.reflect;

import io.pucman.common.exception.TryUtil;
import io.pucman.common.reflect.ReflectUtil;
import io.pucman.common.reflect.accessors.ConstructorAccessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConstructorTest
{
    @Test
    public void getConstructor()
    {
        ConstructorAccessor<ReflectClass> clazz = TryUtil.sneaky(() -> ReflectUtil.wrap(ReflectClass.class.getConstructor(String.class)), ConstructorAccessor.class);
        Assertions.assertEquals(clazz, ReflectUtil.get(ReflectClass.class, ReflectUtil.Type.DECLARED, String.class));
        clazz = null;
    }

    @Test
    public void createNewInstanceTest()
    {
        ConstructorAccessor<ReflectClass> clazz = ReflectUtil.get(ReflectClass.class, ReflectUtil.Type.DECLARED, String.class);
        ReflectClass instance1 = clazz.call("hello");
        Assertions.assertEquals("hello", instance1.getText());
        clazz = null;
        instance1 = null;
    }
}
