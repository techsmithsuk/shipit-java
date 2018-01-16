package com.softwire.training.shipit.controller;

import com.softwire.training.shipit.dao.CompanyDAO;
import com.softwire.training.shipit.dao.EmployeeDAO;
import com.softwire.training.shipit.dao.ProductDAO;
import com.softwire.training.shipit.dao.StockDAO;
import com.softwire.training.shipit.model.RenderableAsXML;
import com.softwire.training.shipit.model.Status;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StatusController extends BaseController
{
    private StockDAO stockDAO;
    private ProductDAO productDAO;
    private EmployeeDAO employeeDAO;
    private CompanyDAO companyDAO;

    public void setStockDAO(StockDAO stockDAO)
    {
        this.stockDAO = stockDAO;
    }

    public void setProductDAO(ProductDAO productDAO)
    {
        this.productDAO = productDAO;
    }

    public void setEmployeeDAO(EmployeeDAO employeeDAO)
    {
        this.employeeDAO = employeeDAO;
    }

    public void setCompanyDAO(CompanyDAO companyDAO)
    {
        this.companyDAO = companyDAO;
    }

    protected RenderableAsXML handleGetMethod(
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        return new Status(
                employeeDAO.getWarehouseCount(),
                employeeDAO.getEmployeeCount(),
                stockDAO.getTrackedItemsCount(),
                stockDAO.getStockHeldSum(),
                productDAO.getProductCount(),
                companyDAO.getCompanyCount());
    }
}
