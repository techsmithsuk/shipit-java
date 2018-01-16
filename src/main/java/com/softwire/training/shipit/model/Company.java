package com.softwire.training.shipit.model;

import com.softwire.training.shipit.exception.ValidationException;
import com.softwire.training.shipit.exception.XMLParseException;
import com.softwire.training.shipit.utils.XMLParsingUtils;
import com.softwire.training.shipit.validators.CompanyValidator;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.w3c.dom.Element;

public class Company implements RenderableAsXML
{
    private String gcp;
    private String name;
    private String addr2;
    private String addr3;
    private String addr4;
    private String postalCode;
    private String city;
    private String tel;
    private String mail;

    public Company(String gcp, String name, String addr2, String addr3, String addr4, String postalCode, String city, String tel, String mail)
    {
        this.gcp = gcp;
        this.name = name;
        this.addr2 = addr2;
        this.addr3 = addr3;
        this.addr4 = addr4;
        this.postalCode = postalCode;
        this.city = city;
        this.tel = tel;
        this.mail = mail;
    }

    public static Company parseXML(Element root) throws XMLParseException, ValidationException
    {
        String gcp = XMLParsingUtils.getSingleTextElementByTagName(root, "gcp");
        String name = XMLParsingUtils.getSingleTextElementByTagName(root, "name");
        String addr2 = XMLParsingUtils.getSingleTextElementByTagName(root, "addr2");
        String addr3 = XMLParsingUtils.getSingleTextElementByTagName(root, "addr3");
        String addr4 = XMLParsingUtils.getSingleTextElementByTagName(root, "addr4");
        String postalCode = XMLParsingUtils.getSingleTextElementByTagName(root, "postalCode");
        String city = XMLParsingUtils.getSingleTextElementByTagName(root, "city");
        String tel = XMLParsingUtils.getSingleTextElementByTagName(root, "tel");
        String mail = XMLParsingUtils.getSingleTextElementByTagName(root, "mail");

        Company company = new Company(gcp, name, addr2, addr3, addr4, postalCode, city, tel, mail);

        (new CompanyValidator()).validate(company);

        return company;
    }

    public String getGcp()
    {
        return gcp;
    }

    public String getName()
    {
        return name;
    }

    public String getAddr2()
    {
        return addr2;
    }

    public String getAddr3()
    {
        return addr3;
    }

    public String getAddr4()
    {
        return addr4;
    }

    public String getPostalCode()
    {
        return postalCode;
    }

    public String getCity()
    {
        return city;
    }

    public String getTel()
    {
        return tel;
    }

    public String getMail()
    {
        return mail;
    }

    public String renderXML()
    {
        return "<company>" +
                "<gcp>" + gcp + "</gcp>" +
                "<name>" + StringEscapeUtils.escapeXml10(name) + "</name>" +
                "<addr2>" + StringEscapeUtils.escapeXml10(addr2) + "</addr2>" +
                "<addr3>" + StringEscapeUtils.escapeXml10(addr3) + "</addr3>" +
                "<addr4>" + StringEscapeUtils.escapeXml10(addr4) + "</addr4>" +
                "<postalCode>" + StringEscapeUtils.escapeXml10(postalCode) + "</postalCode>" +
                "<city>" + StringEscapeUtils.escapeXml10(city) + "</city>" +
                "<tel>" + StringEscapeUtils.escapeXml10(tel) + "</tel>" +
                "<mail>" + StringEscapeUtils.escapeXml10(mail) + "</mail>" +
                "</company>";
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

        Company company = (Company) o;

        return new EqualsBuilder()
                .append(gcp, company.gcp)
                .append(name, company.name)
                .append(addr2, company.addr2)
                .append(addr3, company.addr3)
                .append(addr4, company.addr4)
                .append(postalCode, company.postalCode)
                .append(city, company.city)
                .append(tel, company.tel)
                .append(mail, company.mail)
                .isEquals();
    }


    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(gcp)
                .append(name)
                .append(addr2)
                .append(addr3)
                .append(addr4)
                .append(postalCode)
                .append(city)
                .append(tel)
                .append(mail)
                .toHashCode();
    }


    public String toString()
    {
        return new ToStringBuilder(this)
                .append("gcp", gcp)
                .append("name", name)
                .append("addr2", addr2)
                .append("addr3", addr3)
                .append("addr4", addr4)
                .append("postalCode", postalCode)
                .append("city", city)
                .append("tel", tel)
                .append("mail", mail)
                .toString();
    }
}
