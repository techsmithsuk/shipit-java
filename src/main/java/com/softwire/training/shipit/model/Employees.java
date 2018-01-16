package com.softwire.training.shipit.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class Employees implements RenderableAsXML
{
    private final List<Employee> employees;

    public Employees(List<Employee> employees)
    {
        this.employees = employees;
    }

    public List<Employee> getEmployees()
    {
        return employees;
    }

    public String renderXML()
    {
        StringBuilder builder = new StringBuilder();
        for (Employee employee : employees)
        {
            builder.append(employee.renderXML());
        }

        return "<employees>" + builder.toString() + "</employees>";
    }


    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Employees employees1 = (Employees) o;

        return new EqualsBuilder()
                .append(employees, employees1.employees)
                .isEquals();
    }


    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(employees)
                .toHashCode();
    }

    public String toString()
    {
        return new ToStringBuilder(this)
                .append("employees", employees)
                .toString();
    }
}
