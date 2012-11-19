package org.openlmis.core.service;


import org.junit.Test;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.FacilityRepository;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FacilityServiceTest {

    @Test
    public void shouldStoreFacility() throws Exception {
        FacilityRepository facilityRepository = mock(FacilityRepository.class);
        FacilityService service = new FacilityService(facilityRepository);
        Facility facility = make(a(FacilityBuilder.facility));
        service.save(facility);
        verify(facilityRepository).save(facility);
    }

}
