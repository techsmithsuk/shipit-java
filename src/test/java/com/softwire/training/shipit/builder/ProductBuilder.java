package com.softwire.training.shipit.builder;

import com.softwire.training.shipit.model.Product;

public class ProductBuilder
{
    private Integer id = 1;
    private String gtin = "0000346374230";
    private String gcp = "0000346";
    private String name = "2 Count 1 T30 Torx Bit Tips TX";
    private float weight = 300.0f;
    private int lowerThreshold = 322;
    private boolean discontinued = false;
    private int minimumOrderQuantity = 108;

    public ProductBuilder setId(Integer id)
    {
        this.id = id;
        return this;
    }

    public ProductBuilder setGtin(String gtin)
    {
        this.gtin = gtin;
        return this;
    }

    public ProductBuilder setGcp(String gcp)
    {
        this.gcp = gcp;
        return this;
    }

    public ProductBuilder setName(String name)
    {
        this.name = name;
        return this;
    }

    public ProductBuilder setWeight(float weight)
    {
        this.weight = weight;
        return this;
    }

    public ProductBuilder setLowerThreshold(int lowerThreshold)
    {
        this.lowerThreshold = lowerThreshold;
        return this;
    }

    public ProductBuilder setDiscontinued(boolean discontinued)
    {
        this.discontinued = discontinued;
        return this;
    }

    public ProductBuilder setMinimumOrderQuantity(int minimumOrderQuantity)
    {
        this.minimumOrderQuantity = minimumOrderQuantity;
        return this;
    }

    public Product createProduct()
    {
        return new Product(id, gtin, gcp, name, weight, lowerThreshold, discontinued, minimumOrderQuantity);
    }
}