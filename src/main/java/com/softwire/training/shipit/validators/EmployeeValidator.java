package com.softwire.training.shipit.validators;

import com.softwire.training.shipit.model.Employee;

public class EmployeeValidator extends BaseValidator<Employee>
{
    public EmployeeValidator()
    {
        super(Employee.class);
    }

    protected void doValidation(Employee target)
    {
        validateWarehouseId(target.getWarehouseId());

        assertNotBlank("name", target.getName());
        assertMaxLength("name", target.getName(), 40);

        assertNotNull("role", target.getRole());

        assertNotNull("ext", target.getExt());
        assertExactLength("ext", target.getExt(), 5);
        assertNumeric("ext", target.getExt());
    }

}
