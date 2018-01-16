package com.softwire.training.shipit.model;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class InboundOrderLine implements RenderableAsXML
{
    private String gtin;
    private String name;
    private int quantity;

    public InboundOrderLine(String gtin, String name, int quantity)
    {
        this.gtin = gtin;
        this.name = name;
        this.quantity = quantity;
    }

    public String getGtin()
    {
        return gtin;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public String renderXML()
    {
        return "<orderLine>" +
                "<gtin>" + gtin + "</gtin>" +
                "<name>" + StringEscapeUtils.escapeXml10(name) + "</name>" +
                "<quantity>" + quantity + "</quantity>" +
                "</orderLine>";
    }

    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        InboundOrderLine inboundOrderLine = (InboundOrderLine) o;

        return new EqualsBuilder()
                .append(quantity, inboundOrderLine.quantity)
                .append(gtin, inboundOrderLine.gtin)
                .append(name, inboundOrderLine.name)
                .isEquals();
    }

    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(gtin)
                .append(name)
                .append(quantity)
                .toHashCode();
    }

    public String toString()
    {
        return new ToStringBuilder(this)
                .append("gtin", gtin)
                .append("name", name)
                .append("quantity", quantity)
                .toString();
    }
}
