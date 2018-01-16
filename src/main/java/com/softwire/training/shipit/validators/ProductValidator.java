package com.softwire.training.shipit.validators;

import com.softwire.training.shipit.model.Product;

public class ProductValidator extends BaseValidator<Product>
{
    public ProductValidator()
    {
        super(Product.class);
    }

    protected void doValidation(Product target)
    {
        assertNotBlank("name", target.getName());
        assertMaxLength("name", target.getName(), 255);

        validateGtin(target.getGtin());

        validateGcp(target.getGcp());

        assertNonNegative("m_g", target.getWeight());

        assertNonNegative("lowerThreshold", target.getLowerThreshold());

        assertNonNegative("minimumOrderQuantity", target.getMinimumOrderQuantity());
    }
}
