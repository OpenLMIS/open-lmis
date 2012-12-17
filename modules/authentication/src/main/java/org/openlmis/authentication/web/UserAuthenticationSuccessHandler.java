package org.openlmis.authentication.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class UserAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  public static final String USER = "USER";

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
    request.getSession().setAttribute(USER, authentication.getPrincipal());
    super.onAuthenticationSuccess(request, response, authentication);
  }
}
