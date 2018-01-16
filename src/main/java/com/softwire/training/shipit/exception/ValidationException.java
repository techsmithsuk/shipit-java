package com.softwire.training.shipit.exception;

public class ValidationException extends MalformedRequestException
{
    public ValidationException()
    {
    }

    public ValidationException(String message)
    {
        super(message);
    }

    public ValidationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ValidationException(Throwable cause)
    {
        super(cause);
    }
}
