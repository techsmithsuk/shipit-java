package com.softwire.training.shipit.validators;

import com.softwire.training.shipit.model.InboundManifest;

public class InboundManifestValidator extends BaseValidator<InboundManifest>
{
    public InboundManifestValidator()
    {
        super(InboundManifest.class);
    }

    protected void doValidation(InboundManifest target)
    {
        validateGcp(target.getGcp());
        validateWarehouseId(target.getWarehouseId());
        assertNotNull("orderLines", target.getOrderLines());
        validateOrderLines(target.getOrderLines());
    }
}
