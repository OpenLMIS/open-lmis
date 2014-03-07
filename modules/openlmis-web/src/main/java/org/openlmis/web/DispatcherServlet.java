/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web;

import org.openlmis.LmisThreadLocal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;

/**
 * This class is the central dispatcher for HTTP request handlers/controllers.
 */
public class DispatcherServlet extends org.springframework.web.servlet.DispatcherServlet {

  @Override
  protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Object userName;
    if ((userName = request.getSession().getAttribute(USER)) != null) {
      LmisThreadLocal.set(userName.toString());
    }
    super.doService(request, response);
  }
}
