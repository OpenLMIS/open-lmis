package org.openlmis.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Controller
public class LoginController {

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login() {
        return "auth/login";
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String homeDefault() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        Collection<? extends GrantedAuthority> grantedAuthorities = auth.getAuthorities();
        String role = getUserRole(grantedAuthorities);

        return homePageUrl(role);
    }

    private String homePageUrl(String role) {
        return ("ADMIN".equals(role))?("redirect:/admin/home"):("redirect:/home");
    }

    private String getUserRole(Collection<? extends GrantedAuthority> grantedAuthorities) {
        if(grantedAuthorities == null || grantedAuthorities.size() == 0) return null;
        return grantedAuthorities.iterator().next().getAuthority();
    }
}
