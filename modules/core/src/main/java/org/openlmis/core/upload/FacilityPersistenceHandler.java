/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
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
    protected void save(Importable importable, AuditFields auditFields) {
        Facility facility = (Facility) importable;
        facility.setModifiedBy(auditFields.getUser());
        facility.setModifiedDate(auditFields.getCurrentTimestamp());
        facilityRepository.save(facility);
    }
}
