package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;


@Controller
@NoArgsConstructor
public class UserController extends BaseController {

  @Autowired
  private RoleRightsService roleRightService;
  @Autowired
  private UserService userService;

  public UserController(RoleRightsService roleRightService, UserService userService) {
    this.roleRightService = roleRightService;
    this.userService = userService;
  }

  @RequestMapping(value = "/user", method = RequestMethod.GET)
  public HashMap<String, Object> user(HttpServletRequest httpServletRequest, @RequestParam(required = false) String error) {
    String userName = (String) httpServletRequest.getSession().getAttribute(USER);
    HashMap<String, Object> params = new HashMap<>();
    if (userName != null) {
      params.put("name", userName);
      params.put("authenticated", TRUE);
      params.put("rights", roleRightService.getRights(userName));
    } else {
      params.put("authenticated", FALSE);
      params.put("error", error);
    }
    return params;
  }

  @RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
  public void sendPasswordTokenEmail(@RequestBody User user) {
      userService.sendForgotPasswordEmail(user);
  }
}
