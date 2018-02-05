package com.softwire.training.shipit.dao.impl;

import com.mysql.jdbc.Statement;
import com.softwire.training.shipit.dao.EmployeeDAO;
import com.softwire.training.shipit.dao.EmployeeRoleMarshaller;
import com.softwire.training.shipit.exception.InvalidStateException;
import com.softwire.training.shipit.model.Employee;
import com.softwire.training.shipit.model.EmployeeRole;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
            return new Employee(rs.getInt("employee_id"),
                    rs.getString("name"),
                    rs.getInt("w_id"),
                    EmployeeRoleMarshaller.unmarshall(rs.getString("role")),
                    rs.getString("ext"));
        }
    };

    private SimpleJdbcTemplate simpleJdbcTemplate;
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource)
    {
        simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Employee getEmployee(int id)
    {
        String sql = "SELECT employee_id, name, w_id, role, ext FROM em WHERE employee_id = ?";
        try
        {
            return simpleJdbcTemplate.queryForObject(sql, MAPPER, id);
        }
        catch (EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    public List<Employee> getEmployees(int warehouseId)
    {
        String sql = "SELECT employee_id, name, w_id, role, ext FROM em WHERE w_id = ?";
        return simpleJdbcTemplate.query(sql, MAPPER, warehouseId);
    }

    public List<Employee> getEmployees(int warehouseId, EmployeeRole employeeRole)
    {
        String sql = "SELECT employee_id, name, w_id, role, ext FROM em WHERE w_id = ? AND role = ?";
        return simpleJdbcTemplate.query(sql, MAPPER, warehouseId, EmployeeRoleMarshaller.marshall(employeeRole));
    }

    public List<Employee> getEmployeesByName(String name)
    {
        String sql = "SELECT employee_id, name, w_id, role, ext FROM em WHERE name = ?";
        return simpleJdbcTemplate.query(sql, MAPPER, name);
    }

    public void addEmployees(final List<Employee> employees) throws InvalidStateException
    {
        final String sql = "INSERT INTO em (name, w_id, role, ext) VALUES (?, ?, ?, ?)";

        for (final Employee employee : employees)
        {
            final KeyHolder keyHolder = new GeneratedKeyHolder();

            int result = jdbcTemplate.update(new PreparedStatementCreator()
            {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException
                {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1,  employee.getName());
                    ps.setInt(2, employee.getWarehouseId());
                    ps.setString(3, EmployeeRoleMarshaller.marshall(employee.getRole()));
                    ps.setString(4, employee.getExt());
                    return ps;
                }
            }, keyHolder);

            if (result != 1)
            {
                throw new InvalidStateException(String.format("Expected single row to be updated, but was: %s", result));
            }
        }
    }

    public void removeEmployee(int id) throws InvalidStateException
    {
        String deleteEmployeeSql = "DELETE FROM em WHERE employee_id = ?";
        int rowsDeleted = simpleJdbcTemplate.update(deleteEmployeeSql, id);
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
