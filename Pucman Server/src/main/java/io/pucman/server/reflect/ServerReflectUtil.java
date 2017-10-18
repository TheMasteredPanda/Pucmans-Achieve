package io.pucman.server.reflect;

import io.pucman.common.test.reflect.ReflectUtil;

/**
 * Consider this an extention of ReflectUtil, but for servers specifically.
 *
 * @see io.pucman.common.test.reflect.ReflectUtil
 */
public class ServerReflectUtil
{
    private static final String NMS_PACKAGE = "net.minecraft.server";
    private static final String BUKKIT_PACKAGE = "org.bukkit";
    private static final String OBC_PACKAGE = BUKKIT_PACKAGE + ".craftbukkit";

    public static Class<?> getNMSClass(String clazz)
    {
        return ReflectUtil.getClass(NMS_PACKAGE + "." + clazz);
    }

    public static Class<?> getOBCClass(String clazz)
    {
        return ReflectUtil.getClass(OBC_PACKAGE + "." + clazz);
    }

    public static Class<?> getNMSUtil(String name)
    {
        return ReflectUtil.getClass(NMS_PACKAGE + ".util." + name);
    }

    public static Class<?> getBukkitClass(String name)
    {
        return ReflectUtil.getClass(BUKKIT_PACKAGE + "." + name);
    }
}
