package io.pucman.common.test.reflect;

public class ReflectClass
{
    private String text;

    private ReflectClass(String s)
    {
        this.text = s;
    }

    public String getText()
    {
        return this.text;
    }
}
