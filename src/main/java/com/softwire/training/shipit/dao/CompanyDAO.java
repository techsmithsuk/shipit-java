package com.softwire.training.shipit.dao;

import com.softwire.training.shipit.model.Company;

import java.util.List;
import java.util.Map;

public interface CompanyDAO
{
    Company getCompany(String gcp);

    void addCompanies(List<Company> companies);

    int getCompanyCount();

    Map<String, Company> getCompanies(List<String> gcps);
}
