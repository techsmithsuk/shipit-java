package com.softwire.training.shipit.controller;

import com.softwire.training.shipit.dao.EmployeeDAO;
import com.softwire.training.shipit.exception.ClientVisibleException;
import com.softwire.training.shipit.exception.MalformedRequestException;
import com.softwire.training.shipit.exception.MultipleEntitiesException;
import com.softwire.training.shipit.exception.NoSuchEntityException;
import com.softwire.training.shipit.model.Employee;
import com.softwire.training.shipit.model.Employees;
import com.softwire.training.shipit.model.RenderableAsXML;
import com.softwire.training.shipit.utils.TransactionManagerUtils;
import com.softwire.training.shipit.utils.XMLParsingUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class EmployeeController extends BaseController
{
    private static Logger sLog = Logger.getLogger(EmployeeController.class);

    private EmployeeDAO employeeDAO;

    public void setEmployeeDAO(EmployeeDAO employeeDAO)
    {
        this.employeeDAO = employeeDAO;
    }

    protected RenderableAsXML handlePostMethod(
            Element documentElement,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        Node employeesNode = XMLParsingUtils.getSingleElementByTagName(documentElement, "employees");

        List<Employee> employees = new ArrayList<Employee>();
        NodeList employeeNodes = employeesNode.getChildNodes();
        for (int i = 0; i < employeeNodes.getLength(); i++)
        {
            Node node = employeeNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE)
            {
                continue;
            }

            employees.add(Employee.parseXML((Element) node));
        }

        if (employees.size() == 0)
        {
            throw new MalformedRequestException("Expected at least one <employee> tag");
        }

        sLog.info("Adding employees: " + employees);

        TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try
        {
            employeeDAO.addEmployees(employees);
            transactionManager.commit(txStatus);
        }
        catch (Exception e)
        {
            TransactionManagerUtils.rollbackIgnoringErrors(transactionManager, txStatus, sLog);
            throw e;
        }

        sLog.debug("Employees added successfully");

        return new Employees(employees);
    }

    protected RenderableAsXML handleGetMethod(
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        String action = ServletRequestUtils.getStringParameter(request, "action");

        if ("get".equals(action))
        {
            return handleGetAction(request);
        }
        else if ("delete".equals(action))
        {
            return handleDeleteAction(request);
        }
        else
        {
            throw new MalformedRequestException("Invalid or missing action: " + action);
        }
    }

    private RenderableAsXML handleGetAction(HttpServletRequest request)
            throws ServletRequestBindingException, ClientVisibleException
    {
        String name = ServletRequestUtils.getStringParameter(request, "name");
        Integer employeeId = ServletRequestUtils.getIntParameter(request, "employeeId");
        Integer warehouseId = ServletRequestUtils.getIntParameter(request, "warehouseId");

        if (employeeId == null && warehouseId == null && name == null)
        {
            throw new MalformedRequestException(
                    "Unable to parse employeeId, warehouseId, or name from request parameters");
        }
        if ((employeeId != null && warehouseId != null) ||
                (employeeId != null && name != null) ||
                (name != null && warehouseId != null))
        {
            throw new MalformedRequestException(
                    "Only one of employeeId warehouseId, and name is valid on this request");
        }

        if (name != null)
        {
            sLog.info(String.format("Looking up employee by name: %s", name));
            Employee employee = getEmployeeByName(name);
            sLog.info("Found employee: " + employee);
            return employee;
        }
        else if (employeeId != null)
        {
            sLog.info(String.format("Looking up employee by employeeId: %s", employeeId));
            Employee employee = employeeDAO.getEmployee(employeeId);
            if (employee == null)
            {
                throw new NoSuchEntityException("No employee exists with employeeId: " + employeeId);
            }
            sLog.info("Found employee: " + employee);
            return employee;
        }
        else
        {
            sLog.info(String.format("Looking up employee by warehouseId: %s", warehouseId));
            List<Employee> employees = employeeDAO.getEmployees(warehouseId);
            if (employees.isEmpty())
            {
                throw new NoSuchEntityException("No employee exists with warehouseId: " + warehouseId);
            }
            sLog.info("Found employees: " + employees);
            return new Employees(employees);
        }
    }

    private RenderableAsXML handleDeleteAction(HttpServletRequest request)
            throws Exception
    {
        Integer employeeId = ServletRequestUtils.getIntParameter(request, "employeeId");
        String name = ServletRequestUtils.getStringParameter(request, "name");

        if (employeeId == null && name == null)
        {
            throw new MalformedRequestException("Unable to parse employeeId or name from request parameters");
        }

        if (employeeId != null && name != null)
        {
            throw new MalformedRequestException("Only one of employeeId and name is valid on this request");
        }

        if (employeeId != null)
        {
            sLog.info(String.format("Deleting employee by employeeId: %s", employeeId));
            try
            {
                employeeDAO.removeEmployee(employeeId);
            }
            catch (EmptyResultDataAccessException e)
            {
                throw new NoSuchEntityException("No employee exists with employeeId: " + employeeId, e);
            }
        }
        else
        {
            sLog.info(String.format("Deleting employee by name: %s", name));
            TransactionStatus txStatus = transactionManager.getTransaction(
                    new DefaultTransactionDefinition(TransactionDefinition.ISOLATION_SERIALIZABLE));
            try
            {
                Employee employee = getEmployeeByName(name);
                employeeDAO.removeEmployee(employee.getId());
                transactionManager.commit(txStatus);
            }
            catch (Exception e)
            {
                TransactionManagerUtils.rollbackIgnoringErrors(transactionManager, txStatus, sLog);
                throw e;
            }
        }
        return null;
    }

    private Employee getEmployeeByName(String name) throws NoSuchEntityException, MultipleEntitiesException
    {
        List<Employee> employees = employeeDAO.getEmployeesByName(name);
        if (employees.isEmpty())
        {
            throw new NoSuchEntityException("No employee exists with name: " + name);
        }
        else if (employees.size() > 1)
        {
            throw new MultipleEntitiesException("Multiple employees exist with name: " + name);
        }
        return employees.get(0);
    }

}