package com.softwire.training.shipit.model;

import com.softwire.training.shipit.exception.ValidationException;
import com.softwire.training.shipit.exception.XMLParseException;
import com.softwire.training.shipit.utils.XMLParsingUtils;
import com.softwire.training.shipit.validators.ProductValidator;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.w3c.dom.Element;

public class Product implements RenderableAsXML
{
    private Integer id;
    private String gtin;
    private String gcp;
    private String name;
    private float weight;
    private int lowerThreshold;
    private boolean discontinued;
    private int minimumOrderQuantity;

    public Product(Integer id, String gtin, String gcp, String name, float weight, int lowerThreshold, boolean discontinued, int minimumOrderQuantity)
    {
        this.id = id;
        this.gtin = gtin;
        this.gcp = gcp;
        this.name = name;
        this.weight = weight;
        this.lowerThreshold = lowerThreshold;
        this.discontinued = discontinued;
        this.minimumOrderQuantity = minimumOrderQuantity;
    }

    public static Product parseXML(Element element) throws XMLParseException, ValidationException
    {
        String gtin = XMLParsingUtils.getSingleTextElementByTagName(element, "gtin");
        String gcp = XMLParsingUtils.getSingleTextElementByTagName(element, "gcp");
        String name = XMLParsingUtils.getSingleTextElementByTagName(element, "name");
        String weight = XMLParsingUtils.getSingleTextElementByTagName(element, "weight");
        String lowerThreshold = XMLParsingUtils.getSingleTextElementByTagName(element, "lowerThreshold");
        String discontinued = XMLParsingUtils.getSingleTextElementByTagName(element, "discontinued");
        String minimumOrderQuantity = XMLParsingUtils.getSingleTextElementByTagName(element, "minimumOrderQuantity");

        Product product;
        try
        {
            product = new Product(
                    null,
                    gtin,
                    gcp,
                    name,
                    Float.valueOf(weight),
                    Integer.valueOf(lowerThreshold),
                    Integer.valueOf(discontinued) != 0,
                    Integer.valueOf(minimumOrderQuantity)
            );
        }
        catch (NumberFormatException e)
        {
            throw new XMLParseException("Unable to construct Product object");
        }

        (new ProductValidator()).validate(product);

        return product;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getGtin()
    {
        return gtin;
    }

    public String getGcp()
    {
        return gcp;
    }

    public String getName()
    {
        return name;
    }

    public float getWeight()
    {
        return weight;
    }

    public int getLowerThreshold()
    {
        return lowerThreshold;
    }

    public boolean isDiscontinued()
    {
        return discontinued;
    }

    public int getMinimumOrderQuantity()
    {
        return minimumOrderQuantity;
    }

    public String renderXML()
    {
        return "<product>" +
                "<id>" + id + "</id>" +
                "<gtin>" + gtin + "</gtin>" +
                "<gcp>" + gcp + "</gcp>" +
                "<name>" + StringEscapeUtils.escapeXml10(name) + "</name>" +
                "<weight>" + weight + "</weight>" +
                "<lowerThreshold>" + lowerThreshold + "</lowerThreshold>" +
                "<discontinued>" + (discontinued ? 1 : 0) + "</discontinued>" +
                "<minimumOrderQuantity>" + minimumOrderQuantity + "</minimumOrderQuantity>" +
                "</product>";
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

        Product product = (Product) o;

        return new EqualsBuilder()
                .append(weight, product.weight)
                .append(lowerThreshold, product.lowerThreshold)
                .append(discontinued, product.discontinued)
                .append(minimumOrderQuantity, product.minimumOrderQuantity)
                .append(id, product.id)
                .append(gtin, product.gtin)
                .append(gcp, product.gcp)
                .append(name, product.name)
                .isEquals();
    }


    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(gtin)
                .append(gcp)
                .append(name)
                .append(weight)
                .append(lowerThreshold)
                .append(discontinued)
                .append(minimumOrderQuantity)
                .toHashCode();
    }

    public String toString()
    {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("gtin", gtin)
                .append("gcp", gcp)
                .append("name", name)
                .append("weight", weight)
                .append("lowerThreshold", lowerThreshold)
                .append("discontinued", discontinued)
                .append("minimumOrderQuantity", minimumOrderQuantity)
                .toString();
    }

}
