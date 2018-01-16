package com.softwire.training.shipit.validators;

import com.softwire.training.shipit.model.OutboundOrder;

public class OutboundOrderValidator extends BaseValidator<OutboundOrder>
{
    public OutboundOrderValidator()
    {
        super(OutboundOrder.class);
    }

    protected void doValidation(OutboundOrder target)
    {
        assertNonNegative("warehouseId", target.getWarehouseId());
        assertNotNull("outboundOrderLines", target.getOrderLines());

        validateOrderLines(target.getOrderLines());
    }
}
