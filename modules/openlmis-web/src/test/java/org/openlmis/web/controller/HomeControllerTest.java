package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.web.authentication.UserAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class HomeControllerTest {

    @Mock
    HttpServletRequest request;
    @Mock
    HttpSession session;

    HomeController homeController;

    @Before
    public void setUp() {
        initMocks(this);
        homeController = new HomeController();
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void shouldReturnAdminHomeUrlOnAdminLogin() {
        when(session.getAttribute(UserAuthenticationSuccessHandler.IS_ADMIN)).thenReturn(true);
        String homePageURl = homeController.homeDefault(request);
        assertEquals("redirect:/resources/pages/admin/index.html", homePageURl);
    }

    @Test
    public void shouldRedirectToIndexPageForUsersWhoAreNotAdmins() {
        when(session.getAttribute(UserAuthenticationSuccessHandler.IS_ADMIN)).thenReturn(false);
        String homePageURl = homeController.homeDefault(request);
        assertEquals("redirect:/resources/pages/logistics/index.html", homePageURl);
    }

}
