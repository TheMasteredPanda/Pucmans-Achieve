package io.pucman.common.test.math;

import io.pucman.common.math.NumberUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NumerUtilTest
{
    private String testField = "4";

    @Test
    public void parsable()
    {
        Assertions.assertEquals(true, NumberUtil.parseable(testField, Integer.class));
    }

    @Test
    public void parse()
    {
        Assertions.assertEquals(4, (int) NumberUtil.parse(testField, Integer.class));
    }
}
