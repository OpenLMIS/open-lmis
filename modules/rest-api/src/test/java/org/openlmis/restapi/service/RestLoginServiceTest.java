package org.openlmis.restapi.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.authentication.domain.UserToken;
import org.openlmis.authentication.service.UserAuthenticationService;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.UserService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.LoginInformation;
import org.openlmis.restapi.response.RestResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestResponse.class)
public class RestLoginServiceTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    @InjectMocks
    private RestLoginService restLoginService;
    @Mock
    private UserService userService;
    @Mock
    private UserAuthenticationService userAuthenticationService;
    @Mock
    private FacilityService facilityService;

    @Test
    public void shouldReturnLoginInformationIfUsernameAndPasswordAreCorrect() {
        User user = new User();
        user.setUserName("username");
        user.setFacilityId(123L);
        user.setFirstName("Charles");
        user.setLastName("Xavier");

        Facility facility = new Facility();
        facility.setCode("123");
        facility.setName("health facility");
        when(userAuthenticationService.authenticateUser(any(User.class))).thenReturn(new UserToken("username", 1L, true));
        when(userService.getByUserName("username")).thenReturn(user);
        when(facilityService.getById(123L)).thenReturn(facility);

        LoginInformation loginInformation = restLoginService.login("username", "password");
        assertEquals("username", loginInformation.getUserName());
        assertEquals("health facility", loginInformation.getFacilityName());
        assertEquals("123", loginInformation.getFacilityCode());
        assertEquals("Charles", loginInformation.getUserFirstName());
        assertEquals("Xavier", loginInformation.getUserLastName());
    }

    @Test
    public void shouldReturnLoginInformationIfUserHasNoFacility() {
        User user = new User();
        user.setUserName("username");
        user.setFirstName("Charles");
        user.setLastName("Xavier");

        when(userAuthenticationService.authenticateUser(any(User.class))).thenReturn(new UserToken("username", 1L, true));
        when(userService.getByUserName("username")).thenReturn(user);
        LoginInformation loginInformation = restLoginService.login("username", "password");
        assertEquals("username", loginInformation.getUserName());
        assertEquals(null, loginInformation.getFacilityName());
        assertEquals(null, loginInformation.getFacilityCode());
        assertEquals("Charles", loginInformation.getUserFirstName());
        assertEquals("Xavier", loginInformation.getUserLastName());
    }

    @Test
    public void shouldThrowExceptionIfUsernameAndPasswordAreIncorrect() {
        when(userAuthenticationService.authenticateUser(any(User.class))).thenReturn(new UserToken("username", null, false));
        exception.expect(BadCredentialsException.class);
        restLoginService.login("username", "password");
    }
}