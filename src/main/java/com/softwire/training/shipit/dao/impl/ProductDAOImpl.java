package com.softwire.training.shipit.dao.impl;

import com.mysql.jdbc.Statement;
import com.softwire.training.shipit.dao.ProductDAO;
import com.softwire.training.shipit.exception.InvalidStateException;
import com.softwire.training.shipit.model.Product;
import org.apache.commons.lang3.StringUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDAOImpl implements ProductDAO
{
    private static final ParameterizedRowMapper<Product> MAPPER = new ParameterizedRowMapper<Product>()
    {
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException
        {
            return new Product(
                    rs.getInt("p_id"),
                    rs.getString("gtin_cd"),
                    rs.getString("gcp_cd"),
                    rs.getString("gtin_nm"),
                    rs.getFloat("m_g"),
                    rs.getInt("l_th"),
                    rs.getInt("ds") == 1,
                    rs.getInt("min_qt"));
        }
    };

    private SimpleJdbcTemplate simpleJdbcTemplate;
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource)
    {
        simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Product getProduct(int id)
    {
        String sql = "SELECT p_id, gtin_cd, gcp_cd, gtin_nm, m_g, l_th, ds, min_qt FROM gtin WHERE p_id = ?";
        return simpleJdbcTemplate.queryForObject(sql, MAPPER, id);
    }

    public void addProducts(final List<Product> products) throws InvalidStateException
    {
        final String sql = "INSERT INTO gtin (gtin_cd, gcp_cd, gtin_nm, m_g, l_th, ds, min_qt) VALUES (?, ?, ?, ?, ?, ?, ?)";

        for (final Product product : products)
        {
            final KeyHolder keyHolder = new GeneratedKeyHolder();

            int result = jdbcTemplate.update(new PreparedStatementCreator()
            {

                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException
                {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, product.getGtin());
                    ps.setString(2, product.getGcp());
                    ps.setString(3, product.getName());
                    ps.setFloat(4, product.getWeight());
                    ps.setInt(5, product.getLowerThreshold());
                    ps.setInt(6, product.isDiscontinued() ? 1 : 0);
                    ps.setInt(7, product.getMinimumOrderQuantity());
                    return ps;
                }
            }, keyHolder);

            if (result != 1)
            {
                throw new InvalidStateException(String.format("Expected single row to be updated, but was: %s (p_id: %s)",
                        result, product.getId()));
            }

            product.setId(keyHolder.getKey().intValue());
        }
    }

    public void discontinueProduct(String gtin) throws InvalidStateException
    {
        String sql = "UPDATE gtin SET ds = 1 WHERE gtin_cd = ?";
        int rowsUpdated = simpleJdbcTemplate.update(sql, gtin);

        if (rowsUpdated == 0)
        {
            throw new EmptyResultDataAccessException(1);
        }
        else if (rowsUpdated > 1)
        {
            throw new InvalidStateException(
                    "Unexpectedly updated " + rowsUpdated + " rows, but expected a single update");
        }
    }

    public int getProductCount()
    {
        String sql = "SELECT COUNT(*) FROM gtin";
        return simpleJdbcTemplate.queryForInt(sql);
    }

    public Product getProductByGtin(String gtin)
    {
        String sql = String.format("SELECT p_id, gtin_cd, gcp_cd, gtin_nm, m_g, l_th, ds, min_qt FROM gtin WHERE gtin_cd = %s", gtin);
        try
        {
            return simpleJdbcTemplate.queryForObject(sql, MAPPER);
        }
        catch (EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    public Map<String, Product> getProductsByGtin(List<String> gtins)
    {
        String sql = String.format("SELECT p_id, gtin_cd, gcp_cd, gtin_nm, m_g, l_th, ds, min_qt FROM gtin WHERE gtin_cd IN (%s)",
                StringUtils.join(gtins, ","));
        List<Product> products = simpleJdbcTemplate.query(sql, MAPPER);

        Map<String, Product> productsByGtin = new HashMap<String, Product>();
        for (Product product : products)
        {
            productsByGtin.put(product.getGtin(), product);
        }

        return productsByGtin;
    }
}
