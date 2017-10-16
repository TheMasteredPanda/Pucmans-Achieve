package io.pucman.bungee.locale;

import com.google.common.collect.LinkedListMultimap;
import io.pucman.common.exception.UtilException;
import io.pucman.common.generic.GenericUtil;
import net.md_5.bungee.api.ChatColor;

import java.util.List;

public final class Format
{
    private Format()
    {
        throw new UtilException();
    }

    public static String color(String message)
    {
        return ChatColor.translateAlternateColorCodes('&', message);
    }


    public static <T> LinkedListMultimap<Integer, T> paginate(Class<T> value, List<T> content, T header, T footer, int contentPerPage)
    {
        LinkedListMultimap<Integer, T> pages = LinkedListMultimap.create();
        int contentCount = 0;
        int pageNumber = 1;

        for (T v : content) {
            if (contentPerPage > content.size() && (contentCount + content.size()) <= contentPerPage) {
                pages.putAll(pageNumber, GenericUtil.cast(content.toArray(new Object[content.size()])));
                contentCount = contentPerPage;
            }

            if (contentCount == contentPerPage) {
                if (footer != null) {
                    pages.put(pageNumber, footer);
                }

                pageNumber++;
                contentCount = 0;

                if (header != null) {
                    pages.put(pageNumber, header);
                }
            }

            pages.put(pageNumber, v);
            content.remove(v);
        }

        return pages;
    }
}
