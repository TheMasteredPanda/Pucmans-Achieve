package io.pucman.common.test.reflect;

import io.pucman.common.exception.TryUtil;
import io.pucman.common.reflect.ReflectUtil;
import io.pucman.common.reflect.accessors.ConstructorAccessor;
import io.pucman.common.reflect.accessors.FieldAccessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FieldTest
{
    @Test
    public void getField()
    {
        FieldAccessor<String> fieldAccessor = TryUtil.sneaky(() -> ReflectUtil.wrap(ReflectClass.class.getField("s")));
        Assertions.assertEquals(fieldAccessor, ReflectUtil.get(ReflectClass.class, "s", ReflectUtil.Type.DECLARED));
        fieldAccessor = null;
    }

    @Test
    public void setField()
    {
        ConstructorAccessor<ReflectClass> clazz = ReflectUtil.get(ReflectClass.class, ReflectUtil.Type.DECLARED, String.class);
        ReflectClass instance = clazz.call("hello");
        FieldAccessor<String> fieldAccessor = ReflectUtil.get(ReflectClass.class, "s", ReflectUtil.Type.DECLARED);
        Assertions.assertEquals("hello", instance.getText());
        fieldAccessor.set(instance, "hi");
        Assertions.assertEquals("hi", fieldAccessor.get(instance));
        clazz = null;
        instance = null;
        fieldAccessor = null;
    }
}
