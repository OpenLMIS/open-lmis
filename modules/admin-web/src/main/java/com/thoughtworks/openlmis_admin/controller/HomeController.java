package com.thoughtworks.openlmis_admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created with IntelliJ IDEA.
 * User: Manan
 * Date: 10/17/12
 * Time: 11:05 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class HomeController {

    @RequestMapping(value = "home", method = RequestMethod.GET)
    public ModelAndView loadHelloWorld(){
        //  System.out.println("in controller");
        return new ModelAndView("admin-hello");
    }

    @RequestMapping(value = "",method = RequestMethod.GET)
    public ModelAndView loadHomePage(){
        return new ModelAndView("redirect:home");
    }
}