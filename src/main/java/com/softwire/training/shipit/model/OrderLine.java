package com.softwire.training.shipit.model;

import com.softwire.training.shipit.exception.ValidationException;
import com.softwire.training.shipit.exception.XMLParseException;
import com.softwire.training.shipit.utils.XMLParsingUtils;
import com.softwire.training.shipit.validators.OrderLineValidator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.w3c.dom.Element;

public class OrderLine
{
    private String gtin;
    private int quantity;

    public OrderLine(String gtin, int quantity)
    {
        this.gtin = gtin;
        this.quantity = quantity;
    }

    public static OrderLine parseXML(Element root) throws XMLParseException, ValidationException
    {
        String gtin = XMLParsingUtils.getSingleTextElementByTagName(root, "gtin");
        int quantity = XMLParsingUtils.getSingleIntElementByTagName(root, "quantity");

        OrderLine orderLine = new OrderLine(gtin, quantity);

        (new OrderLineValidator()).validate(orderLine);

        return orderLine;
    }

    public String getGtin()
    {
        return gtin;
    }

    public int getQuantity()
    {
        return quantity;
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

        OrderLine that = (OrderLine) o;

        return new EqualsBuilder()
                .append(quantity, that.quantity)
                .append(gtin, that.gtin)
                .isEquals();
    }

    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(gtin)
                .append(quantity)
                .toHashCode();
    }

    public String toString()
    {
        return new ToStringBuilder(this)
                .append("gtin", gtin)
                .append("quantity", quantity)
                .toString();
    }
}
