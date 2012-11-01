package org.openlmis.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

    @RequestMapping(value = "home", method = RequestMethod.GET)
    public String loadHelloWorld() {
        return new String("core/index");
    }
}
