package org.openlmis.core.service;


import org.junit.Test;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.FacilityRepository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FacilityServiceTest {

    @Test
    public void shouldStoreFacility() throws Exception {
        FacilityRepository facilityRepository = mock(FacilityRepository.class);
        FacilityService service = new FacilityService(facilityRepository);
        Facility facility = new Facility("code","name", 1, 1);
        service.save(facility);
        verify(facilityRepository).save(facility);
    }
}
