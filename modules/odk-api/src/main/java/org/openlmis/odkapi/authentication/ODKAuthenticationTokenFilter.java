/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.odkapi.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ODKAuthenticationTokenFilter implements Filter {


    @Override
    public void init(FilterConfig fc) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain fc) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null && context.getAuthentication().isAuthenticated()) {
            // do nothing
        } else {
            Map<String,String[]> params = req.getParameterMap();
            if (!params.isEmpty() && params.containsKey("deviceID")) {
                String token = params.get("deviceID")[0];
                System.out.println("++++++++++++++" + token);
                if (token != null) {
                    Authentication auth = new TokenAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }

        fc.doFilter(req, res);
    }

    @Override
    public void destroy() {

    }

    class TokenAuthentication implements Authentication {
        private String token;
        private TokenAuthentication(String token) {
            this.token = token;
        }
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return new ArrayList<GrantedAuthority>(0);
        }
        @Override
        public Object getCredentials() {
            return token;
        }
        @Override
        public Object getDetails() {
            return null;
        }
        @Override
        public Object getPrincipal() {
            return null;
        }
        @Override
        public boolean isAuthenticated() {
            return false;
        }
        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        }
        @Override
        public String getName() {
            // your custom logic here

            return "ODK";
        }
    }

}