package org.openlmis.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String loadHelloWorld() {
        return new String("admin/index");
    }
}