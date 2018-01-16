package com.softwire.training.shipit.exception;

public class InvalidStateException extends ClientVisibleException
{
    public InvalidStateException()
    {
    }

    public InvalidStateException(String message)
    {
        super(message);
    }

    public InvalidStateException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InvalidStateException(Throwable cause)
    {
        super(cause);
    }

    public int getErrorCode()
    {
        return ErrorCodes.INVALID_STATE;
    }
}
