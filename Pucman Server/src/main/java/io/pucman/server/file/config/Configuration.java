package io.pucman.server.file.config;


import com.google.common.collect.Sets;
import io.pucman.common.generic.GenericUtil;

import java.util.*;

/**
 * Configuration system that is mostly copied from BungeeCord, authored by md_5.
 */
public final class Configuration
{
    /**
     * Separator for each node.
     */
    private static final char SEPARATOR = '.';

    /**
     * The data.
     */
    final Map<String, Object> self;

    /**
     * Default configuration options.
     */
    private final Configuration defaults;

    public Configuration()
    {
        this(null);
    }

    public Configuration(Configuration defaults)
    {
        this(new LinkedHashMap<String, Object>(), defaults);
    }

    public Configuration(Map<?, ?> map, Configuration defaults)
    {
        this.self = new LinkedHashMap<>();
        this.defaults = defaults;

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = (entry.getKey() == null) ? "null" : entry.getKey().toString();

            if (entry.getValue() instanceof Map) {
                this.self.put(key, new Configuration((Map) entry.getValue(), (defaults == null) ? null : defaults.getSection(key)));
            } else {
                this.self.put(key, entry.getValue());
            }
        }
    }

    /**
     * For getting a section of a configuration.
     * @param path - the path to that section in nodes.
     * @return the configuration section.
     */
    private Configuration getSectionFor(String path)
    {
        int index = path.indexOf(SEPARATOR);
        if (index == -1) {
            return this;
        }

        String root = path.substring(0, index);
        Object section = self.get(root);
        if (section == null) {
            section = new Configuration((defaults == null) ? null : defaults.getSection(path));
            self.put(root, section);
        }

        return (Configuration) section;
    }

    //???
    private String getChild(String path)
    {
        int index = path.indexOf(SEPARATOR);
        return (index == -1) ? path : path.substring(index + 1);
    }

    /**
     * Gets an object, if the object is not present it will return the default object.
     * @param path - path to the object.
     * @param def - default value.
     * @param <T> - generic type.
     * @return they object if present, else the default object.
     */
    public <T> T get(String path, T def)
    {
        Configuration section = getSectionFor(path);
        Object val;
        if (section == this) {
            val = self.get(path);
        } else {
            val = section.get(getChild(path), def);
        }

        if (val == null && def instanceof Configuration) {
            self.put(path, def);
        }

        return (val != null) ? GenericUtil.cast(val) : def;
    }

    /**
     * To check if a value exists in the configuration.
     * @param path - the path of the value.
     * @return true if exists, else false.
     */
    public boolean contains(String path)
    {
        return get(path, null) != null;
    }

    /**
     * Gets a value.
     * @param path - the path of the value.
     * @return if present the value, else null.
     */
    public Object get(String path)
    {
        return get(path, getDefault(path));
    }

    //???
    public Object getDefault(String path)
    {
        return (defaults == null) ? null : defaults.get(path);
    }

    /**
     * For setting a value in a configuration.
     * @param path - the path of the value.
     * @param value - the value.
     */
    public void set(String path, Object value)
    {
        if (value instanceof Map) {
            value = new Configuration((Map) value, (defaults == null) ? null : defaults.getSection(path));
        }

        Configuration section = getSectionFor(path);
        if (section == this) {
            if (value == null) {
                self.remove(path);
            } else {
                self.put(path, value);
            }
        } else {
            section.set(getChild(path), value);
        }
    }

    /**
     * Gets a section of the configuration.
     * @param path - path of that section in the configuration.
     * @return configuration instance.
     */
    public Configuration getSection(String path)
    {
        Object def = getDefault(path);
        return (Configuration) get(path, (def instanceof Configuration) ? def : new Configuration((defaults == null) ? null : defaults.getSection(path)));
    }

    /**
     * Gets keys, not deep by default.
     *
     * @return top level keys for this section
     */
    public Collection<String> getKeys()
    {
        return Sets.newLinkedHashSet(self.keySet());
    }

    /**
     * Gets a byte value.
     * @param path - path to value.
     * @return byte value if present, else 0.
     */
    public byte getByte(String path)
    {
        Object def = getDefault(path);
        return getByte(path, (def instanceof Number) ? ((Number) def).byteValue() : 0);
    }

    /**
     * Gets a byte value.
     * @param path - path to value.
     * @param def - default value.
     * @return byte value if present, else default value.
     */
    public byte getByte(String path, byte def)
    {
        Object val = get(path, def);
        return (val instanceof Number) ? ((Number) val).byteValue() : def;
    }

    /**
     * Gets a byte list.
     * @param path - path to value.
     * @return byte list if present, else empty list.
     */
    public List<Byte> getByteList(String path)
    {
        List<?> list = getList(path);
        List<Byte> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).byteValue());
            }
        }

        return result;
    }

    /**
     * Gets a short value.
     * @param path - path to value.
     * @return short value if present, else 0.
     */
    public short getShort(String path)
    {
        Object def = getDefault(path);
        return getShort(path, (def instanceof Number) ? ((Number) def).shortValue() : 0);
    }

    /**
     * Gets a short value.
     * @param path - path to value.
     * @param def - default value.
     * @return short value if present, else default value.
     */
    public short getShort(String path, short def)
    {
        Object val = get(path, def);
        return (val instanceof Number) ? ((Number) val).shortValue() : def;
    }

    /**
     * Gets a short list.
     * @param path - path to short list.
     * @return short list if present, else null.
     */
    public List<Short> getShortList(String path)
    {
        List<?> list = getList(path);
        List<Short> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).shortValue());
            }
        }

        return result;
    }

    /**
     * Gets a int value.
     * @param path - path to int value.
     * @return int value if present, else 0.
     */
    public int getInt(String path)
    {
        Object def = getDefault(path);
        return getInt(path, (def instanceof Number) ? ((Number) def).intValue() : 0);
    }

    /**
     * Gets a int value.
     * @param path - path to int value.
     * @param def - default value.
     * @return int value if present, else 0.
     */
    public int getInt(String path, int def)
    {
        Object val = get(path, def);
        return (val instanceof Number) ? ((Number) val).intValue() : def;
    }

    /**
     * Gets a int list.
     * @param path - path to int list.
     * @return int list if present, else empty list.
     */
    public List<Integer> getIntList(String path)
    {
        List<?> list = getList(path);
        List<Integer> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).intValue());
            }
        }

        return result;
    }

    /**
     * Gets a long value.
     * @param path - path to long list.
     * @return long list if present, else empty list.
     */
    public long getLong(String path)
    {
        Object def = getDefault(path);
        return getLong(path, (def instanceof Number) ? ((Number) def).longValue() : 0);
    }

    /**
     * Gets a long value.
     * @param path - path to long value.
     * @param def - default value.
     * @return long value if present, else default value.
     */
    public long getLong(String path, long def)
    {
        Object val = get(path, def);
        return (val instanceof Number) ? ((Number) val).longValue() : def;
    }

    /**
     * Gets a long list.
     * @param path - path to long list.
     * @return long list if present, else empty list.
     */
    public List<Long> getLongList(String path)
    {
        List<?> list = getList(path);
        List<Long> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).longValue());
            }
        }

        return result;
    }

    /**
     * Gets a float value.
     * @param path - path to float value.
     * @return float value if present, else 0.
     */
    public float getFloat(String path)
    {
        Object def = getDefault(path);
        return getFloat(path, (def instanceof Number) ? ((Number) def).floatValue() : 0);
    }

    /**
     * Gets a float value.
     * @param path - path to float value.
     * @param def - default value.
     * @return float value if present, else default value.
     */
    public float getFloat(String path, float def)
    {
        Object val = get(path, def);
        return (val instanceof Number) ? ((Number) val).floatValue() : def;
    }

    /**
     * Gets a float list.
     * @param path - path to float list.
     * @return float list if present, else default value.
     */
    public List<Float> getFloatList(String path)
    {
        List<?> list = getList(path);
        List<Float> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).floatValue());
            }
        }

        return result;
    }

    /**
     * Gets a double value.
     * @param path - path to double value.
     * @return double value if present, else 0.
     */
    public double getDouble(String path)
    {
        Object def = getDefault(path);
        return getDouble(path, (def instanceof Number) ? ((Number) def).doubleValue() : 0);
    }

    /**
     * Gets a double value.
     * @param path - path to double value.
     * @param def - default value.
     * @return double value if present, else 0.
     */
    public double getDouble(String path, double def)
    {
        Object val = get(path, def);
        return (val instanceof Number) ? ((Number) val).doubleValue() : def;
    }

    /**
     * Gets a double list.
     * @param path - path to double list.
     * @return double list if present, else empty list.
     */
    public List<Double> getDoubleList(String path)
    {
        List<?> list = getList(path);
        List<Double> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).doubleValue());
            }
        }

        return result;
    }

    /**
     * Gets a boolean.
     * @param path - path to boolean.
     * @return boolean value if present, else false.
     */
    public boolean getBoolean(String path)
    {
        Object def = getDefault(path);
        return getBoolean(path, (def instanceof Boolean) ? (Boolean) def : false);
    }

    /**
     * Gets a boolean.
     * @param path - path to boolean.
     * @param def - default value.
     * @return boolean value if present, else default value.
     */
    public boolean getBoolean(String path, boolean def)
    {
        Object val = get(path, def);
        return (val instanceof Boolean) ? (Boolean) val : def;
    }

    /**
     * Gets a boolean list.
     * @param path - path to boolean list.
     * @return boolean list if present, else empty list.
     */
    public List<Boolean> getBooleanList(String path)
    {
        List<?> list = getList(path);
        List<Boolean> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Boolean) {
                result.add((Boolean) object);
            }
        }

        return result;
    }

    /**
     * Gets a character.
     * @param path - path to character.
     * @return character value if present, else \u0000.
     */
    public char getChar(String path)
    {
        Object def = getDefault(path);
        return getChar(path, (def instanceof Character) ? (Character) def : '\u0000');
    }

    /**
     * Gets a character.
     * @param path - path to character.
     * @param def - default value.
     * @return character if present, else default value.
     */
    public char getChar(String path, char def)
    {
        Object val = get(path, def);
        return (val instanceof Character) ? (Character) val : def;
    }

    /**
     * Gets a character list.
     * @param path - path to character list.
     * @return character if present, else empty list.
     */
    public List<Character> getCharList(String path)
    {
        List<?> list = getList(path);
        List<Character> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Character) {
                result.add((Character) object);
            }
        }

        return result;
    }

    /**
     * Gets a string.
     * @param path - path to string.
     * @return string if present, else empty string.
     */
    public String getString(String path)
    {
        Object def = getDefault(path);
        return getString(path, (def instanceof String) ? (String) def : "");
    }

    /**
     * Gets a string.
     * @param path - path to string.
     * @param def - default value.
     * @return string if present, else default value.
     */
    public String getString(String path, String def)
    {
        Object val = get(path, def);
        return (val instanceof String) ? (String) val : def;
    }

    /**
     * Gets a string list.
     * @param path - path to string list.
     * @return string list if present, else empty list.
     */
    public List<String> getStringList(String path)
    {
        List<?> list = getList(path);
        List<String> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof String) {
                result.add((String) object);
            }
        }

        return result;
    }

    /**
     * Gets a list.
     * @param path - path to list.
     * @return list if present, else empty list.
     */
    public List<?> getList(String path)
    {
        Object def = getDefault(path);
        return getList(path, (def instanceof List<?>) ? (List<?>) def : Collections.EMPTY_LIST);
    }

    /**
     * Gets a list.
     * @param path - path to list.
     * @param def - default value.
     * @return list if present, else default value.
     */
    public List<?> getList(String path, List<?> def)
    {
        Object val = get(path, def);
        return (val instanceof List<?>) ? (List<?>) val : def;
    }
}