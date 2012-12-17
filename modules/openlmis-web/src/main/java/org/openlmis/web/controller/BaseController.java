package org.openlmis.web.controller;

import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    protected String loggedInUser(HttpServletRequest request) {
        return (String) request.getSession().getAttribute(UserAuthenticationSuccessHandler.USER);
    }

    protected String homePageUrl() {
        return  "redirect:/public/pages/index.html" ;
    }

}
