package com.softwire.training.shipit.exception;

public class NoSuchEntityException extends ClientVisibleException
{
    public NoSuchEntityException()
    {
    }

    public NoSuchEntityException(String message)
    {
        super(message);
    }

    public NoSuchEntityException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public NoSuchEntityException(Throwable cause)
    {
        super(cause);
    }

    public int getErrorCode()
    {
        return ErrorCodes.NO_SUCH_ENTITY_EXCEPTION;
    }
}
