package com.softwire.training.shipit;

import com.softwire.training.shipit.builder.CompanyBuilder;
import com.softwire.training.shipit.builder.EmployeeBuilder;
import com.softwire.training.shipit.builder.ProductBuilder;
import com.softwire.training.shipit.controller.InboundOrderController;
import com.softwire.training.shipit.dao.StockAlteration;
import com.softwire.training.shipit.exception.ValidationException;
import com.softwire.training.shipit.model.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Collections;
import java.util.List;

public class InboundOrderTest extends AbstractBaseTest
{
    private static final Company COMPANY = new CompanyBuilder().createCompany();
    private static final String GCP = COMPANY.getGcp();
    private static final Employee OPS_MANAGER = new EmployeeBuilder().createEmployee();
    private static final int WAREHOUSE_ID = OPS_MANAGER.getWarehouseId();

    private InboundOrderController inboundOrderController;
    private Product product;
    private Integer productId;

    public void setInboundOrderController(InboundOrderController inboundOrderController)
    {
        this.inboundOrderController = inboundOrderController;
    }

    public void onSetUp() throws Exception
    {
        super.onSetUp();

        employeeDAO.addEmployees(Collections.singletonList(OPS_MANAGER));
        companyDAO.addCompanies(Collections.singletonList(COMPANY));
        List<Product> products = Collections.singletonList(new ProductBuilder().createProduct());
        productDAO.addProducts(products);
        product = products.get(0);
        productId = product.getId();
    }

    public void testCreateOrderNoProductsHeld() throws Exception
    {
        MockHttpServletRequest request = createGetRequest("create");
        request.setParameter("warehouseId", Integer.toString(WAREHOUSE_ID));

        InboundOrder inboundOrder = assertSuccessResponseAndReturnContent(
                inboundOrderController.handleRequest(request, new MockHttpServletResponse()), InboundOrder.class);

        assertEquals(inboundOrder.getWarehouseId(), WAREHOUSE_ID);
        assertEquals(inboundOrder.getOperationsManager(), OPS_MANAGER);
        assertEquals(inboundOrder.getSegments().size(), 0);
    }

    public void testCreateOrderProductHoldingNoStock() throws Exception
    {
        stockDAO.addStock(OPS_MANAGER.getWarehouseId(),
                Collections.singletonList(new StockAlteration(productId, 0)));

        MockHttpServletRequest request = createGetRequest("create");
        request.setParameter("warehouseId", "1");

        InboundOrder inboundOrder = assertSuccessResponseAndReturnContent(
                inboundOrderController.handleRequest(request, new MockHttpServletResponse()), InboundOrder.class);

        assertEquals(inboundOrder.getSegments().size(), 1);
        OrderSegment orderSegment = inboundOrder.getSegments().get(0);
        assertEquals(orderSegment.getCompany().getGcp(), GCP);
    }

    public void testCreateOrderProductHoldingSufficientStock() throws Exception
    {
        stockDAO.addStock(OPS_MANAGER.getWarehouseId(),
                Collections.singletonList(new StockAlteration(productId, product.getLowerThreshold())));

        MockHttpServletRequest request = createGetRequest("create");
        request.setParameter("warehouseId", "1");

        InboundOrder inboundOrder = assertSuccessResponseAndReturnContent(
                inboundOrderController.handleRequest(request, new MockHttpServletResponse()), InboundOrder.class);

        assertEquals(inboundOrder.getSegments().size(), 0);
    }

    public void testCreateOrderDiscontinuedProduct() throws Exception
    {
        stockDAO.addStock(OPS_MANAGER.getWarehouseId(),
                Collections.singletonList(new StockAlteration(productId, product.getLowerThreshold() - 1)));
        productDAO.discontinueProduct(product.getGtin());

        MockHttpServletRequest request = createGetRequest("create");
        request.setParameter("warehouseId", "1");

        InboundOrder inboundOrder = assertSuccessResponseAndReturnContent(
                inboundOrderController.handleRequest(request, new MockHttpServletResponse()), InboundOrder.class);

        assertEquals(inboundOrder.getSegments().size(), 0);
    }

