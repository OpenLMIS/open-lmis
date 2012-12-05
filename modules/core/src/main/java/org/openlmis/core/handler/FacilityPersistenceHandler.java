package org.openlmis.core.handler;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.handler.AbstractModelPersistenceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("facilityPersistenceHandler")
@NoArgsConstructor
public class FacilityPersistenceHandler extends AbstractModelPersistenceHandler {

    private FacilityService facilityService;

    @Autowired
    public FacilityPersistenceHandler(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @Override
    protected void save(Importable importable, String modifiedBy) {
        Facility facility = (Facility) importable;
        facility.setModifiedBy(modifiedBy);
        facilityService.saveOrUpdate(facility);
    }
}
