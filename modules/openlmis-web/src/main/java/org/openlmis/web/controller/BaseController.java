package org.openlmis.web.controller;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;

public class BaseController {

  protected String loggedInUser(HttpServletRequest request) {
    return (String) request.getSession().getAttribute(USER);
  }

  protected Integer loggedInUserId(HttpServletRequest request) {
    return (Integer) request.getSession().getAttribute(USER_ID);
  }

  protected String homePageUrl() {
    return "redirect:/public/pages/index.html";
  }

}
