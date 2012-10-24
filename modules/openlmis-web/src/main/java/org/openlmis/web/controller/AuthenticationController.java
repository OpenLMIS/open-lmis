package org.openlmis.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthenticationController {

    @RequestMapping(value = "authenticate", method = RequestMethod.POST)
    @ResponseBody
    public String authenticate() {
        return "Authenticated";
    }

}
