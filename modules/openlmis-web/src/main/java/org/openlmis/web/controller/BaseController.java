package org.openlmis.web.controller;

import org.openlmis.web.authentication.UserAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    protected String user(HttpServletRequest request) {
        return (String) request.getSession().getAttribute(UserAuthenticationSuccessHandler.USER);
    }

    protected Boolean isAdmin(HttpServletRequest request) {
        return (Boolean) request.getSession().getAttribute(UserAuthenticationSuccessHandler.IS_ADMIN);
    }

    protected String homePageUrl(HttpServletRequest request) {
        return isAdmin(request) ? "redirect:/resources/pages/admin/index.html" : "redirect:/resources/pages/logistics/index.html";
    }

}
