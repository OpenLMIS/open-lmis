/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.authentication.web;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class is responsible for responding with 401 response for ajax request made to webapp.
 */

public final class RestAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

  public RestAuthenticationEntryPoint(String loginFormUrl) {
    super(loginFormUrl);
  }

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
                       AuthenticationException authException) throws IOException, ServletException {
    if ("XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "unauthorized");
    } else {
      super.commence(request, response, authException);
    }
  }
}