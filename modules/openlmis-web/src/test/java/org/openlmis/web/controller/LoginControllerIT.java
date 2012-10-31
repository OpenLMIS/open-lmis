package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneSetup;

@ContextConfiguration(locations = "classpath*:applicationTestContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class LoginControllerIT {


    @Autowired
    private LoginController loginController;

    @Test
    public void shouldLoadLoginPage() throws Exception {
        standaloneSetup(loginController).build()
                .perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnAdminHomeUrlOnAdminLogin() {
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Authentication authentication = new AuthenticationStub();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        assertThat(loginController.homeDefault(), is(equalTo("redirect:/admin/home")));
    }



    private class AuthenticationStub implements Authentication {

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            GrantedAuthority grantedAuthority = new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return "ADMIN";
                }
            };

            Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
            grantedAuthorities.add(grantedAuthority);
            return grantedAuthorities;
        }

        @Override
        public Object getCredentials() {
            return null;
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
            return true;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

        @Override
        public String toString() {
            return null;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String getName() {
            return null;
        }
    }
}
