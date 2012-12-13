package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Right;
import org.openlmis.core.service.RoleRightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;


@Controller
@NoArgsConstructor
public class UserController extends BaseController {

  @Autowired
  private RoleRightsService roleRightService;

  public UserController(RoleRightsService roleRightService) {
    this.roleRightService = roleRightService;
  }

  @RequestMapping(value = "/user", method = RequestMethod.GET)
  public HashMap<String, String> user(HttpServletRequest httpServletRequest, @RequestParam(required = false) String error) {
    String userName = (String) httpServletRequest.getSession().getAttribute(UserAuthenticationSuccessHandler.USER);
    Boolean isAdmin = (Boolean) httpServletRequest.getSession().getAttribute(UserAuthenticationSuccessHandler.IS_ADMIN);
    HashMap<String, String> params = new HashMap<String, String>();
    if (userName != null) {
      params.put("name", userName);
      params.put("authenticated", "true");
      params.put("isAdmin", isAdmin.toString());
    } else {
      params.put("authenticated", "false");
      params.put("error", error);
      params.put("isAdmin", "false");
    }
    return params;
  }

  @RequestMapping(value = "user/rights", method = RequestMethod.GET)
  public List<Right> getAllRights(HttpServletRequest httpServletRequest) {
    String userName = loggedInUser(httpServletRequest);
    return roleRightService.getAllRightsForUser(userName);
  }

}
