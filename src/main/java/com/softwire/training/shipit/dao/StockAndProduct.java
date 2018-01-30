package com.softwire.training.shipit.dao;

import com.softwire.training.shipit.model.Product;
import com.softwire.training.shipit.model.Stock;

public class StockAndProduct
{
    private Stock stock;
    private Product product;

    public StockAndProduct(Stock stock, Product product)
    {
        this.stock = stock;
        this.product = product;
    }

    public Stock getStock()
    {
        return stock;
    }

    public Product getProduct()
    {
        return product;
    }
}
