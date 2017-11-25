package io.pucman.common.test.math;

import io.pucman.common.math.NumberUtil;
import org.junit.Assert;
import org.junit.Test;

public class NumberUtilTest
{
    private String testField = "4";

    @Test
    public void parsable()
    {
        Assert.assertEquals(true, NumberUtil.parseable(testField, int.class));
    }

    @Test
    public void parse()
    {
        Assert.assertEquals(4, (int) NumberUtil.parse(testField, int.class));
    }
}
