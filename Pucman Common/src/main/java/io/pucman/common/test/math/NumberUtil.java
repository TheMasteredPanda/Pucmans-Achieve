package io.pucman.common.test.math;

import io.pucman.common.exception.DeveloperException;
import io.pucman.common.exception.UtilException;
import io.pucman.common.generic.GenericUtil;

/**
 * For converting a string into a number.
 */
public final class NumberUtil
{
    private NumberUtil()
    {
        throw new UtilException();
    }

    /**
     * Checking if it is parsable.
     * @param value - the string number.
     * @param type - the number you want it to be.
     * @return true if it can be parsed, false if not.
     */
    public static boolean parseable(String value, Class<?> type)
    {
        return parse(value, type) != null;
    }

    /**
     * Parsing the string number into it's primitive type.
     * @param value - the string number.
     * @param type - the number class.
     * @param <N> - generic type.
     * @return the parsed number.
     */
    public static <N extends Number> N parse(String value, Class<?> type)
    {
        if (type.equals(byte.class)) {
            return GenericUtil.cast(Byte.parseByte(value));
        }

        if (type.equals(short.class)) {
            return GenericUtil.cast(Short.parseShort(value));
        }

        if (type.equals(int.class)) {
            return GenericUtil.cast(Integer.parseInt(value));
        }

        if (type.equals(double.class)) {
            return GenericUtil.cast(Double.parseDouble(value));
        }

        if (type.equals(long.class)) {
            return GenericUtil.cast(Long.parseLong(value));
        }

        if (type.equals(float.class)) {
            return GenericUtil.cast(Float.parseFloat(value));
        }

        throw new DeveloperException("NumberUtils.parse(" + value + ", " + type.getSimpleName() + ") returned null.");
    }
}
