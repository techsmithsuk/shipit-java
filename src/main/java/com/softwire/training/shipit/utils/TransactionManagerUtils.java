package com.softwire.training.shipit.utils;

import org.apache.log4j.Logger;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

public class TransactionManagerUtils
{
    public static void rollbackIgnoringErrors(
            PlatformTransactionManager transactionManager,
            TransactionStatus txStatus,
            Logger logger)
    {
        try
        {
            logger.warn("Rolling back transaction after encountering error: %s");
            transactionManager.rollback(txStatus);
        }
        catch (Exception inner)
        {
            logger.error("Error while rolling back transaction", inner);
        }
    }
}
