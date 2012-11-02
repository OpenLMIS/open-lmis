package org.openlmis.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController extends BaseController {

    @RequestMapping(value = "home", method = RequestMethod.GET)
    public String loadHelloWorld() {
        return "index.html";
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String homeDefault(HttpServletRequest request) {
        return homePageUrl(request);
    }

}
