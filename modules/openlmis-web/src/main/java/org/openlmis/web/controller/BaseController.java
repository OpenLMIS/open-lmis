package org.openlmis.web.controller;

import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    protected String loggedInUser(HttpServletRequest request) {
        return (String) request.getSession().getAttribute(UserAuthenticationSuccessHandler.USER);
    }

    protected Boolean isAdmin(HttpServletRequest request) {
        return (Boolean) request.getSession().getAttribute(UserAuthenticationSuccessHandler.IS_ADMIN);
    }

    protected String homePageUrl(HttpServletRequest request) {
        return  "redirect:/public/pages/index.html" ;
    }

}
