package io.pucman.server.locale;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.TextComponent;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class FormatTest
{
    private String header = "---";
    private LinkedList<String> stringContent = Lists.newLinkedList(Arrays.asList("first", "second", "third"));
    private String footer = "===";

    private TextComponent componentHeader = new TextComponent(header);
    private LinkedList<TextComponent> componentContent = stringContent.stream().map(TextComponent::new).collect(Collectors.toCollection(Lists::newLinkedList));
    private TextComponent componentFooter = new TextComponent(footer);

    @Test
    public void stringPaginateTest()
    {
        StringBuilder sb = new StringBuilder(header).append("\n");
        stringContent.forEach(s -> sb.append(s).append("\n"));
        sb.append(footer);
        LinkedHashMap<Integer, String> pages = Format.paginate(stringContent, header, footer, 10);
        Assert.assertEquals(sb.toString(), pages.get(1));
    }

    @Test
    public void textComponentPaginateTest()
    {
        LinkedList<TextComponent> page = Lists.newLinkedList();
        page.add(componentHeader);
        page.addAll(componentContent);
        page.add(componentFooter);

        Assert.assertEquals(page, Format.paginate(componentContent, componentHeader, componentFooter, 10).get(1));
    }


}
