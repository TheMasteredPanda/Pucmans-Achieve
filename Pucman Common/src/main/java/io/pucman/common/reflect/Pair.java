package io.pucman.common.reflect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Pair<T, T1>
{
    private T left;
    private T1 right;
}
