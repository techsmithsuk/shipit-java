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
    private Integer id;
    private String name;
    private int warehouseId;
    private EmployeeRole role;
    private String ext;

    public Employee(Integer id, String name, int warehouseId, EmployeeRole role, String ext)
    {
        this.id = id;
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
            employee = new Employee(null, name, warehouseId, EmployeeRole.valueOf(role), ext);
        }
        catch (IllegalArgumentException e)
        {
            throw new XMLParseException("Unable to construct employee object", e);
        }

        (new EmployeeValidator()).validate(employee);

        return employee;
    }

    public Integer getId()
    {
        return id;
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
                        "<id>%s</id>" +
                        "<name>%s</name>" +
                        "<warehouseId>%d</warehouseId>" +
                        "<role>%s</role>" +
                        "<ext>%s</ext>" +
                        "</employee>",
                id, name, warehouseId, role, ext);
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
                .append(id, employee.id)
                .append(warehouseId, employee.warehouseId)
                .append(name, employee.name)
                .append(role, employee.role)
                .append(ext, employee.ext)
                .isEquals();
    }

    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(warehouseId)
                .append(role)
                .append(ext)
                .toHashCode();
    }

    public String toString()
    {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("warehouseId", warehouseId)
                .append("role", role)
                .append("ext", ext)
                .toString();
    }
}
