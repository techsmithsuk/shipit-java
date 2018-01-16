package com.softwire.training.shipit.dao.impl;

import com.softwire.training.shipit.dao.EmployeeDAO;
import com.softwire.training.shipit.dao.EmployeeRoleMarshaller;
import com.softwire.training.shipit.exception.InvalidStateException;
import com.softwire.training.shipit.model.Employee;
import com.softwire.training.shipit.model.EmployeeRole;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAOImpl implements EmployeeDAO
{
    private static final ParameterizedRowMapper<Employee> MAPPER = new ParameterizedRowMapper<Employee>()
    {
        public Employee mapRow(ResultSet rs, int rowNum) throws SQLException
        {
            return new Employee(rs.getString("name"),
                    rs.getInt("w_id"),
                    EmployeeRoleMarshaller.unmarshall(rs.getString("role")),
                    rs.getString("ext"));
        }
    };

    private SimpleJdbcTemplate simpleJdbcTemplate;

    public void setDataSource(DataSource dataSource)
    {
        simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
    }

    public Employee getEmployee(String name)
    {
        String sql = "SELECT name, w_id, role, ext FROM em WHERE name = ?";
        try
        {
            return simpleJdbcTemplate.queryForObject(sql, MAPPER, name);
        }
        catch (EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    public List<Employee> getEmployees(int warehouseId)
    {
        String sql = "SELECT name, w_id, role, ext FROM em WHERE w_id = ?";
        return simpleJdbcTemplate.query(sql, MAPPER, warehouseId);
    }

    public List<Employee> getEmployees(int warehouseId, EmployeeRole employeeRole)
    {
        String sql = "SELECT name, w_id, role, ext FROM em WHERE w_id = ? AND role = ?";
        return simpleJdbcTemplate.query(sql, MAPPER, warehouseId, EmployeeRoleMarshaller.marshall(employeeRole));
    }

    public void addEmployees(final List<Employee> employees)
    {
        String sql = "INSERT INTO em (name, w_id, role, ext) VALUES (?, ?, ?, ?)";
        List<Object[]> args = new ArrayList<Object[]>(employees.size());
        for (Employee employee : employees)
        {
            args.add(new Object[]{
                    employee.getName(),
                    employee.getWarehouseId(),
                    EmployeeRoleMarshaller.marshall(employee.getRole()),
                    employee.getExt()
            });
        }
        simpleJdbcTemplate.batchUpdate(sql, args);
    }

    public void removeEmployee(String name) throws InvalidStateException
    {
        String deleteEmployeeSql = "DELETE FROM em WHERE name = ?";
        int rowsDeleted = simpleJdbcTemplate.update(deleteEmployeeSql, name);
        if (rowsDeleted == 0)
        {
            throw new EmptyResultDataAccessException(1);
        }
        else if (rowsDeleted > 1)
        {
            throw new InvalidStateException(
                    "Unexpectedly deleted " + rowsDeleted + " rows, but expected a single update");
        }
    }

    public int getEmployeeCount()
    {
        String sql = "SELECT COUNT(*) FROM em";
        return simpleJdbcTemplate.queryForInt(sql);
    }

    public int getWarehouseCount()
    {
        String sql = "SELECT COUNT(DISTINCT w_id) FROM em";
        return simpleJdbcTemplate.queryForInt(sql);
    }
}
