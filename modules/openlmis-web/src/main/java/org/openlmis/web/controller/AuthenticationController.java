package org.openlmis.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AuthenticationController {

    @RequestMapping(value = "authenticate", method = RequestMethod.GET)
    public String authenticate() {
        return "Authenticated";
    }
}
