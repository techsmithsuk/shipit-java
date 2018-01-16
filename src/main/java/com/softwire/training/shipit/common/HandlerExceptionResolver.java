package com.softwire.training.shipit.common;

import com.softwire.training.shipit.exception.ClientVisibleException;
import com.softwire.training.shipit.exception.ErrorCodes;
import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HandlerExceptionResolver implements org.springframework.web.servlet.HandlerExceptionResolver
{
    private static final int GENERIC_ERROR_CODE = 0;

    private static Logger sLog = Logger.getLogger(HandlerExceptionResolver.class);

    public ModelAndView resolveException(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex)
    {
        sLog.error("Handling Exception", ex);
        ModelAndView model = new ModelAndView("Error");
        model.addObject("message", ex.getMessage());

        int code = GENERIC_ERROR_CODE;
        if (ex instanceof ClientVisibleException)
        {
            code = ((ClientVisibleException) ex).getErrorCode();
            sLog.debug(String.format("Found ClientVisibleException with code: %s", code));
        }
        else if (ex instanceof ServletRequestBindingException)
        {
            code = ErrorCodes.MALFORMED_REQUEST;
            sLog.debug(String.format("Found ServletRequestBindingException, setting code = %s", code));
        }

        model.addObject("code", code);
        return model;
    }
}
