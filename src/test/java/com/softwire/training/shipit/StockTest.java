package com.softwire.training.shipit;

import com.softwire.training.shipit.builder.CompanyBuilder;
import com.softwire.training.shipit.builder.ProductBuilder;
import com.softwire.training.shipit.dao.StockAlteration;
import com.softwire.training.shipit.exception.InvalidStateException;

import java.util.Collections;

public class StockTest extends AbstractBaseTest
{
    public void onSetUp() throws Exception
    {
        super.onSetUp();
        companyDAO.addCompanies(Collections.singletonList(new CompanyBuilder().createCompany()));
        productDAO.addProducts(Collections.singletonList(new ProductBuilder().setId(1).createProduct()));
    }

    public void testAddNewStock() throws InvalidStateException
    {
        stockDAO.addStock(1, Collections.singletonList(new StockAlteration(1, 1)));

        assertEquals(stockDAO.getStock(1, 1).getHeld(), 1);
    }

    public void testUpdateExistingStock() throws InvalidStateException
    {
        stockDAO.addStock(1, Collections.singletonList(new StockAlteration(1, 2)));
        stockDAO.addStock(1, Collections.singletonList(new StockAlteration(1, 5)));

        int held = stockDAO.getStock(1, 1).getHeld();
        assertEquals(held, 7);
    }
}
