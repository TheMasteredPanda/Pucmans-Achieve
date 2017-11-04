package io.pucman.bungee.locale;

import com.google.common.collect.LinkedListMultimap;
import io.pucman.common.exception.UtilException;
import io.pucman.common.generic.GenericUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

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
     * For replacing a string's placeholders {i} to objects.
     * @param message - string.
     * @param objects - objects.
     * @return formatted message.
     */
    public static String format(String message, Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            message = message.replace("{" + String.valueOf(i) + "]", objects[i].toString());
        }

        return message;
    }

    /**
     * For parsing a list of content into a book like structure whereas the page numbers
     * corresponds to a list of content.
     * @param content - the content list.
     * @param header - the page header.
     * @param footer - the page footer.
     * @param contentPerPage - the amount of content per page.
     * @return a linked list multimap with the structure <pageNumber, LinkedList<TextComponent>>
     */
    public static LinkedListMultimap<Integer, TextComponent> paginate(List<TextComponent> content, TextComponent header, TextComponent footer, int contentPerPage)
    {
        LinkedListMultimap<Integer, TextComponent> pages = LinkedListMultimap.create();
        int contentCount = 0;
        int pageNumber = 1;

        for (TextComponent v : content) {
            if (contentCount == 0) {
                if (header != null && !pages.get(pageNumber).contains(header)) {
                    pages.put(pageNumber, header);
                }
            }

            if (contentPerPage > content.size() && (contentCount + content.size()) <= contentPerPage) {
                for (TextComponent c : content) {
                    pages.put(pageNumber, c);
                }

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

        return GenericUtil.cast(pages);
    }

    /**
     * Same as above, but with strings.
     * @return a linked list multimap with the structure of <pageNumber, LinkedList<String>>
     */
    public static LinkedListMultimap<Integer, String> paginateString(List<String> content, String header, String footer, int contentPerPage)
    {
        LinkedListMultimap<Integer, String> pages = LinkedListMultimap.create();
        int contentCount = 0;
        int pageNumber = 1;

        for (String v : content) {
            if (contentCount == 0) {
                if (header != null && !pages.get(pageNumber).contains(header)) {
                    pages.put(pageNumber, header);
                }
            }

            if (contentPerPage > content.size() && (contentCount + content.size()) <= contentPerPage) {
                for (String c : content) {
                    pages.put(pageNumber, c);
                }

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
