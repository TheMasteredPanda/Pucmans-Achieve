package io.pucman.sql;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.math.BigInteger;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Constants of each SQL data type, their minimum and maximum values, minimum and maximum digit lengths,
 * and maximum supported decimals, as well as the Java classes their corresponding to.
 */
@Getter
public enum DataType
{
    TINYINT(-127, 128, byte.class, Byte.class),
    SMALLINT(-32768, 32767, short.class, Short.class),
    MEDIUMINT(-8388608, 8388607, int.class, Integer.class),
    INT(-214783648, 2147483647, int.class, Integer.class),
    BIGINT(-9223372036854775808D, 9223372036854775807D, BigInteger.class),
    DECIMAL(0, 65, 30, float.class, Float.class),
    FLOAT(-3.402823466E+38D, 3.402823466E+38, 0, 0, 7, float.class, Float.class),
    DOUBLE(-2.2250738585072014E-308D, 2.2250738585072014E-308D, double.class, Double.class),

    BIT(0, 64, 0, byte.class, Byte.class), //M - number of bits.
    CHAR(0, 255, char.class, Character.class),
    VARCHAR(0, 65535, char[].class, Character[].class), //M - number of bytes.
    BINARY(0, 255, byte.class, Byte.class),
    VARBINARY(0, 65535, byte[].class, Byte[].class),

    TINYBLOB(0, 255, Blob.class), //All measurements are in bytes for Blobs.
    BLOB(0, 65535 -1, Blob.class),
    MEDIUMBLOB(0, 16777215, Blob.class),
    LONGBLOB(0, 4294967295D, Blob.class),

    TINYTEXT(0, 255, String.class), //All measurements are in characters for text.
    TEXT(0, 65535, String.class),
    MEDIUMTEXT(0, 16777215, String.class),
    LONGTEXT(0, 4294967295D, String.class),

    DATE(Date.class),
    TIME(String.class),
    DATETIME(Date.class),
    YEAR(int.class, Integer.class),

    JSON,
    ENUM(Enum.class),
    BOOLEAN(boolean.class, Boolean.class);

    /**
     * A list of all Java classes that correspond to the SQL data type.
     */
    ArrayList<Class> correspondingClasses = Lists.newArrayList();


    //Range of objects.
    /**
     * Minimum value accepted by MariaDB of the data type it's being stored in.
     */
    double minimumAcceptedValue;

    /**
     * Maximum value accept by MariaDB of the data type it's being stored in.
     */
    double maximumAcceptValue;

    //For DECIMAL and FLOAT.
    /**
     * Maximum supported decimals the data type has. If it has none then the value
     * will remain as 0.
     */
    int supportedDecimals = 0;

    /**
     * Minimum digit length, If the data type has none it will remain as 0.
     */
    int minimumDigitSize = 0;

    /**
     * Maximum digit length, if the data type has none it will remain as 0.
     */
    int maximumDigitSize = 0;

    DataType(Class... correspondingClasses)
    {
        this.correspondingClasses.addAll(Arrays.asList(correspondingClasses));
    }


    DataType(double minimumAcceptedValue, double maximumAcceptValue, Class... correspondingClasses)
    {
        this(correspondingClasses);
        this.minimumAcceptedValue = minimumAcceptedValue;
        this.maximumAcceptValue = maximumAcceptValue;
    }

    DataType(int minimumDigitSize, int maximumDigitSize, int supportedDecimals, Class... correspondingClasses)
    {
        this.minimumDigitSize = minimumDigitSize;
        this.maximumDigitSize = maximumDigitSize;
        this.supportedDecimals = supportedDecimals;
        this.correspondingClasses.addAll(Arrays.asList(correspondingClasses));
    }

    DataType(double minimumAcceptedValue, double maximumAcceptValue, int minimumDigitSize, int maximumDigitSize, int supportedDecimals, Class... correspondingClasses)
    {
        this(minimumAcceptedValue, maximumAcceptValue, correspondingClasses);
        this.minimumDigitSize = minimumDigitSize;
        this.maximumDigitSize = maximumDigitSize;
        this.supportedDecimals = supportedDecimals;
    }
}
