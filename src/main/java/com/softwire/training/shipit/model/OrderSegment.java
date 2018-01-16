package com.softwire.training.shipit.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class OrderSegment implements RenderableAsXML
{
    private Company company;
    private List<InboundOrderLine> inboundOrderLines;

    public OrderSegment(Company company, List<InboundOrderLine> inboundOrderLines)
    {
        this.company = company;
        this.inboundOrderLines = inboundOrderLines;
    }

    public Company getCompany()
    {
        return company;
    }

    public List<InboundOrderLine> getInboundOrderLines()
    {
        return inboundOrderLines;
    }

    public String renderXML()
    {
        StringBuilder renderedOrderLines = new StringBuilder();
        for (InboundOrderLine inboundOrderLine : inboundOrderLines)
        {
            renderedOrderLines.append(inboundOrderLine.renderXML());
        }
        return "<segment>" +
                company.renderXML() +
                "<orderLines>" + renderedOrderLines.toString() + "</orderLines>" +
                "</segment>";
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

        OrderSegment that = (OrderSegment) o;

        return new EqualsBuilder()
                .append(company, that.company)
                .append(inboundOrderLines, that.inboundOrderLines)
                .isEquals();
    }

    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(company)
                .append(inboundOrderLines)
                .toHashCode();
    }

    public String toString()
    {
        return new ToStringBuilder(this)
                .append("company", company)
                .append("orderLines", inboundOrderLines)
                .toString();
    }
}
