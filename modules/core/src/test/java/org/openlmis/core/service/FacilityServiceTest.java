package org.openlmis.core.service;


import org.junit.Test;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.FacilityRepository;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;

public class FacilityServiceTest {

    @Test
    public void shouldStoreFacility() throws Exception {
        FacilityRepository facilityRepository = mock(FacilityRepository.class);
        FacilityService service = new FacilityService(facilityRepository);
        Facility facility = make(a(defaultFacility));
        service.save(facility);
        verify(facilityRepository).save(facility);
    }
}
