package com.softwire.training.shipit.exception;

public abstract class ClientVisibleException extends Exception
{
    public ClientVisibleException()
    {
    }

    public ClientVisibleException(String message)
    {
        super(message);
    }

    public ClientVisibleException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ClientVisibleException(Throwable cause)
    {
        super(cause);
    }

    public abstract int getErrorCode();
}
