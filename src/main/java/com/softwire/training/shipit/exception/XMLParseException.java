package com.softwire.training.shipit.exception;

public class XMLParseException extends MalformedRequestException
{
    public XMLParseException()
    {
    }

    public XMLParseException(String message)
    {
        super(message);
    }

    public XMLParseException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public XMLParseException(Throwable cause)
    {
        super(cause);
    }
}
