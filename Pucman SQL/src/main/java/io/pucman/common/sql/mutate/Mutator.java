package io.pucman.common.sql.mutate;

/**
 * Template Codec class.
 * @param <T> - Serialized type.
 * @param <T1> - Deserialized type.
 */
public class Mutator<T, T1>
{
    /**
     * Convert the deserialized type into a serialized instance.
     * @param deserializedType - deserialized instance.
     * @return serialized instance.
     */
    T to(T1 deserializedType)
    {
        return null;
    }

    /**
     * Convert the serialized type into a deserialized type.
     * @param serializedType - serialized instance.
     * @return deserialized instance.
     */
    T1 from (T serializedType)
    {
        return null;
    }
}
