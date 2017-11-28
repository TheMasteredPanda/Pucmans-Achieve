package io.pucman.server.file.config;

import com.google.gson.Gson;
import io.pucman.common.exception.TryUtil;
import io.pucman.common.reflect.ReflectUtil;
import io.pucman.common.reflect.accessors.ConstructorAccessor;
import io.pucman.common.reflect.accessors.FieldAccessor;

import java.io.*;
import java.util.Map;

/**
 * Used when instantiating a new instance of a configuration class.
 * This just makes it easier to read and write in the json file.
 */
public class JsonProvider extends ConfigurationProvider
{
    private final ConstructorAccessor<Configuration> MAP_CONFIURATION_CONSTRUCTOR = ReflectUtil.getConstructor(Configuration.class, ReflectUtil.Type.DECLARED, Map.class, Configuration.class);
    private final FieldAccessor<Map<String, Object>> CONFIGURATION_SELF_FIELD = ReflectUtil.getField(Configuration.class, "self", ReflectUtil.Type.DECLARED);
    private final Gson gson = new Gson();

    @Override
    public void save(Configuration configuration, File file) throws IOException
    {
        this.save(configuration, new FileWriter(file));
    }

    @Override
    public void save(Configuration configuration, Writer writer)
    {
        gson.toJson(gson.toJson(this.CONFIGURATION_SELF_FIELD.get(configuration)), writer);
        TryUtil.sneaky(writer::close);
    }

    @Override
    public Configuration load(File file) throws IOException
    {
        return this.load(file, null);
    }

    @Override
    public Configuration load(File file, Configuration configuration) throws IOException
    {
        return this.load(new FileReader(file), configuration);
    }

    @Override
    public Configuration load(Reader reader)
    {
        return this.load(reader, null);
    }

    @Override
    public Configuration load(Reader reader, Configuration configuration)
    {
        return this.MAP_CONFIURATION_CONSTRUCTOR.call(gson.fromJson(reader, Map.class), configuration);
    }

    @Override
    public Configuration load(InputStream inputStream)
    {
        return this.load(inputStream, null);
    }

    @Override
    public Configuration load(InputStream inputStream, Configuration configuration)
    {
        return this.MAP_CONFIURATION_CONSTRUCTOR.call(gson.fromJson(new InputStreamReader(inputStream), Map.class), configuration);
    }

    @Override
    public Configuration load(String s)
    {
        return this.load(s, null);
    }

    @Override
    public Configuration load(String s, Configuration configuration)
    {
        return this.MAP_CONFIURATION_CONSTRUCTOR.call(gson.fromJson(s, Map.class), configuration);
    }
}
