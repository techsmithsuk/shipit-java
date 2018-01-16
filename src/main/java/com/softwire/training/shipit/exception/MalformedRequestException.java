package com.softwire.training.shipit.exception;

public class MalformedRequestException extends ClientVisibleException
{

    public MalformedRequestException()
    {
    }

    public MalformedRequestException(String message)
    {
        super(message);
    }

    public MalformedRequestException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public MalformedRequestException(Throwable cause)
    {
        super(cause);
    }

    public int getErrorCode()
    {
        return ErrorCodes.MALFORMED_REQUEST;
    }
}
