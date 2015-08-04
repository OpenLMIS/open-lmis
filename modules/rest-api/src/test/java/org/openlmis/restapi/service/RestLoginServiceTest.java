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
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.UserService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.LoginInformation;
import org.openlmis.restapi.response.RestResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestResponse.class)
public class RestLoginServiceTest {

  @InjectMocks
  private RestLoginService restLoginService;

  @Mock
  private UserService userService;

  @Mock
  private UserAuthenticationService userAuthenticationService;

  @Mock
  private FacilityService facilityService;

  @Mock
  private ProgramMapper programMapper;

  @Rule
  public ExpectedException exception = ExpectedException.none();

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
    List<Program> programsByFacility = new ArrayList();
    when(programMapper.getActiveByFacility(123L)).thenReturn(programsByFacility);

    LoginInformation loginInformation = restLoginService.login("username", "password");
    assertEquals("username", loginInformation.getUserName());
    assertEquals("health facility", loginInformation.getFacilityName());
    assertEquals("123", loginInformation.getFacilityCode());
    assertEquals("Charles", loginInformation.getUserFirstName());
    assertEquals("Xavier", loginInformation.getUserLastName());
  }

  @Test
  public void shouldReturnProgramInformation(){
    User user = new User();
    user.setUserName("username");
    user.setFacilityId(123L);
    user.setFirstName("Charles");
    user.setLastName("Xavier");
    when(userAuthenticationService.authenticateUser(any(User.class))).thenReturn(new UserToken("username", 1L, true));
    when(userService.getByUserName("username")).thenReturn(user);

    when(facilityService.getById(123L)).thenReturn(new Facility());

    List<Program> programsByFacility = new ArrayList();
    Program p1 = new Program();
    p1.setCode("1A");
    p1.setName("Program 1");
    programsByFacility.add(p1);
    Program p2 = new Program();
    p2.setCode("2X");
    p2.setName("Program 2");
    programsByFacility.add(p2);
    when(programMapper.getActiveByFacility(123L)).thenReturn(programsByFacility);

    LoginInformation loginInformation = restLoginService.login("username", "password");
    assertEquals(2, loginInformation.getPrograms().size());
    assertEquals("1A", loginInformation.getPrograms().get(0).getCode());
    assertEquals("Program 1", loginInformation.getPrograms().get(0).getName());
    assertEquals("2X", loginInformation.getPrograms().get(1).getCode());
    assertEquals("Program 2", loginInformation.getPrograms().get(1).getName());
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