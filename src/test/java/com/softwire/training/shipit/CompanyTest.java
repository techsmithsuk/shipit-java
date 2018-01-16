package com.softwire.training.shipit;

import com.softwire.training.shipit.controller.CompanyController;
import com.softwire.training.shipit.exception.NoSuchEntityException;
import com.softwire.training.shipit.model.Company;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

public class CompanyTest extends AbstractBaseTest
{
    private static final String GCP = "0000346";

    private static final Company COMPANY = new Company(
            GCP,
            "Robert Bosch Tool Corporation",
            "1800 West Central",
            "wat",
            "IL",
            "60056",
            "Mount Prospect",
            "(224) 232-2407",
            "info@gs1us.org");

    private static final String COMPANY_XML = "<company>" +
            "<gcp>0000346</gcp>" +
            "<name>Robert Bosch Tool Corporation</name>" +
            "<addr2>1800 West Central</addr2>" +
            "<addr3>wat</addr3>" +
            "<addr4>IL</addr4>" +
            "<postalCode>60056</postalCode>" +
            "<city>Mount Prospect</city>" +
            "<tel>(224) 232-2407</tel>" +
            "<mail>info@gs1us.org</mail>" +
            "</company>";

    private CompanyController companyController;

    public void setCompanyController(CompanyController companyController)
    {
        this.companyController = companyController;
    }

    public void testRoundtripXMLParsingAndRendering() throws Exception
    {
        buildXMLFragment(COMPANY_XML);
        assertEquals(COMPANY_XML, Company.parseXML(buildXMLFragment(COMPANY_XML)).renderXML());
        assertEquals(COMPANY, Company.parseXML(buildXMLFragment(COMPANY.renderXML())));
    }

    public void testRoundtripCompanyDAO()
    {
        companyDAO.addCompanies(Collections.singletonList(COMPANY));
        assertEquals(companyDAO.getCompany(GCP), COMPANY);
    }

    public void testGetCompanyByGcp() throws Exception
    {
        companyDAO.addCompanies(Collections.singletonList(COMPANY));

        MockHttpServletRequest request = createGetRequest("get");
        request.setParameter("gcp", GCP);
        MockHttpServletResponse response = new MockHttpServletResponse();

        ModelAndView modelAndView = companyController.handleRequest(request, response);

        assertEquals(modelAndView.getModel().get("content"), COMPANY);
    }

    public void testGetNonExistentCompany() throws Exception
    {
        MockHttpServletRequest request = createGetRequest("get");
        request.setParameter("gcp", GCP);
        MockHttpServletResponse response = new MockHttpServletResponse();

        try
        {
            companyController.handleRequest(request, response);
            fail("Expected exception to be thrown");
        }
        catch (NoSuchEntityException e)
        {
            assertTrue(e.getMessage().contains(GCP));
        }
    }

    public void testAddCompanies() throws Exception
    {
        String xmlWrapper = "<companies>%s</companies>";
        MockHttpServletRequest request = createPostRequest(String.format(xmlWrapper, COMPANY_XML));

        assertEmptySuccessResponse(companyController.handleRequest(request, new MockHttpServletResponse()));

        assertEquals(companyDAO.getCompany(GCP), COMPANY);
    }
}
