package com.softwire.training.shipit.controller;

import com.softwire.training.shipit.dao.CompanyDAO;
import com.softwire.training.shipit.exception.MalformedRequestException;
import com.softwire.training.shipit.exception.NoSuchEntityException;
import com.softwire.training.shipit.model.Company;
import com.softwire.training.shipit.model.RenderableAsXML;
import com.softwire.training.shipit.utils.TransactionManagerUtils;
import com.softwire.training.shipit.utils.XMLParsingUtils;
import org.apache.log4j.Logger;
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

public class CompanyController extends BaseController
{
    private static Logger sLog = Logger.getLogger(CompanyController.class);

    private CompanyDAO companyDAO;

    public void setCompanyDAO(CompanyDAO companyDAO)
    {
        this.companyDAO = companyDAO;
    }

    protected RenderableAsXML handlePostMethod(
            Element documentElement,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        Node companies = XMLParsingUtils.getSingleElementByTagName(documentElement, "companies");

        List<Company> companiesToAdd = new ArrayList<Company>();
        NodeList companyNodes = companies.getChildNodes();
        for (int i = 0; i < companyNodes.getLength(); i++)
        {
            Node node = companyNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE)
            {
                continue;
            }

            companiesToAdd.add(Company.parseXML((Element) node));
        }

        if (companiesToAdd.size() == 0)
        {
            throw new MalformedRequestException("Expected at least one <company> tag");
        }

        sLog.info("Adding companies: " + companiesToAdd);

        TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try
        {
            companyDAO.addCompanies(companiesToAdd);
            transactionManager.commit(txStatus);
        }
        catch (Exception e)
        {
            TransactionManagerUtils.rollbackIgnoringErrors(transactionManager, txStatus, sLog);
            throw e;
        }
        sLog.debug("Companies added successfully");

        return null;
    }

    protected RenderableAsXML handleGetMethod(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String action = ServletRequestUtils.getStringParameter(request, "action");

        if ("get".equals(action))
        {
            return handleGetAction(request);
        }
        else
        {
            throw new MalformedRequestException("Invalid or missing action: " + action);
        }
    }

    private RenderableAsXML handleGetAction(HttpServletRequest request)
            throws MalformedRequestException, ServletRequestBindingException, NoSuchEntityException
    {
        String gcp = ServletRequestUtils.getStringParameter(request, "gcp");

        if (gcp == null)
        {
            throw new MalformedRequestException("Unable to parse gcp from request parameters");
        }

        sLog.info(String.format("Looking up company by name: %s", gcp));

        Company company = companyDAO.getCompany(gcp);

        if (company == null)
        {
            throw new NoSuchEntityException("No company exists with gcp: " + gcp);
        }

        sLog.info("Found company: " + company);
        return company;
    }
}
