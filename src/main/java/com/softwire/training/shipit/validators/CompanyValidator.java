package com.softwire.training.shipit.validators;

import com.softwire.training.shipit.model.Company;

public class CompanyValidator extends BaseValidator<Company>
{
    public CompanyValidator()
    {
        super(Company.class);
    }

    protected void doValidation(Company target)
    {
        assertNotBlank("name", target.getName());
        assertMaxLength("name", target.getName(), 255);

        validateGcp(target.getGcp());

        assertNotNull("addr2", target.getAddr2());
        assertMaxLength("addr2", target.getAddr2(), 38);

        assertNotNull("addr3", target.getAddr3());
        assertMaxLength("addr3", target.getAddr3(), 38);

        assertNotNull("addr4", target.getAddr4());
        assertMaxLength("addr4", target.getAddr4(), 38);

        assertNotNull("postalCode", target.getPostalCode());
        assertMaxLength("postalCode", target.getPostalCode(), 38);

        assertNotNull("city", target.getCity());
        assertMaxLength("city", target.getCity(), 38);

        assertNotNull("tel", target.getTel());
        assertMaxLength("tel", target.getTel(), 255);

        assertNotNull("mail", target.getMail());
        assertMaxLength("mail", target.getMail(), 255);
    }
}
