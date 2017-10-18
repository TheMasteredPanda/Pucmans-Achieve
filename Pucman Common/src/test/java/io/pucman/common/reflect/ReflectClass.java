package io.pucman.common.reflect;

import lombok.Getter;

public class ReflectClass
{
    @Getter
    private String text;

    private ReflectClass(String s)
    {
        this.text = s;
    }
}