    public void testParseInboundManifest() throws Exception
    {
        int quantity = 12;
        String gtin = product.getGtin();

        String inboundManifestXML = "<inboundManifest>" +
                "<warehouseId>1</warehouseId>" +
                "<gcp>" + GCP + "</gcp>" +
                "<orderLines>" +
                "<orderLine><gtin>" + gtin + "</gtin><quantity>" + quantity + "</quantity></orderLine>" +
                "</orderLines>" +
                "</inboundManifest>";

        assertEquals(InboundManifest.parseXML(buildXMLFragment(inboundManifestXML)),
                new InboundManifest(1, GCP, Collections.singletonList(new OrderLine(gtin, quantity))));
    }

    public void testProcessManifest() throws Exception
    {
        int quantity = 12;
        String gtin = product.getGtin();
        MockHttpServletRequest request = createPostRequest("<inboundManifest>" +
                "<warehouseId>1</warehouseId>" +
                "<gcp>" + GCP + "</gcp>" +
                "<orderLines>" +
                "<orderLine><gtin>" + gtin + "</gtin><quantity>" + quantity + "</quantity></orderLine>" +
                "</orderLines>" +
                "</inboundManifest>");

        assertEmptySuccessResponse(inboundOrderController.handleRequest(request, new MockHttpServletResponse()));

        assertEquals(stockDAO.getStock(1, productId).getHeld(), quantity);
    }

    public void testProcessManifestRejectsDodgyGCP() throws Exception
    {
        int quantity = 12;
        String gtin = product.getGtin();

        MockHttpServletRequest request = createPostRequest("<inboundManifest>" +
                "<warehouseId>1</warehouseId>" +
                "<gcp>123456789</gcp>" +
                "<orderLines>" +
                "<orderLine><gtin>" + gtin + "</gtin><quantity>" + quantity + "</quantity></orderLine>" +
                "</orderLines>" +
                "</inboundManifest>");

        try
        {
            inboundOrderController.handleRequest(request, new MockHttpServletResponse());
            fail("Expected an exception to be thrown");
        }
        catch (ValidationException e)
        {
            assertTrue(e.getMessage().contains("123456789"));
        }
    }

    public void testProcessManifestRejectsUnknownProduct() throws Exception
    {
        int quantity = 12;
        MockHttpServletRequest request = createPostRequest("<inboundManifest>" +
                "<warehouseId>1</warehouseId>" +
                "<gcp>" + GCP + "</gcp>" +
                "<orderLines>" +
                "<orderLine><gtin>0123456789</gtin><quantity>" + quantity + "</quantity></orderLine>" +
                "</orderLines>" +
                "</inboundManifest>");

        try
        {
            inboundOrderController.handleRequest(request, new MockHttpServletResponse());
            fail("Expected an exception to be thrown");
        }
        catch (ValidationException e)
        {
            assertTrue(e.getMessage().contains("0123456789"));
        }
    }

    public void testProcessManifestRejectsDuplicateGTINs() throws Exception
    {
        int quantity = 12;
        String gtin = product.getGtin();
        MockHttpServletRequest request = createPostRequest("<inboundManifest>" +
                "<warehouseId>1</warehouseId>" +
                "<gcp>" + GCP + "</gcp>" +
                "<orderLines>" +
                "<orderLine><gtin>" + gtin + "</gtin><quantity>" + quantity + "</quantity></orderLine>" +
                "<orderLine><gtin>" + gtin + "</gtin><quantity>" + quantity + "</quantity></orderLine>" +
                "</orderLines>" +
                "</inboundManifest>");

        try
        {
            inboundOrderController.handleRequest(request, new MockHttpServletResponse());
            fail("Expected exception to be thrown");
        }
        catch (ValidationException e)
        {
            assertTrue(e.getMessage().contains(gtin));
        }
    }

}
