package com.softwire.training.shipit.model;

import com.softwire.training.shipit.exception.ValidationException;
import com.softwire.training.shipit.exception.XMLParseException;
import com.softwire.training.shipit.utils.XMLParsingUtils;
import com.softwire.training.shipit.validators.OutboundOrderValidator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class OutboundOrder
{
    private int warehouseId;
    private List<OrderLine> orderLines;

    public OutboundOrder(int warehouseId, List<OrderLine> orderLines)
    {
        this.warehouseId = warehouseId;
        this.orderLines = orderLines;
    }

    public static OutboundOrder parseXML(Element element) throws XMLParseException, ValidationException
    {
        int warehouseId = XMLParsingUtils.getSingleIntElementByTagName(element, "warehouseId");

        NodeList outboundOrderLineNodes = XMLParsingUtils
                .getSingleElementByTagName(element, "orderLines")
                .getChildNodes();

        List<OrderLine> orderLines = new ArrayList<OrderLine>();
        for (int i = 0; i < outboundOrderLineNodes.getLength(); i++)
        {
            Node childNode = outboundOrderLineNodes.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE)
            {
                continue;
            }

            orderLines.add(OrderLine.parseXML((Element) childNode));
        }

        OutboundOrder outboundOrder = new OutboundOrder(warehouseId, orderLines);

        (new OutboundOrderValidator()).validate(outboundOrder);

        return outboundOrder;
    }

    public int getWarehouseId()
    {
        return warehouseId;
    }

    public List<OrderLine> getOrderLines()
    {
        return orderLines;
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

        OutboundOrder that = (OutboundOrder) o;

        return new EqualsBuilder()
                .append(warehouseId, that.warehouseId)
                .append(orderLines, that.orderLines)
                .isEquals();
    }

    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(warehouseId)
                .append(orderLines)
                .toHashCode();
    }

    public String toString()
    {
        return new ToStringBuilder(this)
                .append("warehouseId", warehouseId)
                .append("orderLines", orderLines)
                .toString();
    }
}
