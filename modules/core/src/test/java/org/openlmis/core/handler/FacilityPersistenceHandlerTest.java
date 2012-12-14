package org.openlmis.core.handler;

import org.junit.Test;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.FacilityRepository;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FacilityPersistenceHandlerTest {

    @Test
    public void shouldSaveFacility(){
        FacilityRepository facilityRepository = mock(FacilityRepository.class);
        FacilityPersistenceHandler facilityPersistenceHandler = new FacilityPersistenceHandler(facilityRepository);
        Facility facility = make(a(FacilityBuilder.defaultFacility));
        facilityPersistenceHandler.save(facility, "user");
        assertThat(facility.getModifiedBy(), is("user"));
        verify(facilityRepository).save(facility);
    }

}
