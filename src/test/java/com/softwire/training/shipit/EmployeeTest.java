package com.softwire.training.shipit;

import com.softwire.training.shipit.builder.EmployeeBuilder;
import com.softwire.training.shipit.controller.EmployeeController;
import com.softwire.training.shipit.exception.InvalidStateException;
import com.softwire.training.shipit.exception.MultipleEntitiesException;
import com.softwire.training.shipit.exception.NoSuchEntityException;
import com.softwire.training.shipit.model.Employee;
import com.softwire.training.shipit.model.Employees;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.web.servlet.support.WebContentGenerator.METHOD_POST;

public class EmployeeTest extends AbstractBaseTest
{
    private static final String NEW_EMPLOYEE_XML =
            "<employee><name>Gissell Sadeem</name><warehouseId>1</warehouseId><role>OPERATIONS_MANAGER</role><ext>73996</ext></employee>";
    private static final String EMPLOYEE_XML =
            "<employee><id>1</id><name>Gissell Sadeem</name><warehouseId>1</warehouseId><role>OPERATIONS_MANAGER</role><ext>73996</ext></employee>";

    private EmployeeController employeeController;

    public void setEmployeeController(EmployeeController employeeController)
    {
        this.employeeController = employeeController;
    }

    public void testRoundtripEmployeeDAO() throws InvalidStateException
    {
        Employee employee = new EmployeeBuilder().createEmployee();
        employeeDAO.addEmployees(Collections.singletonList(employee));
        assertEquals(employeeDAO.getEmployee(employee.getId()), employee);
    }

    public void testXMLParsing() throws Exception
    {
        assertEquals(new EmployeeBuilder().setId(null).createEmployee(),
                Employee.parseXML(buildXMLFragment(NEW_EMPLOYEE_XML)));
        assertEquals(new EmployeeBuilder().setId(1).createEmployee().renderXML(), EMPLOYEE_XML);
    }

    public void testGetEmployeeByName() throws Exception
    {
        Employee employee = new EmployeeBuilder().createEmployee();
        employeeDAO.addEmployees(Collections.singletonList(employee));

        MockHttpServletRequest request = createGetRequest("get");
        request.setParameter("name", employee.getName());
        MockHttpServletResponse response = new MockHttpServletResponse();

        ModelAndView modelAndView = employeeController.handleRequest(request, response);

        assertEquals(modelAndView.getModel().get("content"), employee);
    }

    public void testGetEmployeeById() throws Exception
    {
        Employee employee = new EmployeeBuilder().createEmployee();
        employeeDAO.addEmployees(Collections.singletonList(employee));

        MockHttpServletRequest request = createGetRequest("get");
        request.setParameter("employeeId", Integer.toString(employee.getId()));
        MockHttpServletResponse response = new MockHttpServletResponse();

        ModelAndView modelAndView = employeeController.handleRequest(request, response);

        assertEquals(modelAndView.getModel().get("content"), employee);
    }

    public void testGetEmployeesByWarehouseId() throws Exception
    {
        Employee employee = new EmployeeBuilder().createEmployee();
        employeeDAO.addEmployees(Collections.singletonList(employee));

        MockHttpServletRequest request = createGetRequest("get");
        request.setParameter("warehouseId", Integer.toString(employee.getWarehouseId()));
        MockHttpServletResponse response = new MockHttpServletResponse();

        ModelAndView modelAndView = employeeController.handleRequest(request, response);

        assertEquals(modelAndView.getModel().get("content"), new Employees(Collections.singletonList(employee)));
    }

    public void testGetNonExistentEmployeeByName() throws Exception
    {
        MockHttpServletRequest request = createGetRequest("get");
        request.setParameter("name", "nobody");
        MockHttpServletResponse response = new MockHttpServletResponse();

        try
        {
            employeeController.handleRequest(request, response);
            fail("Expected exception to be thrown");
        }
        catch (NoSuchEntityException e)
        {
            assertTrue(e.getMessage().contains("nobody"));
        }
    }

