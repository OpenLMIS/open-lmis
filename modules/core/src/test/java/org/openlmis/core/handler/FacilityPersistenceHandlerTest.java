package org.openlmis.core.handler;

import org.junit.Test;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.upload.Importable;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;

public class FacilityPersistenceHandlerTest {

    @Test
    public void shouldSaveFacility(){
        FacilityService facilityService = mock(FacilityService.class);
        FacilityPersistenceHandler facilityPersistenceHandler = new FacilityPersistenceHandler(facilityService);
        Importable facility = make(a(defaultFacility));
        facilityPersistenceHandler.save(facility);
        verify(facilityService).save((Facility)facility);
    }
}
