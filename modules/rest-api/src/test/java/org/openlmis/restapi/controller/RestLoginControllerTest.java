/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.restapi.controller;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.LoginInformation;
import org.openlmis.restapi.domain.RestLoginRequest;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestLoginService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestResponse.class)
public class RestLoginControllerTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    @InjectMocks
    private RestLoginController restLoginController;
    @Mock
    private RestLoginService restLoginService;

    @Test
    public void shouldReturnUnauthorizedIfNotAuthenticated() {
        when(restLoginService.login("username", "pass")).thenThrow(new BadCredentialsException("error"));
        RestLoginRequest request = new RestLoginRequest();
        request.setUsername("username");
        request.setPassword("pass");
        ResponseEntity<RestResponse> response = restLoginController.login(request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void shouldReturnStatusOKAndObjectContainingLoginInformation() {
        LoginInformation loggedInUser = new LoginInformation();
        when(restLoginService.login("username", "pass")).thenReturn(loggedInUser);
        RestLoginRequest request = new RestLoginRequest();
        request.setUsername("username");
        request.setPassword("pass");
        ResponseEntity<RestResponse> response = restLoginController.login(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(loggedInUser, response.getBody().getData().get("userInformation"));

    }
}