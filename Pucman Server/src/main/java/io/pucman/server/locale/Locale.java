package io.pucman.server.locale;

import com.google.common.collect.Lists;
import io.pucman.common.exception.DeveloperException;
import io.pucman.common.exception.TryUtil;
import io.pucman.common.generic.GenericUtil;
import io.pucman.server.file.BaseFile;
import io.pucman.server.file.ConfigPopulate;
import io.pucman.server.file.config.Configuration;
import io.pucman.server.file.config.ConfigurationProvider;
import io.pucman.server.file.config.YamlProvider;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

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
public class Locale<P extends JavaPlugin> extends BaseFile
{
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
        super(instance, name, parent, YamlProvider.class);
    }

    @Override @SneakyThrows
    public synchronized void load()
    {
        if (this.getInstance().getResource(this.getName()) != null && !this.getFile().exists()) {
            InputStream is = this.getInstance().getResource(this.getName());
            OutputStream os = new FileOutputStream(this.getFile());

            byte[] buffer = new byte[1024];
            int bytesRead = 0;

            while ((bytesRead = is.read()) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            is.close();
            os.close();
        }

        if (this.getConfiguration() == null) {
            this.setConfiguration(TryUtil.sneaky(() -> ConfigurationProvider.getProvider(this.getProvider()).load(this.getFile()), Configuration.class));
        }

        this.PLUGIN_PREFIX = this.getMessage("Prefix");
        this.PLUGIN_MESSAGE_FORMAT = this.getMessage("PluginMessageFormat");
        this.LIST_HEADER_FORMAT = this.getMessage("ListHeaderFormat");
        this.LIST_FOOTER_FORMAT = this.getMessage("ListFooterFormat");
        this.PAGE_INDEX_FORMAT = this.getMessage("PageIndexFormat");
    }

    /**
     * Same as BaseFile#populate but allows for formatting to be automatically done.
     * @param clazz - the class.
     */
    @Override
    public void populate(Class<?> clazz)
    {
        for (Field f : clazz.getFields()) {
            if (!f.isAnnotationPresent(ConfigPopulate.class)) {
                continue;
            }

            ConfigPopulate annotation = f.getAnnotation(ConfigPopulate.class);

            Object value = this.getConfiguration().get(annotation.value(), null);

            if (value == null) {
                throw new DeveloperException("Key " + annotation.value() + ". Was not found in file " + this.getName() + ".");
            }

            if (!GenericUtil.castable(value, f.getType())) {
                throw new DeveloperException("Value corresponding to key " + annotation.value() + " could not be assigned to field " + f.getName() + " as it's type, " + f.getType().getName() + " could not be casted to the value " + value.toString() + ".");
            }

            f.setAccessible(true);

            if (f.getType().equals(String.class) && annotation.format()) {
                TryUtil.sneaky(() -> f.set(clazz, Format.color(this.PLUGIN_MESSAGE_FORMAT.replace("{prefix}", this.PLUGIN_PREFIX).replace("{message}", ((String) value)))));
                continue;
            } else if (f.getType().equals(String.class) && annotation.color()) {
                TryUtil.sneaky(() -> f.set(clazz, Format.color((String) value)));
                continue;
            }

            TryUtil.sneaky(() -> f.set(clazz, value));
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
