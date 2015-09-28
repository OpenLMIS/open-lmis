package org.openlmis.restapi.domain;


import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.User;
import org.openlmis.db.categories.UnitTests;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class LoginInformationTest {

    @Mock
    private User user;

    @Mock
    private Facility facility;

    @Test
    public void shouldSetLoginInformationFields() {
        when(user.getUserName()).thenReturn("username");
        when(user.getFirstName()).thenReturn("first");
        when(user.getLastName()).thenReturn("last");
        when(facility.getCode()).thenReturn("code");
        when(facility.getName()).thenReturn("name");

        LoginInformation loginInformation = LoginInformation.prepareForREST(user, facility);
        assertEquals("username", loginInformation.getUserName());
        assertEquals("first", loginInformation.getUserFirstName());
        assertEquals("last", loginInformation.getUserLastName());
        assertEquals("code", loginInformation.getFacilityCode());
        assertEquals("name", loginInformation.getFacilityName());
    }

    @Test
    public void shouldSetFacilityFieldsToNullIfUserIsNotAssociatedWithFacility() {
        when(user.getUserName()).thenReturn("username");
        when(user.getFirstName()).thenReturn("first");
        when(user.getLastName()).thenReturn("last");

        LoginInformation loginInformation = LoginInformation.prepareForREST(user, null);
        assertEquals("username", loginInformation.getUserName());
        assertEquals("first", loginInformation.getUserFirstName());
        assertEquals("last", loginInformation.getUserLastName());
        assertEquals(null, loginInformation.getFacilityCode());
        assertEquals(null, loginInformation.getFacilityName());
    }
}

