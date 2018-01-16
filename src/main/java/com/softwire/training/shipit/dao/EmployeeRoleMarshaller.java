package com.softwire.training.shipit.dao;

import com.softwire.training.shipit.model.EmployeeRole;

public class EmployeeRoleMarshaller
{
    public static EmployeeRole unmarshall(String role)
    {
        return EmployeeRole.valueOf(role.toUpperCase().replace(' ', '_'));
    }

    public static String marshall(EmployeeRole employeeRole)
    {
        return employeeRole.name().toLowerCase().replace('_', ' ');
    }
}
