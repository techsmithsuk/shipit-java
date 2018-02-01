package com.softwire.training.shipit.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class OutboundOrderManifest implements RenderableAsXML
{
    private List<Truck> trucks;

    public OutboundOrderManifest(List<Truck> trucks)
    {
        this.trucks = trucks;
    }

    public String renderXML()
    {
        StringBuilder renderedTrucks = new StringBuilder();
        for (Truck truck : trucks)
        {
            renderedTrucks.append(truck.renderXML());
        }
        return "<outboundManifest>" +
                "<trucks>" + renderedTrucks.toString() + "</trucks>" +
                "</outboundManifest>";
    }

    public List<Truck> getTrucks()
    {
        return trucks;
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

        OutboundOrderManifest that = (OutboundOrderManifest) o;

        return new EqualsBuilder()
                .append(trucks, that.trucks)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(trucks)
                .toHashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("trucks", trucks)
                .toString();
    }
}
