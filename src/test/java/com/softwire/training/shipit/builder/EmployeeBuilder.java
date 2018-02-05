package com.softwire.training.shipit.builder;

import com.softwire.training.shipit.model.Employee;
import com.softwire.training.shipit.model.EmployeeRole;

public class EmployeeBuilder
{
    private Integer id = 1;
    private String name = "Gissell Sadeem";
    private int warehouseId = 1;
    private EmployeeRole role = EmployeeRole.OPERATIONS_MANAGER;
    private String ext = "73996";

    public EmployeeBuilder setName(String name)
    {
        this.name = name;
        return this;
    }

    public EmployeeBuilder setWarehouseId(int warehouseId)
    {
        this.warehouseId = warehouseId;
        return this;
    }

    public EmployeeBuilder setRole(EmployeeRole role)
    {
        this.role = role;
        return this;
    }

    public EmployeeBuilder setExt(String ext)
    {
        this.ext = ext;
        return this;
    }

    public EmployeeBuilder setId(Integer id)
    {
        this.id = id;
        return this;
    }

    public Employee createEmployee()
    {
        return new Employee(id, name, warehouseId, role, ext);
    }
}