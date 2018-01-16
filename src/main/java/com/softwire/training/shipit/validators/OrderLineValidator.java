package com.softwire.training.shipit.validators;

import com.softwire.training.shipit.model.OrderLine;

public class OrderLineValidator extends BaseValidator<OrderLine>
{
    public OrderLineValidator()
    {
        super(OrderLine.class);
    }

    protected void doValidation(OrderLine target)
    {
        assertNonNegative("quantity", target.getQuantity());
        validateGtin(target.getGtin());
    }
}
