package com.softwire.training.shipit.utils;

import com.softwire.training.shipit.exception.XMLParseException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLParsingUtils
{
    private static Node getSingleElement(String name, NodeList nodeList) throws XMLParseException
    {
        if (nodeList.getLength() != 1)
        {
            throw new XMLParseException("Expected single element with name: " + name);
        }
        return nodeList.item(0);
    }

    public static Node getSingleElementByTagName(Element root, String name) throws XMLParseException
    {
        return getSingleElement(name, root.getElementsByTagName(name));
    }

    public static String getSingleTextElementByTagName(Element root, String name) throws XMLParseException
    {
        return getSingleElementByTagName(root, name).getTextContent().trim();
    }

    public static int getSingleIntElementByTagName(Element root, String name) throws XMLParseException
    {
        try
        {
            return Integer.valueOf(XMLParsingUtils.getSingleTextElementByTagName(root, name));
        }
        catch (NumberFormatException e)
        {
            throw new XMLParseException("Unable to construct OutboundOrder object", e);
        }
    }
}
