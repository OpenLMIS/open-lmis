package org.openlmis.web.controller;

import org.openlmis.web.authentication.UserAuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;


@Controller
public class UserController {

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public HashMap<String, String> user(HttpServletRequest httpServletRequest, @RequestParam(required = false) String error) {
        String userName = (String) httpServletRequest.getSession().getAttribute(UserAuthenticationSuccessHandler.USER);
        HashMap<String, String> params = new HashMap<String, String>();
        if (userName != null) {
            params.put("name", userName);
            params.put("authenticated", "true");
        } else {
            params.put("authenticated", "false");
            params.put("error", error);
        }
        return params;
    }

}
