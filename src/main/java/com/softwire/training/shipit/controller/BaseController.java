package com.softwire.training.shipit.controller;

import com.softwire.training.shipit.exception.MalformedRequestException;
import com.softwire.training.shipit.model.RenderableAsXML;
import org.apache.log4j.Logger;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public abstract class BaseController extends AbstractController
{
    private static Logger sLog = Logger.getLogger(BaseController.class);

    protected PlatformTransactionManager transactionManager;
    private DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

    public void setTransactionManager(PlatformTransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }

    protected RenderableAsXML handlePostMethod(
            Element documentElement,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        sLog.debug("Rejecting unhandled POST method");
        throw new HttpRequestMethodNotSupportedException(request.getMethod());
    }

    protected RenderableAsXML handleGetMethod(
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        sLog.debug("Rejecting unhandled GET method");
        throw new HttpRequestMethodNotSupportedException(request.getMethod());
    }

    protected ModelAndView handleRequestInternal(
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        RenderableAsXML renderableAsXML;

        if (request.getMethod().equals(METHOD_GET))
        {
            sLog.debug("Handling GET request");
            renderableAsXML = handleGetMethod(request, response);
        }
        else if (request.getMethod().equals(METHOD_POST))
        {
            sLog.debug("Handling POST request");
            Element documentElement = parseXMLRequestBody(request);
            renderableAsXML = handlePostMethod(documentElement, request, response);
        }
        else
        {
            sLog.debug(String.format("Rejecting unhandled %s request", request.getMethod()));
            throw new HttpRequestMethodNotSupportedException(request.getMethod());
        }

        sLog.debug(String.format("Rendering success response with content: %s", renderableAsXML));
        return new ModelAndView("Success", "content", renderableAsXML);
    }

    private Element parseXMLRequestBody(HttpServletRequest request) throws
            ParserConfigurationException, IOException, MalformedRequestException
    {
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc;
        try
        {
            doc = dBuilder.parse(request.getInputStream());
        }
        catch (SAXException e)
        {
            throw new MalformedRequestException("Unable to parse XML request", e);
        }

        doc.getDocumentElement().normalize();
        sLog.debug("Parsed XML request body");

        Element documentElement = doc.getDocumentElement();
        String rootNodeName = documentElement.getNodeName();
        if (!rootNodeName.equals("shipit"))
        {
            throw new MalformedRequestException(
                    String.format("Expected XML root node to be shipit, but was %s", rootNodeName));
        }
        return documentElement;
    }
}
