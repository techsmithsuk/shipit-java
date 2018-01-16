package com.softwire.training.shipit.controller;

import com.softwire.training.shipit.dao.ProductDAO;
import com.softwire.training.shipit.exception.ClientVisibleException;
import com.softwire.training.shipit.exception.InvalidStateException;
import com.softwire.training.shipit.exception.MalformedRequestException;
import com.softwire.training.shipit.exception.NoSuchEntityException;
import com.softwire.training.shipit.model.Product;
import com.softwire.training.shipit.model.Products;
import com.softwire.training.shipit.model.RenderableAsXML;
import com.softwire.training.shipit.utils.TransactionManagerUtils;
import com.softwire.training.shipit.utils.XMLParsingUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Map;

public class ProductController extends BaseController
{
    private static Logger sLog = Logger.getLogger(ProductController.class);

    private ProductDAO productDAO;

    public void setProductDAO(ProductDAO productDAO)
    {
        this.productDAO = productDAO;
    }

    protected RenderableAsXML handlePostMethod(
            Element documentElement,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        Node productsNode = XMLParsingUtils.getSingleElementByTagName(documentElement, "products");

        List<Product> products = new ArrayList<Product>();
        List<String> gtinsToAdd = new ArrayList<String>();
        NodeList productNodes = productsNode.getChildNodes();
        for (int i = 0; i < productNodes.getLength(); i++)
        {
            Node node = productNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE)
            {
                continue;
            }

            Product productToAdd = Product.parseXML((Element) node);

            if (gtinsToAdd.contains(productToAdd.getGtin()))
            {
                throw new MalformedRequestException(String.format("Cannot add products with duplicate gtins: %s",
                        productToAdd.getGtin()));
            }
            products.add(productToAdd);
            gtinsToAdd.add(productToAdd.getGtin());
        }

        if (products.size() == 0)
        {
            throw new MalformedRequestException("Expected at least one <product> tag");
        }

        sLog.info("Adding products: " + products);

        TransactionStatus txStatus = transactionManager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.ISOLATION_SERIALIZABLE));
        try
        {
            Map<String, Product> conflicts = productDAO.getProductsByGtin(gtinsToAdd);
            if (conflicts.size() > 0)
            {
                throw new MalformedRequestException(String.format("Cannot add products with existing gtins: %s",
                        StringUtils.join(conflicts.values(), ", ")));
            }

            productDAO.addProducts(products);
            transactionManager.commit(txStatus);
        }
        catch (Exception e)
        {
            TransactionManagerUtils.rollbackIgnoringErrors(transactionManager, txStatus, sLog);
            throw e;
        }

        sLog.debug("Products added successfully");
        return new Products(products);
    }

    protected RenderableAsXML handleGetMethod(
            HttpServletRequest request,
            HttpServletResponse response) throws ClientVisibleException, ServletRequestBindingException
    {
        String action = ServletRequestUtils.getStringParameter(request, "action");

        if ("get".equals(action))
        {
            return handleGetAction(request);
        }
        else if ("discontinue".equals(action))
        {
            return handleDiscontinueAction(request);
        }
        else
        {
            throw new MalformedRequestException("Invalid or missing action: " + action);
        }
    }

    private RenderableAsXML handleGetAction(HttpServletRequest request)
            throws MalformedRequestException, ServletRequestBindingException, NoSuchEntityException
    {
        String gtin = ServletRequestUtils.getStringParameter(request, "gtin");

        if (gtin == null)
        {
            throw new MalformedRequestException("Unable to parse gtin from request parameters");
        }

        sLog.info(String.format("Looking up product by gtin: %s", gtin));

        Product product = productDAO.getProductByGtin(gtin);
        if (product == null)
        {
            throw new NoSuchEntityException("No product exists with gtin: " + gtin);
        }

        sLog.info("Found product: " + product);
        return product;
    }

    private RenderableAsXML handleDiscontinueAction(HttpServletRequest request)
            throws ServletRequestBindingException, MalformedRequestException, NoSuchEntityException, InvalidStateException
    {
        String gtin = ServletRequestUtils.getStringParameter(request, "gtin");

        if (gtin == null)
        {
            throw new MalformedRequestException("Unable to parse gtin from request parameters");
        }

        sLog.info(String.format("Discontinuing up product by gtin: %s", gtin));

        try
        {
            productDAO.discontinueProduct(gtin);
        }
        catch (EmptyResultDataAccessException e)
        {
            throw new NoSuchEntityException("No product exists with gtin: " + gtin, e);
        }

        sLog.info("Discontinued product: " + gtin);
        return null;
    }
}
