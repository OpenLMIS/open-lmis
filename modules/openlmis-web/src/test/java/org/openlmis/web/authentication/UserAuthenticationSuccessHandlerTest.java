/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.authentication;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.db.categories.UnitTests;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
@Category(UnitTests.class)
public class UserAuthenticationSuccessHandlerTest {

    public static final String CONTEXT_PATH = "contextPath";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final Integer userId = 1;

    @Mock
    MockHttpServletRequest request;

    MockHttpServletResponse response;

    @Mock
    private HttpSession session;

    String SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";

    UserAuthenticationSuccessHandler userAuthenticationSuccessHandler;
    private Map userDetails;

    @Before
    public void setup() {
        initMocks(this);
        userAuthenticationSuccessHandler = new UserAuthenticationSuccessHandler();

        when(request.getSession()).thenReturn(session);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        userDetails = new HashMap();
        userDetails.put(USER, USERNAME);
        response = new MockHttpServletResponse();
    }

    @Test
    public void shouldRedirectUserToHome() throws IOException, ServletException {
        String defaultTargetUrl = "/";

        TestingAuthenticationToken authentication = new TestingAuthenticationToken(userId, "password", "USER");
        authentication.setDetails(userDetails);
        userAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        assertEquals(CONTEXT_PATH + defaultTargetUrl, response.getRedirectedUrl());
    }

    @Test
    public void shouldSaveUsernameInSession() throws IOException, ServletException {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(userId, "password", "USER");
        authentication.setDetails(userDetails);
        userAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(session).setAttribute(USER, USERNAME);
    }

    @Test
    public void shouldSaveUserIdInSession() throws IOException, ServletException {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(userId, "password", "USER");
        authentication.setDetails(userDetails);
        userAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(session).setAttribute(USER_ID, userId);
    }



}
