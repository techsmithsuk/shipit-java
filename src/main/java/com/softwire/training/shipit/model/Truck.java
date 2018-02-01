package com.softwire.training.shipit.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class Truck implements RenderableAsXML
{
    private List<SummaryOrderLine> orderLines;
    private int weightInKg;

    public Truck(List<SummaryOrderLine> orderLines, int weightInKg)
    {
        this.orderLines = orderLines;
        this.weightInKg = weightInKg;
    }

    public List<SummaryOrderLine> getOrderLines()
    {
        return orderLines;
    }

    public int getWeightInKg()
    {
        return weightInKg;
    }

    public String renderXML()
    {
        StringBuilder renderedOrderLines = new StringBuilder();

        for (SummaryOrderLine orderLine : orderLines)
        {
            renderedOrderLines.append(orderLine.renderXML());
        }

        return "<truck>" +
                "<weightInKg>" + weightInKg + "</weightInKg>" +
                "<orderLines>" + renderedOrderLines.toString() + "</orderLines>" +
                "</truck>";
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

        Truck truck = (Truck) o;

        return new EqualsBuilder()
                .append(weightInKg, truck.weightInKg)
                .append(orderLines, truck.orderLines)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(orderLines)
                .append(weightInKg)
                .toHashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("orderLines", orderLines)
                .append("weightInKg", weightInKg)
                .toString();
    }
}
