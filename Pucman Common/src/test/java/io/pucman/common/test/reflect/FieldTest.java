package io.pucman.common.test.reflect;

import io.pucman.common.generic.GenericUtil;
import io.pucman.common.reflect.ReflectUtil;
import io.pucman.common.reflect.accessors.ConstructorAccessor;
import io.pucman.common.reflect.accessors.FieldAccessor;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

public class FieldTest
{
    @Test @SneakyThrows
    public void getField()
    {
        FieldAccessor<String> fieldAccessor = GenericUtil.cast(ReflectUtil.wrapField(ReflectClass.class.getDeclaredField("text")));
        Assert.assertEquals(fieldAccessor.getName(), ReflectUtil.getField(ReflectClass.class, "text", ReflectUtil.Type.DECLARED).getName());
    }

    @Test
    public void setField()
    {
        ConstructorAccessor<ReflectClass> clazz = ReflectUtil.getConstructor(ReflectClass.class, ReflectUtil.Type.DECLARED, String.class);
        ReflectClass instance = clazz.call("hello");

        if (instance == null) {
            Assert.fail("Instance is null.");
        }

        FieldAccessor<String> fieldAccessor = ReflectUtil.getField(ReflectClass.class, "text", ReflectUtil.Type.DECLARED);
        Assert.assertEquals("hello", instance.getText());
        fieldAccessor.set(instance, "hi");
        Assert.assertEquals("hi", fieldAccessor.get(instance));
    }
}
