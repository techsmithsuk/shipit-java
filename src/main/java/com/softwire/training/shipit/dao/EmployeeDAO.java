package com.softwire.training.shipit.dao;

import com.softwire.training.shipit.exception.InvalidStateException;
import com.softwire.training.shipit.model.Employee;
import com.softwire.training.shipit.model.EmployeeRole;

import java.util.List;

public interface EmployeeDAO
{
    Employee getEmployee(int id);

    List<Employee> getEmployeesByName(String name);

    List<Employee> getEmployees(int warehouseId);

    List<Employee> getEmployees(int warehouseId, EmployeeRole employeeRole);

    void addEmployees(List<Employee> employee) throws InvalidStateException;

    void removeEmployee(int employeeId) throws InvalidStateException;

    int getEmployeeCount();

    int getWarehouseCount();
}
