package com.softwire.training.shipit;

import com.softwire.training.shipit.dao.CompanyDAO;
import com.softwire.training.shipit.dao.EmployeeDAO;
import com.softwire.training.shipit.dao.ProductDAO;
import com.softwire.training.shipit.dao.StockDAO;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.springframework.web.servlet.support.WebContentGenerator.METHOD_GET;
import static org.springframework.web.servlet.support.WebContentGenerator.METHOD_POST;

public abstract class AbstractBaseTest extends AbstractDependencyInjectionSpringContextTests
{
    private static Logger sLog = Logger.getLogger(AbstractBaseTest.class);

    protected JdbcTemplate jdbcTemplate;
    protected PlatformTransactionManager transactionManager;

    protected EmployeeDAO employeeDAO;
    protected ProductDAO productDAO;
    protected CompanyDAO companyDAO;
    protected StockDAO stockDAO;

    private DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    public void setDataSource(DataSource dataSource)
    {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }

    public void setEmployeeDAO(EmployeeDAO employeeDAO)
    {
        this.employeeDAO = employeeDAO;
    }

    public void setProductDAO(ProductDAO productDAO)
    {
        this.productDAO = productDAO;
    }

    public void setCompanyDAO(CompanyDAO companyDAO)
    {
        this.companyDAO = companyDAO;
    }

    public void setStockDAO(StockDAO stockDAO)
    {
        this.stockDAO = stockDAO;
    }

    public void onSetUp() throws Exception
    {
        TransactionDefinition txDef = new DefaultTransactionDefinition(TransactionDefinition.ISOLATION_SERIALIZABLE);
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);
        try
        {
            // Start from a clean slate
            jdbcTemplate.execute("LOCK TABLES em WRITE, gcp WRITE, gtin WRITE, stock WRITE");
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
            jdbcTemplate.execute("TRUNCATE TABLE em");
            jdbcTemplate.execute("TRUNCATE TABLE gcp");
            jdbcTemplate.execute("TRUNCATE TABLE gtin");
            jdbcTemplate.execute("TRUNCATE TABLE stock");
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            jdbcTemplate.execute("UNLOCK TABLES");
            transactionManager.commit(txStatus);
        }
        catch (RuntimeException e)
        {
            try
            {
                sLog.warn("Rolling back addStock transaction after encountering error: %s");
                transactionManager.rollback(txStatus);
            }
            catch (Exception inner)
            {
                sLog.error("Error while rolling back transaction", inner);
            }
            throw e;
        }
    }

    protected String[] getConfigLocations()
    {
        return new String[]{"classpath:test-context.xml"};
    }

    protected Element buildXMLFragment(String rawXML) throws ParserConfigurationException, IOException, SAXException
    {
        return documentBuilderFactory
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream(rawXML.getBytes()))
                .getDocumentElement();
    }

    protected void assertEmptySuccessResponse(ModelAndView modelAndView)
    {
        assertEquals(modelAndView.getViewName(), "Success");
        assertNull(modelAndView.getModel().get("content"));
    }

    protected <T> T assertSuccessResponseAndReturnContent(ModelAndView modelAndView, Class<T> expectedClass)
    {
        assertEquals(modelAndView.getViewName(), "Success");
        Object content = modelAndView.getModel().get("content");
        assertNotNull(content);
        assertTrue(content.getClass().equals(expectedClass));
        return (T) content;
    }

    protected MockHttpServletRequest createGetRequest(String action)
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(METHOD_GET);
        request.setParameter("action", action);
        return request;
    }

    protected MockHttpServletRequest createPostRequest(String innerXML) throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(METHOD_POST);
        String xmlWrapper = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><shipit>%s</shipit>";
        request.setContent(String.format(xmlWrapper, innerXML).getBytes("UTF-8"));
        return request;
    }
}
