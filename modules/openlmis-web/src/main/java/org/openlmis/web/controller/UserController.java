package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.service.RoleRightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;


@Controller
@NoArgsConstructor
public class UserController extends BaseController {

  @Autowired
  private RoleRightsService roleRightService;

  public UserController(RoleRightsService roleRightService) {
    this.roleRightService = roleRightService;
  }

  @RequestMapping(value = "/user", method = RequestMethod.GET)
  public HashMap<String, Object> user(HttpServletRequest httpServletRequest, @RequestParam(required = false) String error) {
    String userName = (String) httpServletRequest.getSession().getAttribute(UserAuthenticationSuccessHandler.USER);
    HashMap<String, Object> params = new HashMap<>();
    if (userName != null) {
      params.put("name", userName);
      params.put("authenticated", "true");
      params.put("rights",roleRightService.getAllRightsForUser(userName));
    } else {
      params.put("authenticated", "false");
      params.put("error", error);
    }
    return params;
  }
}
