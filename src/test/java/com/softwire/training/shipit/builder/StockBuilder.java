package com.softwire.training.shipit.builder;

import com.softwire.training.shipit.model.Stock;

public class StockBuilder
{
    private int warehouseId = 1;
    private int productId = 1;
    private Integer held = 0;

    public StockBuilder setWarehouseId(int warehouseId)
    {
        this.warehouseId = warehouseId;
        return this;
    }

    public StockBuilder setProductId(int productId)
    {
        this.productId = productId;
        return this;
    }

    public StockBuilder setHeld(Integer held)
    {
        this.held = held;
        return this;
    }

    public Stock createStock()
    {
        return new Stock(warehouseId, productId, held);
    }
}