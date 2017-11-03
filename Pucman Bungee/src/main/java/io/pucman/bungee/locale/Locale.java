package io.pucman.bungee.locale;

import com.google.common.collect.Lists;
import io.pucman.bungee.PLibrary;
import io.pucman.bungee.file.BaseFile;
import io.pucman.bungee.file.ConfigPopulate;
import io.pucman.common.exception.DeveloperException;
import io.pucman.common.exception.TryUtil;
import io.pucman.common.generic.GenericUtil;
import lombok.SneakyThrows;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * The concept behind this class is for easy management of the locale
 * in any given plugin, given that it adds the benefit of reducing the
 * amount of 'mess' it has when managing a locale consistency within
 * the plugin, let alone the server.
 * @param <P> - the plugin instance.
 */
@ParametersAreNonnullByDefault
public class Locale<P extends Plugin> extends BaseFile
{
    /**
     * Instane of the library main class.
     */
    private PLibrary lib = PLibrary.get();

    /**
     * Prefix of the plugin.
     */
    public String PLUGIN_PREFIX;

    /**
     * The plugins message format when sending single sentence messages.
     */
    private String PLUGIN_MESSAGE_FORMAT;

    /**
     * The header for an array of messages being sent to the player.
     */
    private String LIST_HEADER_FORMAT;

    /**
     * The footer for an array of messages being sent to the player.
     */
    private String LIST_FOOTER_FORMAT;

    /**
     * The page index for each page that is made, often used in when
     * a book of pages, aka: lists, is made.
     */
    private String PAGE_INDEX_FORMAT;

    public Locale(P instance, String name, File parent)
    {
        super(instance, name, parent, YamlConfiguration.class);
    }

    @Override @SneakyThrows
    public synchronized void load()
    {
        if (!this.getInstance().getDataFolder().exists()) {
            this.lib.debug(this, "Creating parent directory.");
            this.getInstance().getDataFolder().mkdir();
        }

        if (this.getInstance().getResourceAsStream(this.getName()) != null && !this.getFile().exists()) {
            this.lib.debug(this, "File " + this.getName() + " not found, creating one.");
            InputStream is = this.getInstance().getResourceAsStream(this.getName());
            OutputStream os = new FileOutputStream(this.getFile());

            byte[] buffer = new byte[1024];
            int read;

            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }

            is.close();
            os.close();
        }

        if (this.configuration == null) {
            PLibrary.get().debug(this, "Loading file: " + this.getFile() + " as a configuration file.");
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.getFile());
        }

        this.PLUGIN_PREFIX = this.getMessage("Plugin.Prefix");
        this.PLUGIN_MESSAGE_FORMAT = this.getMessage("Plugin.PluginMessageFormat");
        this.LIST_HEADER_FORMAT = this.getMessage("Plugin.ListHeaderFormat");
        this.LIST_FOOTER_FORMAT = this.getMessage("Plugin.ListFooterFormat");
        this.PAGE_INDEX_FORMAT = this.getMessage("Plugin.PageIndexFormat");
    }

    /**
     * Same as BaseFile#populate but allows for formatting to be automatically done.
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
     * Gets the message corresponding to the path.
     * @param path - the node.
     * @return the message.
     */
    public String getMessage(String path)
    {
        return this.getConfiguration().getString(path);
    }

    public LinkedList<String> getHeader()
    {
        return Lists.newLinkedList(Arrays.asList(this.LIST_HEADER_FORMAT.split("\n")));
    }

    public LinkedList<String> getFooter()
    {
        return Lists.newLinkedList(Arrays.asList(this.LIST_FOOTER_FORMAT.split("\n")));
    }
}
