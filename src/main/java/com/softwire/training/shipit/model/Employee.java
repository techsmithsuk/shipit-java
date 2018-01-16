package com.softwire.training.shipit.model;

import com.softwire.training.shipit.exception.ValidationException;
import com.softwire.training.shipit.exception.XMLParseException;
import com.softwire.training.shipit.utils.XMLParsingUtils;
import com.softwire.training.shipit.validators.EmployeeValidator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.w3c.dom.Element;

public class Employee implements RenderableAsXML
{
    private String name;
    private int warehouseId;
    private EmployeeRole role;
    private String ext;

    public Employee(String name, int warehouseId, EmployeeRole role, String ext)
    {
        this.name = name;
        this.warehouseId = warehouseId;
        this.role = role;
        this.ext = ext;
    }

    public static Employee parseXML(Element element) throws XMLParseException, ValidationException
    {
        String name = XMLParsingUtils.getSingleTextElementByTagName(element, "name");
        int warehouseId = XMLParsingUtils.getSingleIntElementByTagName(element, "warehouseId");
        String role = XMLParsingUtils.getSingleTextElementByTagName(element, "role");
        String ext = XMLParsingUtils.getSingleTextElementByTagName(element, "ext");

        Employee employee;
        try
        {
            employee = new Employee(name, warehouseId, EmployeeRole.valueOf(role), ext);
        }
        catch (IllegalArgumentException e)
        {
            throw new XMLParseException("Unable to construct employee object", e);
        }

        (new EmployeeValidator()).validate(employee);

        return employee;
    }

    public String getName()
    {
        return name;
    }

    public int getWarehouseId()
    {
        return warehouseId;
    }

    public EmployeeRole getRole()
    {
        return role;
    }

    public String getExt()
    {
        return ext;
    }

    public String renderXML()
    {
        return String.format(
                "<employee>" +
                        "<name>%s</name>" +
                        "<warehouseId>%d</warehouseId>" +
                        "<role>%s</role>" +
                        "<ext>%s</ext>" +
                        "</employee>",
                name, warehouseId, role, ext);
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

        Employee employee = (Employee) o;

        return new EqualsBuilder()
                .append(warehouseId, employee.warehouseId)
                .append(name, employee.name)
                .append(role, employee.role)
                .append(ext, employee.ext)
                .isEquals();
    }

    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(warehouseId)
                .append(role)
                .append(ext)
                .toHashCode();
    }

    public String toString()
    {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("warehouseId", warehouseId)
                .append("role", role)
                .append("ext", ext)
                .toString();
    }
}
