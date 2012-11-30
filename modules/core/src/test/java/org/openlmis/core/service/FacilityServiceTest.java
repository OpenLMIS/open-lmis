package org.openlmis.core.service;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.FacilityRepository;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;

public class FacilityServiceTest {
    @Mock
    FacilityRepository facilityRepository;

    FacilityService facilityService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        facilityService = new FacilityService(facilityRepository);
    }

    @Test
    public void shouldStoreFacility() throws Exception {
        Facility facility = make(a(defaultFacility));
        facilityService.save(facility);
        verify(facilityRepository).save(facility);
    }

    @Test
        public void shouldReturnEmptyListIfUserIsNotAssignedAFacility() {
        when(facilityRepository.getHomeFacility("abcd")).thenReturn(null);
        assertTrue(facilityService.getAllForUser("abcd").isEmpty());
    }
}
