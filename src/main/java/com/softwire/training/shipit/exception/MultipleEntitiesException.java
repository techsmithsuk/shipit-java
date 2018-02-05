package com.softwire.training.shipit.exception;

public class MultipleEntitiesException extends ClientVisibleException
{
    public MultipleEntitiesException()
    {
    }

    public MultipleEntitiesException(String message)
    {
        super(message);
    }

    public MultipleEntitiesException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public MultipleEntitiesException(Throwable cause)
    {
        super(cause);
    }

    public int getErrorCode()
    {
        return ErrorCodes.MULTIPLE_ENTITIES_EXCEPTION;
    }
}
