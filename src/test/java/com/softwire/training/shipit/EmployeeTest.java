package com.softwire.training.shipit;

import com.softwire.training.shipit.controller.EmployeeController;
import com.softwire.training.shipit.exception.NoSuchEntityException;
import com.softwire.training.shipit.model.Employee;
import com.softwire.training.shipit.model.EmployeeRole;
import com.softwire.training.shipit.model.Employees;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

import static org.springframework.web.servlet.support.WebContentGenerator.METHOD_POST;

public class EmployeeTest extends AbstractBaseTest
{
    private static final Employee EMPLOYEE = new Employee(
            "Gissell Sadeem", 1, EmployeeRole.OPERATIONS_MANAGER, "73996");
    private static final String EMPLOYEE_XML =
            "<employee><name>Gissell Sadeem</name><warehouseId>1</warehouseId><role>OPERATIONS_MANAGER</role><ext>73996</ext></employee>";
    private static final String EMPLOYEE_NAME = "Gissell Sadeem";

    private EmployeeController employeeController;

    public void setEmployeeController(EmployeeController employeeController)
    {
        this.employeeController = employeeController;
    }

    public void testRoundtripEmployeeDAO()
    {
        employeeDAO.addEmployees(Collections.singletonList(EMPLOYEE));
        assertEquals(employeeDAO.getEmployee(EMPLOYEE_NAME), EMPLOYEE);
    }

    public void testRoundtripXMLParsingAndRendering() throws Exception
    {
        assertEquals(EMPLOYEE_XML, Employee.parseXML(buildXMLFragment(EMPLOYEE_XML)).renderXML());
        assertEquals(EMPLOYEE, Employee.parseXML(buildXMLFragment(EMPLOYEE.renderXML())));
    }

    public void testGetEmployeeByName() throws Exception
    {
        employeeDAO.addEmployees(Collections.singletonList(EMPLOYEE));

        MockHttpServletRequest request = createGetRequest("get");
        request.setParameter("name", EMPLOYEE_NAME);
        MockHttpServletResponse response = new MockHttpServletResponse();

        ModelAndView modelAndView = employeeController.handleRequest(request, response);

        assertEquals(modelAndView.getModel().get("content"), EMPLOYEE);
    }

    public void testGetEmployeesByWarehouseId() throws Exception
    {
        employeeDAO.addEmployees(Collections.singletonList(EMPLOYEE));

        MockHttpServletRequest request = createGetRequest("get");
        request.setParameter("warehouseId", "1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        ModelAndView modelAndView = employeeController.handleRequest(request, response);

        assertEquals(modelAndView.getModel().get("content"), new Employees(Collections.singletonList(EMPLOYEE)));
    }

    public void testGetNonExistentEmployee() throws Exception
    {
        MockHttpServletRequest request = createGetRequest("get");
        request.setParameter("name", EMPLOYEE_NAME);
        MockHttpServletResponse response = new MockHttpServletResponse();

        try
        {
            employeeController.handleRequest(request, response);
            fail("Expected exception to be thrown");
        }
        catch (NoSuchEntityException e)
        {
            assertTrue(e.getMessage().contains(EMPLOYEE_NAME));
        }
    }

    public void testGetEmployeeInNonexistentWarehouse() throws Exception
    {
        MockHttpServletRequest request = createGetRequest("get");
        String warehouseId = "4";
        request.setParameter("warehouseId", warehouseId);
        MockHttpServletResponse response = new MockHttpServletResponse();

        try
        {
            employeeController.handleRequest(request, response);
            fail("Expected exception to be thrown");
        }
        catch (NoSuchEntityException e)
        {
            assertTrue(e.getMessage().contains(warehouseId));
        }
    }

    public void testAddEmployees() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(METHOD_POST);
        String xmlWrapper = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><shipit><employees>%s</employees></shipit>";
        request.setContent(String.format(xmlWrapper, EMPLOYEE_XML).getBytes("UTF-8"));

        assertEmptySuccessResponse(employeeController.handleRequest(request, new MockHttpServletResponse()));

        assertEquals(employeeDAO.getEmployee(EMPLOYEE_NAME), EMPLOYEE);
    }

    public void testDeleteEmployees() throws Exception
    {
        employeeDAO.addEmployees(Collections.singletonList(EMPLOYEE));

        MockHttpServletRequest request = createGetRequest("delete");
        request.setParameter("name", EMPLOYEE_NAME);

        assertEmptySuccessResponse(employeeController.handleRequest(request, new MockHttpServletResponse()));

        assertNull(employeeDAO.getEmployee(EMPLOYEE_NAME));
    }

    public void testDeleteNonexistentEmployee() throws Exception
    {
        MockHttpServletRequest request = createGetRequest("delete");
        request.setParameter("name", EMPLOYEE_NAME);

        try
        {
            assertEmptySuccessResponse(employeeController.handleRequest(request, new MockHttpServletResponse()));
            fail("Expected exception to be thrown");
        }
        catch (NoSuchEntityException e)
        {
            assertTrue(e.getMessage().contains(EMPLOYEE_NAME));
        }
    }

    public void testAddDuplicateEmployee() throws Exception
    {
        employeeDAO.addEmployees(Collections.singletonList(EMPLOYEE));
        MockHttpServletRequest request = createPostRequest(String.format("<employees>%s</employees>", EMPLOYEE_XML));

        try
        {
            assertEmptySuccessResponse(employeeController.handleRequest(request, new MockHttpServletResponse()));
            fail("Expected exception to be thrown");
        }
        catch (DataIntegrityViolationException e)
        {
            assertTrue(e.getMessage().contains(EMPLOYEE_NAME));
        }
    }
}
