package com.softwire.training.shipit.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Status implements RenderableAsXML
{
    private final int warehouseCount;
    private final int employeeCount;
    private final int itemsTracked;
    private final int stockHeld;
    private final int productCount;
    private final int companyCount;

    public Status(int warehouseCount, int employeeCount, int itemsTracked, int stockHeld, int productCount, int companyCount)
    {
        this.warehouseCount = warehouseCount;
        this.employeeCount = employeeCount;
        this.itemsTracked = itemsTracked;
        this.stockHeld = stockHeld;
        this.productCount = productCount;
        this.companyCount = companyCount;
    }

    public String renderXML()
    {
        return "<status>" +
                "<warehouses>" + warehouseCount + "</warehouses>" +
                "<employees>" + employeeCount + "</employees>" +
                "<stockTracked>" + itemsTracked + "</stockTracked>" +
                "<stockHeld>" + stockHeld + "</stockHeld>" +
                "<products>" + productCount + "</products>" +
                "<companies>" + companyCount + "</companies>" +
                "</status>";
    }

    public String toString()
    {
        return new ToStringBuilder(this)
                .append("warehouseCount", warehouseCount)
                .append("employeeCount", employeeCount)
                .append("itemsTracked", itemsTracked)
                .append("stockHeld", stockHeld)
                .append("productCount", productCount)
                .append("companyCount", companyCount)
                .toString();
    }
}
