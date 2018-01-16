package com.softwire.training.shipit.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Stock implements RenderableAsXML
{
    private int warehouseId;
    private int productId;
    private int held;

    public Stock(int warehouseId, int productId, int held)
    {
        this.warehouseId = warehouseId;
        this.productId = productId;
        this.held = held;
    }

    public int getWarehouseId()
    {
        return warehouseId;
    }

    public int getProductId()
    {
        return productId;
    }

    public int getHeld()
    {
        return held;
    }

    public String renderXML()
    {
        return "<stock>" +
                "<warehouseId>" + warehouseId + "</warehouseId>" +
                "<productId>" + productId + "</productId>" +
                "<held>" + held + "</held>" +
                "</stock>";
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

        Stock stock = (Stock) o;

        return new EqualsBuilder()
                .append(warehouseId, stock.warehouseId)
                .append(productId, stock.productId)
                .append(held, stock.held)
                .isEquals();
    }

    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(warehouseId)
                .append(productId)
                .append(held)
                .toHashCode();
    }

    public String toString()
    {
        return new ToStringBuilder(this)
                .append("warehouseId", warehouseId)
                .append("productId", productId)
                .append("held", held)
                .toString();
    }
}
