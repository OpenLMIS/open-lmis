package org.openlmis.restapi.service;

import com.natpryce.makeiteasy.MakeItEasy;
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
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.builder.ProgramSupportedBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramSupportedService;
import org.openlmis.core.service.UserService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.FacilitySupportedProgram;
import org.openlmis.restapi.domain.LoginInformation;
import org.openlmis.restapi.response.RestResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
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
    @Mock
    private ProgramSupportedService programSupportedService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
        List<ProgramSupported> programsSupportedByFacility = asList(make(a(ProgramSupportedBuilder.defaultProgramSupported)));
        when(programSupportedService.getActiveByFacilityId(123L)).thenReturn(programsSupportedByFacility);

        LoginInformation loginInformation = restLoginService.login("username", "password");
        assertEquals("username", loginInformation.getUserName());
        assertEquals("health facility", loginInformation.getFacilityName());
        assertEquals("123", loginInformation.getFacilityCode());
        assertEquals("Charles", loginInformation.getUserFirstName());
        assertEquals("Xavier", loginInformation.getUserLastName());
        assertEquals("P_CD", loginInformation.getFacilitySupportedPrograms().get(0));
    }

    @Test
    public void shouldThrowErrorIfUserHasNoFacility() {
        User user = new User();
        user.setUserName("username");
        user.setFirstName("Charles");
        user.setLastName("Xavier");

        expectedException.expect(DataException.class);
        when(userAuthenticationService.authenticateUser(any(User.class))).thenReturn(new UserToken("username", 1L, true));
        when(userService.getByUserName("username")).thenReturn(user);
        restLoginService.login("username", "password");
    }

    @Test
    public void shouldThrowExceptionIfUsernameAndPasswordAreIncorrect() {
        when(userAuthenticationService.authenticateUser(any(User.class))).thenReturn(new UserToken("username", null, false));
        exception.expect(BadCredentialsException.class);
        restLoginService.login("username", "password");
    }

    @Test
    public void shouldReturnFacilitySupportedProgramInformation() {
        Program program1 = make(a(ProgramBuilder.defaultProgram, with(ProgramBuilder.programCode, "P1")));
        program1.setIsSupportEmergency(true);
        Program program2 = make(a(ProgramBuilder.defaultProgram, with(ProgramBuilder.programCode, "P2")));
        program2.setParent(program1);
        program2.setIsSupportEmergency(false);
        ProgramSupported programSupported1 = make(a(ProgramSupportedBuilder.defaultProgramSupported, with(ProgramSupportedBuilder.supportedProgram, program1)));
        ProgramSupported programSupported2 = make(a(ProgramSupportedBuilder.defaultProgramSupported, with(ProgramSupportedBuilder.supportedProgram, program2)));
        List<ProgramSupported> programSupportedList = asList(programSupported1, programSupported2);
        when(programSupportedService.getActiveByFacilityId(1L)).thenReturn(programSupportedList);

        List<FacilitySupportedProgram> facilitySupportedPrograms = restLoginService.getFacilitySupportedPrograms(1L);
        assertEquals(2, facilitySupportedPrograms.size());
        assertEquals("P1", facilitySupportedPrograms.get(0).getProgramCode());
        assertEquals(null, facilitySupportedPrograms.get(0).getParentCode());
        assertEquals(true, facilitySupportedPrograms.get(0).getIsSupportEmergency());
        assertEquals("P2", facilitySupportedPrograms.get(1).getProgramCode());
        assertEquals("P1", facilitySupportedPrograms.get(1).getParentCode());
        assertEquals(false, facilitySupportedPrograms.get(1).getIsSupportEmergency());
    }
}