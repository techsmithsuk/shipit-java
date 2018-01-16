package com.softwire.training.shipit.model;

import com.softwire.training.shipit.exception.ValidationException;
import com.softwire.training.shipit.exception.XMLParseException;
import com.softwire.training.shipit.utils.XMLParsingUtils;
import com.softwire.training.shipit.validators.InboundManifestValidator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class InboundManifest
{
    private int warehouseId;
    private String gcp;
    private List<OrderLine> orderLines;

    public InboundManifest(int warehouseId, String gcp, List<OrderLine> orderLines)
    {
        this.warehouseId = warehouseId;
        this.gcp = gcp;
        this.orderLines = orderLines;
    }

    public static InboundManifest parseXML(Element element) throws ValidationException, XMLParseException
    {
        String gcp = XMLParsingUtils.getSingleTextElementByTagName(element, "gcp");
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

        InboundManifest inboundManifest = new InboundManifest(warehouseId, gcp, orderLines);

        (new InboundManifestValidator()).validate(inboundManifest);

        return inboundManifest;
    }

    public int getWarehouseId()
    {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId)
    {
        this.warehouseId = warehouseId;
    }

    public String getGcp()
    {
        return gcp;
    }

    public void setGcp(String gcp)
    {
        this.gcp = gcp;
    }

    public List<OrderLine> getOrderLines()
    {
        return orderLines;
    }

    public void setOrderLines(List<OrderLine> orderLines)
    {
        this.orderLines = orderLines;
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

        InboundManifest that = (InboundManifest) o;

        return new EqualsBuilder()
                .append(warehouseId, that.warehouseId)
                .append(gcp, that.gcp)
                .append(orderLines, that.orderLines)
                .isEquals();
    }

    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(warehouseId)
                .append(gcp)
                .append(orderLines)
                .toHashCode();
    }

    public String toString()
    {
        return new ToStringBuilder(this)
                .append("warehouseId", warehouseId)
                .append("gcp", gcp)
                .append("orderLines", orderLines)
                .toString();
    }
}
