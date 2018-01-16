package com.softwire.training.shipit.dao;

import com.softwire.training.shipit.model.Company;

import java.util.List;

public interface CompanyDAO
{
    Company getCompany(String gcp);

    void addCompanies(List<Company> companies);

    int getCompanyCount();
}
