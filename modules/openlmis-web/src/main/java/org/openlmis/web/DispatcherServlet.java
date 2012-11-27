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
