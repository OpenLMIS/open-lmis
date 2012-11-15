package org.openlmis.core.handler;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.upload.Importable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("facilityPersistenceHandler")
public class FacilityPersistenceHandler extends AbstractModelPersistenceHandler{

    private FacilityService facilityService;

    @Autowired
    public FacilityPersistenceHandler(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @Override
    protected void save(Importable importable) {
        facilityService.save((Facility)importable);
    }
}
