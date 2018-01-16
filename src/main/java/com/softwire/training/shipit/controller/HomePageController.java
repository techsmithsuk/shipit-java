package com.softwire.training.shipit.controller;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HomePageController extends AbstractController
{
    private static Logger sLog = Logger.getLogger(HomePageController.class);

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        sLog.info("Redirecting home page request");
        return new ModelAndView("redirect:/status.xml");
    }
}
