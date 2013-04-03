/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.service.FacilityService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("facilityPersistenceHandler")
@NoArgsConstructor
public class FacilityPersistenceHandler extends AbstractModelPersistenceHandler {

  public static final String DUPLICATE_FACILITY_CODE = "Duplicate Facility Code";
  private FacilityService facilityService;

  @Autowired
  public FacilityPersistenceHandler(FacilityService facilityService) {
    super(DUPLICATE_FACILITY_CODE);
    this.facilityService = facilityService;
  }

  @Override
  protected Importable getExisting(Importable importable) {
    Facility facility = (Facility) importable;
    return facilityService.getByCode(facility);
  }

  @Override
  protected void save(Importable existingRecord, Importable currentRecord, AuditFields auditFields) {
    Facility facility = (Facility) currentRecord;
    facility.setModifiedBy(auditFields.getUser());
    facility.setModifiedDate(auditFields.getCurrentTimestamp());
    if (existingRecord != null) facility.setId(((Facility)existingRecord).getId());
    facilityService.save(facility);
  }

}
