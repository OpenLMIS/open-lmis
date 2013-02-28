package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.upload.Importable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("facilityPersistenceHandler")
@NoArgsConstructor
public class FacilityPersistenceHandler extends AbstractModelPersistenceHandler {

    private FacilityRepository facilityRepository;

    @Autowired
    public FacilityPersistenceHandler(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    @Override
    protected void save(Importable importable, Integer modifiedBy) {
        Facility facility = (Facility) importable;
        facility.setModifiedBy(modifiedBy);
        facilityRepository.save(facility);
    }
}
