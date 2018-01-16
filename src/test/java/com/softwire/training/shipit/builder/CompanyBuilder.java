package com.softwire.training.shipit.builder;

import com.softwire.training.shipit.model.Company;

public class CompanyBuilder
{
    private String gcp = "0000346";
    private String name = "Robert Bosch Tool Corporation";
    private String addr2 = "1800 West Central";
    private String addr3 = "";
    private String addr4 = "IL";
    private String postalCode = "60056";
    private String city = "Mount Prospect";
    private String tel = "(224) 232-2407";
    private String mail = "info@gs1us.org";

    public CompanyBuilder setGcp(String gcp)
    {
        this.gcp = gcp;
        return this;
    }

    public CompanyBuilder setName(String name)
    {
        this.name = name;
        return this;
    }

    public CompanyBuilder setAddr2(String addr2)
    {
        this.addr2 = addr2;
        return this;
    }

    public CompanyBuilder setAddr3(String addr3)
    {
        this.addr3 = addr3;
        return this;
    }

    public CompanyBuilder setAddr4(String addr4)
    {
        this.addr4 = addr4;
        return this;
    }

    public CompanyBuilder setPostalCode(String postalCode)
    {
        this.postalCode = postalCode;
        return this;
    }

    public CompanyBuilder setCity(String city)
    {
        this.city = city;
        return this;
    }

    public CompanyBuilder setTel(String tel)
    {
        this.tel = tel;
        return this;
    }

    public CompanyBuilder setMail(String mail)
    {
        this.mail = mail;
        return this;
    }

    public Company createCompany()
    {
        return new Company(gcp, name, addr2, addr3, addr4, postalCode, city, tel, mail);
    }
}