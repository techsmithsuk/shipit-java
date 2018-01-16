package com.softwire.training.shipit.controller;

import com.softwire.training.shipit.dao.ProductDAO;
import com.softwire.training.shipit.dao.StockAlteration;
import com.softwire.training.shipit.dao.StockDAO;
import com.softwire.training.shipit.exception.InsufficientStockException;
import com.softwire.training.shipit.exception.NoSuchEntityException;
import com.softwire.training.shipit.model.*;
import com.softwire.training.shipit.utils.TransactionManagerUtils;
import com.softwire.training.shipit.utils.XMLParsingUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OutboundOrderController extends BaseController
{
    private static Logger sLog = Logger.getLogger(OutboundOrderController.class);

    private StockDAO stockDAO;
    private ProductDAO productDAO;

    public void setStockDAO(StockDAO stockDAO)
    {
        this.stockDAO = stockDAO;
    }

    public void setProductDAO(ProductDAO productDAO)
    {
        this.productDAO = productDAO;
    }

    protected RenderableAsXML handlePostMethod(
            Element documentElement,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        OutboundOrder outboundOrder = OutboundOrder.parseXML((Element)
                XMLParsingUtils.getSingleElementByTagName(documentElement, "outboundOrder"));

        sLog.info(String.format("Processing outbound order: %s", outboundOrder));

        List<String> gtins = new ArrayList<String>();
        for (OrderLine orderLine : outboundOrder.getOrderLines())
        {
            gtins.add(orderLine.getGtin());
        }
        Map<String, Product> products = productDAO.getProductsByGtin(gtins);

        List<StockAlteration> lineItems = new ArrayList<StockAlteration>();
        List<Integer> productIds = new ArrayList<Integer>();
        List<String> errors = new ArrayList<String>();
        for (OrderLine orderLine : outboundOrder.getOrderLines())
        {
            Product product = products.get(orderLine.getGtin());

            if (product == null)
            {
                errors.add(String.format("Unknown product: %s", orderLine.getGtin()));
            }
            else
            {
                Integer productId = product.getId();
                lineItems.add(new StockAlteration(productId, orderLine.getQuantity()));
                productIds.add(productId);
            }
        }

        if (errors.size() > 0)
        {
            throw new NoSuchEntityException(StringUtils.join(errors, "; "));
        }

        TransactionStatus txStatus = transactionManager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.ISOLATION_SERIALIZABLE));
        try
        {
            Map<Integer, Stock> stock = stockDAO.getStock(outboundOrder.getWarehouseId(), productIds);

            errors = new ArrayList<String>();
            for (int i = 0; i < lineItems.size(); i++)
            {
                StockAlteration lineItem = lineItems.get(i);
                OrderLine orderLine = outboundOrder.getOrderLines().get(i);

                Stock item = stock.get(lineItem.getProductId());
                if (item == null)
                {
                    errors.add(String.format("Product: %s, no stock held", orderLine.getGtin()));
                }
                else if (lineItem.getQuantity() > item.getHeld())
                {
                    errors.add(String.format("Product: %s, stock held: %s, stock to remove: %s",
                            orderLine.getGtin(), item.getHeld(), lineItem.getQuantity()));
                }
            }

            if (errors.size() > 0)
            {
                throw new InsufficientStockException(StringUtils.join(errors, "; "));
            }

            stockDAO.removeStock(outboundOrder.getWarehouseId(), lineItems);
            transactionManager.commit(txStatus);
        }
        catch (Exception e)
        {
            TransactionManagerUtils.rollbackIgnoringErrors(transactionManager, txStatus, sLog);
            throw e;
        }

        return null;
    }
}
