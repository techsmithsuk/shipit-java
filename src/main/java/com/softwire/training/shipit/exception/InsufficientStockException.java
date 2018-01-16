package com.softwire.training.shipit.exception;

public class InsufficientStockException extends ClientVisibleException
{
    public InsufficientStockException()
    {
    }

    public InsufficientStockException(String message)
    {
        super(message);
    }

    public InsufficientStockException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InsufficientStockException(Throwable cause)
    {
        super(cause);
    }

    public int getErrorCode()
    {
        return ErrorCodes.INSUFFICIENT_STOCK;
    }
}
