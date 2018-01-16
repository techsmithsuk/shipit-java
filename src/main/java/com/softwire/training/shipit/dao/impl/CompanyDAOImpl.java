package com.softwire.training.shipit.dao.impl;

import com.softwire.training.shipit.dao.CompanyDAO;
import com.softwire.training.shipit.model.Company;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAOImpl implements CompanyDAO
{
    private static final ParameterizedRowMapper<Company> MAPPER = new ParameterizedRowMapper<Company>()
    {
        public Company mapRow(ResultSet rs, int rowNum) throws SQLException
        {
            return new Company(
                    rs.getString("gcp_cd"),
                    rs.getString("gln_nm"),
                    rs.getString("gln_addr_02"),
                    rs.getString("gln_addr_03"),
                    rs.getString("gln_addr_04"),
                    rs.getString("gln_addr_postalcode"),
                    rs.getString("gln_addr_city"),
                    rs.getString("contact_tel"),
                    rs.getString("contact_mail"));
        }
    };

    private SimpleJdbcTemplate simpleJdbcTemplate;

    public void setDataSource(DataSource dataSource)
    {
        simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
    }

    public Company getCompany(String gcp)
    {
        String sql = "SELECT gcp_cd, gln_nm, gln_addr_02, gln_addr_03, gln_addr_04, " +
                "gln_addr_postalcode, gln_addr_city, contact_tel, contact_mail FROM gcp WHERE gcp_cd = ?";
        try
        {
            return simpleJdbcTemplate.queryForObject(sql, MAPPER, gcp);
        }
        catch (EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    public void addCompanies(List<Company> companies)
    {
        String sql = "INSERT INTO gcp (gcp_cd, gln_nm, gln_addr_02, gln_addr_03, gln_addr_04, " +
                "gln_addr_postalcode, gln_addr_city, contact_tel, contact_mail ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object[]> args = new ArrayList<Object[]>(companies.size());
        for (Company company : companies)
        {
            args.add(new Object[]{
                    company.getGcp(),
                    company.getName(),
                    company.getAddr2(),
                    company.getAddr3(),
                    company.getAddr4(),
                    company.getPostalCode(),
                    company.getCity(),
                    company.getTel(),
                    company.getMail()
            });
        }
        simpleJdbcTemplate.batchUpdate(sql, args);
    }

    public int getCompanyCount()
    {
        String sql = "SELECT COUNT(*) FROM gcp";
        return simpleJdbcTemplate.queryForInt(sql);
    }
}
