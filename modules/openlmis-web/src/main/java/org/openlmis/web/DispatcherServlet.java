/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web;

import org.openlmis.LmisThreadLocal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;

public class DispatcherServlet extends org.springframework.web.servlet.DispatcherServlet{

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object userName;
        if((userName = request.getSession().getAttribute(USER)) != null) {
            LmisThreadLocal.set(userName.toString());
        }
        super.doService(request, response);
    }
}
