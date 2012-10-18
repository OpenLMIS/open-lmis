package org.openlmis.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    @RequestMapping(value = "home", method = RequestMethod.GET)
    public ModelAndView loadHelloWorld() {
        //  System.out.println("in controller");
        return new ModelAndView("index");
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView loadHomePage() {
        return new ModelAndView("redirect:home");
    }

}