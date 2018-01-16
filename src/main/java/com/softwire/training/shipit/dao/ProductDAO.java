package com.softwire.training.shipit.dao;

import com.softwire.training.shipit.exception.InvalidStateException;
import com.softwire.training.shipit.model.Product;

import java.util.List;
import java.util.Map;

public interface ProductDAO
{
    Product getProduct(int id);

    void addProducts(List<Product> product) throws InvalidStateException;

    void discontinueProduct(String gtin) throws InvalidStateException;

    int getProductCount();

    Product getProductByGtin(String gtin);

    Map<String, Product> getProductsByGtin(List<String> gtins);
}
