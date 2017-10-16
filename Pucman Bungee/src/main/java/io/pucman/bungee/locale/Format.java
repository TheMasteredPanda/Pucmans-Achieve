package io.pucman.bungee.locale;

import com.google.common.collect.LinkedListMultimap;
import io.pucman.common.exception.UtilException;
import io.pucman.common.generic.GenericUtil;
import net.md_5.bungee.api.ChatColor;

import java.util.List;

/**
 * To format objects easily, often strings or BaseComponents.
 */
public final class Format
{
    private Format()
    {
        throw new UtilException();
    }

    /**
     * To color a message.
     * @param message - the uncolored message.
     * @return the colored message.
     */
    public static String color(String message)
    {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * For parsing a list of content into a book like structure whereas the page numbers
     * corresponds to a list of content.
     * @param value - the content type.
     * @param content - the content list.
     * @param header - the page header.
     * @param footer - the page footer.
     * @param contentPerPage - the amount of content per page.
     * @param <T> - the generic type.
     * @return a linked list multimap with the structure <pageNumber, LinkedList<T>>
     */
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
