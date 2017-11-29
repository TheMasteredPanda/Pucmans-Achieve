package io.pucman.server.file;

import io.pucman.common.exception.DeveloperException;
import io.pucman.common.exception.TryUtil;
import io.pucman.common.generic.GenericUtil;
import io.pucman.server.file.config.Configuration;
import io.pucman.server.file.config.ConfigurationProvider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

/**
 * Wrapper for managing files,
 */

@Getter
@ThreadSafe
@ParametersAreNonnullByDefault
public class BaseFile
{
    private JavaPlugin instance;
    private String name;
    private File file;
    @Setter(value = AccessLevel.PROTECTED)
    private Configuration configuration;
    private Class<? extends ConfigurationProvider> provider;

    public BaseFile(JavaPlugin instance, String name, File parent, Class<? extends ConfigurationProvider> provider)
    {
        this.instance = instance;
        this.name = name;
        this.file = new File(parent, name);
        this.provider = provider;
    }

    /**
     * Loads the file, if the file is not present in the parent directory but has
     * been compiled with the plugin, it will copy it over.
     */
    @SneakyThrows
    public synchronized void load()
    {
        if (this.instance.getResource(this.getName()) != null && !this.file.exists()) {
            InputStream is = this.instance.getResource(this.getName());
            OutputStream os = new FileOutputStream(this.getFile());

            byte[] buffer = new byte[1024];
            int bytesRead = 0;

            while ((bytesRead = is.read()) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            is.close();
            os.close();
        }

        if (this.configuration == null) {
            this.configuration = TryUtil.sneaky(() -> ConfigurationProvider.getProvider(provider).load(this.file), Configuration.class);
        }
    }

    /**
     * Populates fields within the class with the value corresponding to the key set in the ConfigPopulate annotation.
     * @param clazz - the class.
     */
    public synchronized void populate(Class<?> clazz)
    {
        for (Field f : clazz.getFields()) {
            if (!f.isAnnotationPresent(ConfigPopulate.class)) {
                continue;
            }

            ConfigPopulate annotation = f.getAnnotation(ConfigPopulate.class);

            Object value = this.configuration.get(annotation.value(), null);

            if (value == null) {
                throw new DeveloperException("Key " + annotation.value() + ". Was not found in file " + this.name + ".");
            }

            if (!GenericUtil.caseable(value, f.getType())) {
                throw new DeveloperException("Value corresponding to key " + annotation.value() + " could not be assigned to field " + f.getName() + " as it's type, " + f.getType().getName() + " could not be casted to the value " + value.toString() + ".");
            }

            f.setAccessible(true);

            if (f.getType().equals(String.class) && annotation.color()) {
                TryUtil.sneaky(() -> f.set(clazz, Format.color(((String) value))));
                continue;
            }

            TryUtil.sneaky(() -> f.set(clazz, value));
        }
    }

    /**
     * Saves the file.
     */
    public synchronized void close()
    {
        if (this.configuration != null && this.file != null) {
            TryUtil.sneaky(() -> ConfigurationProvider.getProvider(provider).save(this.configuration, this.file));
        }
    }
}
