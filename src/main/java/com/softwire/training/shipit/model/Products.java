package com.softwire.training.shipit.model;

import java.util.List;

public class Products implements RenderableAsXML
{
    private final List<Product> products;

    public Products(List<Product> products)
    {
        this.products = products;
    }

    public List<Product> getProducts()
    {
        return products;
    }

    public String renderXML()
    {
        StringBuilder renderedProducts = new StringBuilder();
        for (Product product : products)
        {
            renderedProducts.append(product.renderXML());
        }

        return "<products>" + renderedProducts.toString() + "</products>";
    }
}
