package com.softwire.training.shipit;

import com.softwire.training.shipit.builder.CompanyBuilder;
import com.softwire.training.shipit.builder.EmployeeBuilder;
import com.softwire.training.shipit.builder.ProductBuilder;
import com.softwire.training.shipit.controller.ProductController;
import com.softwire.training.shipit.exception.MalformedRequestException;
import com.softwire.training.shipit.exception.NoSuchEntityException;
import com.softwire.training.shipit.model.Employee;
import com.softwire.training.shipit.model.Product;
import com.softwire.training.shipit.model.Products;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

public class ProductTest extends AbstractBaseTest
{
    private static final int WAREHOUSE_ID = 1;
    private static final Employee EMPLOYEE = new EmployeeBuilder().setWarehouseId(WAREHOUSE_ID).createEmployee();
    private static final String GTIN = "0000346374230";

    private static final String PRODUCT_WITH_ID_XML = "<product>" +
            "<id>1</id>" +
            "<gtin>" + GTIN + "</gtin>" +
            "<gcp>0000346</gcp>" +
            "<name>2 Count 1 T30 Torx Bit Tips TX</name>" +
            "<weight>300.0</weight>" +
            "<lowerThreshold>322</lowerThreshold>" +
            "<discontinued>0</discontinued><" +
            "minimumOrderQuantity>108</minimumOrderQuantity>" +
            "</product>";
    private static final String PRODUCT_XML = "<product>" +
            "<gtin>" + GTIN + "</gtin>" +
            "<gcp>0000346</gcp>" +
            "<name>2 Count 1 T30 Torx Bit Tips TX</name>" +
            "<weight>300.0</weight>" +
            "<lowerThreshold>322</lowerThreshold>" +
            "<discontinued>0</discontinued><" +
            "minimumOrderQuantity>108</minimumOrderQuantity>" +
            "</product>";

    private ProductController productController;

    public void setProductController(ProductController productController)
    {
        this.productController = productController;
    }

    public void onSetUp() throws Exception
    {
        super.onSetUp();
        employeeDAO.addEmployees(Collections.singletonList(EMPLOYEE));
        companyDAO.addCompanies(Collections.singletonList(new CompanyBuilder().createCompany()));
    }

    public void testXMLParsingAndRendering() throws Exception
    {
        assertEquals(new ProductBuilder().setId(null).createProduct(), Product.parseXML(buildXMLFragment(PRODUCT_XML)));
        assertEquals(PRODUCT_WITH_ID_XML, new ProductBuilder().setId(1).createProduct().renderXML());
    }

    public void testRoundtripProductDAO() throws Exception
    {
        Product product = new ProductBuilder().createProduct();
        productDAO.addProducts(Collections.singletonList(product));
        assertEquals(productDAO.getProduct(product.getId()), product);
    }

    public void testGetProduct() throws Exception
    {
        Product product = new ProductBuilder().setGtin(GTIN).createProduct();
        productDAO.addProducts(Collections.singletonList(product));

        MockHttpServletRequest request = createGetRequest("get");
        request.setParameter("gtin", GTIN);
        MockHttpServletResponse response = new MockHttpServletResponse();

        ModelAndView modelAndView = productController.handleRequest(request, response);

        assertEquals(modelAndView.getModel().get("content"), product);
    }

    public void testGetNonexistentProduct() throws Exception
    {
        MockHttpServletRequest request = createGetRequest("get");
        request.setParameter("gtin", GTIN);
        MockHttpServletResponse response = new MockHttpServletResponse();

        try
        {
            productController.handleRequest(request, response);
        }
        catch (NoSuchEntityException e)
        {
            assertTrue(e.getMessage().contains(GTIN));
        }
    }

    public void testAddProducts() throws Exception
    {
        MockHttpServletRequest request = createPostRequest(String.format("<products>%s</products>", PRODUCT_XML));

        Products products = assertSuccessResponseAndReturnContent(
                productController.handleRequest(request, new MockHttpServletResponse()),
                Products.class);

        assertEquals(products.getProducts().size(), 1);
        Product actual = products.getProducts().get(0);
        Product expected = new ProductBuilder().setId(actual.getId()).createProduct();
        assertEquals(expected, actual);
        assertNull(stockDAO.getStock(WAREHOUSE_ID, actual.getId()));
    }

    public void testAddPreexistingProduct() throws Exception
    {
        Product product = new ProductBuilder().createProduct();
        productDAO.addProducts(Collections.singletonList(product));

        MockHttpServletRequest request = createPostRequest(String.format("<products>%s</products>", PRODUCT_XML));

        try
        {
            productController.handleRequest(request, new MockHttpServletResponse());
            fail("Expected exception to be thrown");
        }
        catch (MalformedRequestException e)
        {
            assertTrue(e.getMessage().contains(GTIN));
        }

        assertEquals(productDAO.getProduct(product.getId()), product);
    }

    public void testAddDuplicateProduct() throws Exception
    {
        MockHttpServletRequest request = createPostRequest(
                String.format("<products>%s%s</products>", PRODUCT_XML, PRODUCT_XML));

        try
        {
            productController.handleRequest(request, new MockHttpServletResponse());
            fail("Expected exception to be thrown");
        }
        catch (MalformedRequestException e)
        {
            assertTrue(e.getMessage().contains(GTIN));
        }
    }

    public void testDiscontinueProduct() throws Exception
    {
        Product product = new ProductBuilder().createProduct();
        productDAO.addProducts(Collections.singletonList(product));

        MockHttpServletRequest request = createGetRequest("discontinue");
        request.setParameter("gtin", GTIN);

        assertEmptySuccessResponse(productController.handleRequest(request, new MockHttpServletResponse()));
        assertTrue(productDAO.getProduct(product.getId()).isDiscontinued());
    }

    public void testDiscontinueNonexistantProduct() throws Exception
    {
        MockHttpServletRequest request = createGetRequest("discontinue");
        String nonexistantGTIN = "00000000";
        request.setParameter("gtin", nonexistantGTIN);

        try
        {
            productController.handleRequest(request, new MockHttpServletResponse());
            fail("Expected exception to be thrown");
        }
        catch (NoSuchEntityException e)
        {
            assertTrue(e.getMessage().contains(nonexistantGTIN));
        }
    }

    public void testDiscontinueNonexistentProduct() throws Exception
    {
        MockHttpServletRequest request = createGetRequest("discontinue");
        request.setParameter("gtin", GTIN);

        try
        {
            productController.handleRequest(request, new MockHttpServletResponse());
            fail("Expected exception to be thrown");
        }
        catch (NoSuchEntityException e)
        {
            assertTrue(e.getMessage().contains(GTIN));
        }
    }
}