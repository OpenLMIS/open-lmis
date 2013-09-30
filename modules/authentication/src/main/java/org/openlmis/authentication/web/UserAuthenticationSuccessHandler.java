/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.authentication.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;


public class UserAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  public static final String USER = "USER";
  public static final String USER_ID = "USER_ID";

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
    HttpSession session = request.getSession();
    session.setAttribute(USER_ID, authentication.getPrincipal());
    Map userDetails = (Map) authentication.getDetails();
    session.setAttribute(USER, userDetails.get(USER));

    super.onAuthenticationSuccess(request, response, authentication);
  }
}