    public void testGetNonExistentEmployeeById() throws Exception
    {
        MockHttpServletRequest request = createGetRequest("get");
        request.setParameter("employeeId", "99999");
        MockHttpServletResponse response = new MockHttpServletResponse();

        try
        {
            employeeController.handleRequest(request, response);
            fail("Expected exception to be thrown");
        }
        catch (NoSuchEntityException e)
        {
            assertTrue(e.getMessage().contains("99999"));
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

    public void testGetDuplicateEmployeeByName() throws Exception
    {
        String name = "Bob Fossil";
        Employee employee1 = new EmployeeBuilder().setName(name).createEmployee();
        Employee employee2 = new EmployeeBuilder().setName(name).createEmployee();
        employeeDAO.addEmployees(Arrays.asList(employee1, employee2));
        MockHttpServletRequest request = createGetRequest("get");
        request.addParameter("name", name);

        try
        {
            employeeController.handleRequest(request, new MockHttpServletResponse());
        }
        catch (MultipleEntitiesException e)
        {
            assertTrue(e.getMessage().contains(name));
        }
    }

    public void testAddEmployees() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(METHOD_POST);
        String xmlWrapper = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><shipit><employees>%s</employees></shipit>";
        request.setContent(String.format(xmlWrapper, NEW_EMPLOYEE_XML).getBytes("UTF-8"));

        Employees employees = assertSuccessResponseAndReturnContent(
                employeeController.handleRequest(request, new MockHttpServletResponse()),
                Employees.class);

        assertEquals(1, employees.getEmployees().size());
        Employee actual = employees.getEmployees().get(0);
        assertEquals(actual, new EmployeeBuilder().setId(actual.getId()).createEmployee());
    }

    public void testDeleteEmployeeByName() throws Exception
    {
        Employee employee = new EmployeeBuilder().createEmployee();
        employeeDAO.addEmployees(Collections.singletonList(employee));

        MockHttpServletRequest request = createGetRequest("delete");
        request.setParameter("name", employee.getName());

        assertEmptySuccessResponse(employeeController.handleRequest(request, new MockHttpServletResponse()));

        assertNull(employeeDAO.getEmployee(employee.getId()));
    }

    public void testDeleteEmployeeById() throws Exception
    {
        Employee employee = new EmployeeBuilder().createEmployee();
        employeeDAO.addEmployees(Collections.singletonList(employee));

        MockHttpServletRequest request = createGetRequest("delete");
        request.setParameter("employeeId", Integer.toString(employee.getId()));

        assertEmptySuccessResponse(employeeController.handleRequest(request, new MockHttpServletResponse()));

        assertNull(employeeDAO.getEmployee(employee.getId()));
    }


    public void testDeleteNonexistentEmployee() throws Exception
    {
        MockHttpServletRequest request = createGetRequest("delete");
        request.setParameter("name", "nobody");

        try
        {
            assertEmptySuccessResponse(employeeController.handleRequest(request, new MockHttpServletResponse()));
            fail("Expected exception to be thrown");
        }
        catch (NoSuchEntityException e)
        {
            assertTrue(e.getMessage().contains("nobody"));
        }
    }

    public void testDeleteNonexistentEmployeeById() throws Exception
    {
        MockHttpServletRequest request = createGetRequest("delete");
        request.setParameter("employeeId", "99999");

        try
        {
            assertEmptySuccessResponse(employeeController.handleRequest(request, new MockHttpServletResponse()));
            fail("Expected exception to be thrown");
        }
        catch (NoSuchEntityException e)
        {
            assertTrue(e.getMessage().contains("99999"));
        }
    }

    public void testAddDuplicateEmployee() throws Exception
    {
        Employee employee = new EmployeeBuilder().createEmployee();
        employeeDAO.addEmployees(Collections.singletonList(employee));
        MockHttpServletRequest request = createPostRequest(String.format("<employees>%s</employees>", NEW_EMPLOYEE_XML));

        List<Employee> employees = assertSuccessResponseAndReturnContent(
                employeeController.handleRequest(request, new MockHttpServletResponse()),
                Employees.class).getEmployees();

        assertEquals(1, employees.size());
        Employee actual = employees.get(0);
        assertEquals(actual, new EmployeeBuilder().setId(actual.getId()).createEmployee());
    }
}
