package org.openlmis.core.handler;

import org.junit.Test;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FacilityPersistenceHandlerTest {

    @Test
    public void shouldSaveFacility(){
        FacilityService facilityService = mock(FacilityService.class);
        FacilityPersistenceHandler facilityPersistenceHandler = new FacilityPersistenceHandler(facilityService);
        Facility facility = make(a(FacilityBuilder.defaultFacility));
        facilityPersistenceHandler.save(facility, "user");
        assertThat(facility.getModifiedBy(), is("user"));
        verify(facilityService).saveOrUpdate(facility);
    }

}
