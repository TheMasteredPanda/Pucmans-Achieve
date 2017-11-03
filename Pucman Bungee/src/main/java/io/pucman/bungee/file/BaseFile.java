package io.pucman.bungee.file;

import io.pucman.bungee.PLibrary;
import io.pucman.bungee.locale.Format;
import io.pucman.common.exception.DeveloperException;
import io.pucman.common.exception.TryUtil;
import io.pucman.common.generic.GenericUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

/**
 * Wrapper for managing files,
 */
@ParametersAreNonnullByDefault
@Getter
public class BaseFile
{
    private PLibrary lib = PLibrary.get();
    private Plugin instance;
    private String name;
    private File file;
    protected Configuration configuration;
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
    @SneakyThrows
    public synchronized void load()
    {
        if (!this.getInstance().getDataFolder().exists()) {
            this.getInstance().getDataFolder().mkdir();
        }

        if (this.getInstance().getResourceAsStream(this.getName()) != null && !this.getFile().exists()) {
            InputStream is = this.getInstance().getResourceAsStream(this.getName());
            OutputStream os = new FileOutputStream(this.getFile());

            byte[] buffer = new byte[1024];
            int bytesRead;

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
    @Override @SneakyThrows
    public void populate(Class<?> clazz, Object instance)
    {
        this.lib.debug(this, "Attempting to populate class " + clazz.getName() + ".");
        for (Field f : clazz.getFields()) {
            this.lib.debug(this, "Iteration landed at " + f.getName() + ".");
            if (!f.isAnnotationPresent(ConfigPopulate.class)) {
                this.lib.debug(this, f.getName() + " does not have the correct annotation.");
                continue;
            }

            this.lib.debug(this, f.getName() + " does have the correct annotation.");

            ConfigPopulate annotation = f.getAnnotation(ConfigPopulate.class);

            String value = this.getConfiguration().getString(annotation.value(), null);

            if (value == null) {
                this.lib.debug(this, "Value is null in annotation over " + f.getName() + ".");
                throw new DeveloperException("Key " + annotation.value() + ". Was not found in file " + this.getName() + ".");
            }

            if (!GenericUtil.caseable(value, f.getType())) {
                this.lib.debug(this, "Can't cast field type " + f.getName() + " do the value in the config.");
                throw new DeveloperException("Value corresponding to key " + annotation.value() + " could not be assigned to field " + f.getName() + " as it's type, " + f.getType().getName() + " could not be casted to the value " + value.toString() + ".");
            }

            this.lib.debug(this, "Setting the field " + f.getName() + " accessible.");
            f.setAccessible(true);

            if (f.getType().equals(String.class) && annotation.format()) {
                this.lib.debug(this, "Setting the value as a String, formatted, to field " + f.getName() + ". Type of field: " + f.getType().getName() + ".");
                f.set(instance, Format.color(this.PLUGIN_MESSAGE_FORMAT.replace("{prefix}", this.PLUGIN_PREFIX).replace("{message}", value)));
                continue;
            } else if (f.getType().equals(String.class) && annotation.color()) {
                this.lib.debug(this, "Setting the value as a string, colored, to field " + f.getName() + ".");
                f.set(instance, Format.color(value));
                continue;
            }

            this.lib.debug(this, "Setting field " + f.getName() + ".");
            TryUtil.sneaky(() -> f.set(instance, value));
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
