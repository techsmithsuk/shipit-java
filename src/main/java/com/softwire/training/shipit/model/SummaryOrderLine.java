package com.softwire.training.shipit.model;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SummaryOrderLine implements RenderableAsXML
{
    private String gtin;
    private String name;
    private int quantity;

    public SummaryOrderLine(String gtin, String name, int quantity)
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

    @Override
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

        SummaryOrderLine summaryOrderLine = (SummaryOrderLine) o;

        return new EqualsBuilder()
                .append(quantity, summaryOrderLine.quantity)
                .append(gtin, summaryOrderLine.gtin)
                .append(name, summaryOrderLine.name)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(gtin)
                .append(name)
                .append(quantity)
                .toHashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("gtin", gtin)
                .append("name", name)
                .append("quantity", quantity)
                .toString();
    }
}
