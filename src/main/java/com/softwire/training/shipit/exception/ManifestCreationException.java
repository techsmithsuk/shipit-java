package com.softwire.training.shipit.exception;

public class ManifestCreationException extends ClientVisibleException
{
    public ManifestCreationException()
    {
    }

    public ManifestCreationException(String message)
    {
        super(message);
    }

    public ManifestCreationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ManifestCreationException(Throwable cause)
    {
        super(cause);
    }

    public int getErrorCode()
    {
        return ErrorCodes.MANIFEST_CREATION_EXCEPTION;
    }
}
