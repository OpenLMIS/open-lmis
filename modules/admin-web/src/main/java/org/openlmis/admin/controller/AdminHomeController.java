package org.openlmis.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public ModelAndView loadHelloWorld() {
        return new ModelAndView("admin/index");
    }

}