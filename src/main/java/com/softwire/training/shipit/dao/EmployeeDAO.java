package com.softwire.training.shipit.dao;

import com.softwire.training.shipit.exception.InvalidStateException;
import com.softwire.training.shipit.model.Employee;
import com.softwire.training.shipit.model.EmployeeRole;

import java.util.List;

public interface EmployeeDAO
{
    Employee getEmployee(String name);

    List<Employee> getEmployees(int warehouseId);

    List<Employee> getEmployees(int warehouseId, EmployeeRole employeeRole);

    void addEmployees(List<Employee> employee);

    void removeEmployee(String name) throws InvalidStateException;

    int getEmployeeCount();

    int getWarehouseCount();
}
