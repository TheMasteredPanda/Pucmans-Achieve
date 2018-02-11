package io.pucman.bungee.file;

import com.google.common.collect.Lists;
import io.pucman.bungee.PLibrary;
import io.pucman.bungee.locale.Format;
import io.pucman.common.exception.DeveloperException;
import io.pucman.common.exception.TryUtil;
import io.pucman.common.generic.GenericUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
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
import java.util.List;
import java.util.regex.Pattern;

/**
 * Wrapper for managing files,
 */
@Getter
@ParametersAreNonnullByDefault
public class BaseFile
{
    private PLibrary lib = PLibrary.get();
    private Plugin instance;
    private String name;
    private File file;
    protected Configuration configuration;
    private Class<? extends ConfigurationProvider> provider;
    private LinkedList<ReplacementEntry> replacementEntries = Lists.newLinkedList();
    private Pattern node = Pattern.compile("");

    public BaseFile(Plugin instance, String name, File parent, Class<? extends ConfigurationProvider> provider)
    {
        this.instance = instance;
        this.name = name;
        file = new File(parent, name);
        this.provider = provider;
    }

    /**
     * Adds an array of replacement entries to the list.
     * @param entries - replacement entries.
     */
    public void replacers(ReplacementEntry... entries)
    {
        replacementEntries.addAll(Arrays.asList(entries));
    }


    /**
     * Loads the file, if the file is not present in the parent directory but has
     * been compiled with the plugin, it will copy it over.
     */
    @SneakyThrows
    public synchronized void load()
    {
        if (!getInstance().getDataFolder().exists()) {
            lib.debug(this, "Creating parent directory.");
            getInstance().getDataFolder().mkdir();
        }

        if (getInstance().getResourceAsStream(getName()) != null && !getFile().exists()) {
            lib.debug(this, "File " + getName() + " not found, creating one.");
            InputStream is = getInstance().getResourceAsStream(getName());
            OutputStream os = new FileOutputStream(getFile());

            byte[] buffer = new byte[1024];
            int read;

            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }

            is.close();
            os.close();
        }

        if (configuration == null) {
            lib.debug(this, "Loading file: " + getFile() + " as a configuration file.");
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile());
        }
    }

    /**
     * Populates fields within the class with the value corresponding to the key set in the ConfigPopulate annotation.
     * @param instance - the instance of the class.
     */
    @SneakyThrows
    public  <V> void populate(V instance)
    {
        Class clazz = instance.getClass();
        lib.debug(this, "Attempting to populate class " + clazz.getName() + ".");

        List<Field> fieldList = Lists.newArrayList(clazz.getDeclaredFields());
        fieldList.addAll(Arrays.asList(clazz.getFields()));

        for (Field f : fieldList) {
            lib.debug(this, "Iteration landed at " + f.getName() + ".");
            if (!f.isAnnotationPresent(ConfigPopulate.class)) {
                lib.debug(this, f.getName() + " does not have the correct annotation.");
                continue;
            }

            Class type = f.getType();

            lib.debug(this, f.getName() + " does have the correct annotation.");

            ConfigPopulate annotation = f.getAnnotation(ConfigPopulate.class);

            String value = getConfiguration().getString(annotation.value(), null);

            if (value == null) {
                lib.debug(this, "Value is null in annotation over " + f.getName() + ".");
                throw new DeveloperException("Key " + annotation.value() + ". Was not found in file " + getName() + ".");
            }

            f.setAccessible(true);

            if (!GenericUtil.castable(value, type)) {
                lib.debug(this, "Can't cast field type " + f.getName() + " do the value in the config.");
                throw new DeveloperException("Value corresponding to key " + annotation.value() + " could not be assigned to field " + f.getName() + " as it's type, " + f.getType().getName() + " could not be casted to the value " + value.toString() + ".");
            }

            lib.debug(this, "Setting the field " + f.getName() + " accessible.");
            f.setAccessible(true);



            if ((type.equals(String.class) || type.equals(TextComponent.class))) {
                if (annotation.colour()) {
                    lib.debug(this, "Setting the value as a string, colored, to field " + f.getName() + ".");
                    f.set(instance, type.equals(TextComponent.class) ? TextComponent.fromLegacyText(Format.color(value)) : Format.color(value));
                    continue;
                }

                if (annotation.format()) {
                    String stringValue = Format.color(value);

                    for (ReplacementEntry entry : replacementEntries) {
                        stringValue = stringValue.replace(entry.getPlaceholder(), entry.getValue().toString());
                    }

                    f.set(instance, type.equals(TextComponent.class) ? TextComponent.fromLegacyText(stringValue) : stringValue);
                    continue;
                }
            }

            lib.debug(this, "Setting field " + f.getName() + ".");
            TryUtil.sneaky(() -> f.set(instance, value));
        }
    }

    /**
     * Saves the file.
     */
    public synchronized void close()
    {
        if (configuration != null && file != null) {
            TryUtil.sneaky(() -> ConfigurationProvider.getProvider(provider).save(configuration, file));
        }
    }

    /**
     * To get a value.
     * @param type - type of value.
     * @param node - location of value in the file.
     * @param <T> - generic type.
     * @return the value if present, else null.
     */
    public <T> T get(Class<T> type, String node)
    {
        return GenericUtil.cast(configuration.get(node), type);
    }
}
