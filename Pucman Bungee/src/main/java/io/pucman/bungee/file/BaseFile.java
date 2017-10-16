package io.pucman.bungee.file;

import io.pucman.bungee.locale.Format;
import io.pucman.common.exception.DeveloperException;
import io.pucman.common.exception.TryUtil;
import io.pucman.common.generic.GenericUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Wrapper for managing files,
 */
@ParametersAreNonnullByDefault
@Getter
public class BaseFile
{
    private Plugin instance;
    private String name;
    private File file;
    @Setter(value = AccessLevel.PROTECTED)
    private Configuration configuration;
    private Class<? extends ConfigurationProvider> provider;

    public BaseFile(Plugin instance, String name, File parent, Class<? extends ConfigurationProvider> provider)
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
    public synchronized void load()
    {
        if (instance.getResourceAsStream(name) != null) {
            TryUtil.sneaky(() -> Files.copy(instance.getResourceAsStream(name), file.toPath(), StandardCopyOption.ATOMIC_MOVE));
        }

        if (this.configuration == null) {
            this.configuration = TryUtil.sneaky(() -> ConfigurationProvider.getProvider(provider).load(this.file));
        }
    }

    /**
     * Populates fields within the class with the value corresponding to the key set in the ConfigPopulate annotation.
     * @param clazz - the class.
     */
    public void populate(Class<?> clazz)
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
