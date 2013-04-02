/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.GeographicZoneService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("geographicZonePersistenceHandler")
public class GeographicZonePersistenceHandler extends AbstractModelPersistenceHandler {
  GeographicZoneService geographicZoneService;

  @Autowired
  public GeographicZonePersistenceHandler(GeographicZoneService geographicZoneService) {
    this.geographicZoneService = geographicZoneService;
  }


  @Override
  protected Importable getExisting(Importable importable) {
    return geographicZoneService.getByCode((GeographicZone)importable);
  }

  @Override
  protected void save(Importable existingRecord, Importable currentRecord, AuditFields auditFields) {
    GeographicZone geographicZone = (GeographicZone) currentRecord;
    geographicZone.setModifiedBy(auditFields.getUser());
    geographicZone.setModifiedDate(auditFields.getCurrentTimestamp());
    if(existingRecord != null) geographicZone.setId(((GeographicZone)existingRecord).getId());
    geographicZoneService.save(geographicZone);
  }

  @Override
  protected void throwExceptionIfAlreadyProcessedInCurrentUpload(Importable importable, AuditFields auditFields) {
    GeographicZone savedZone = (GeographicZone) importable;
    if (savedZone != null && savedZone.getModifiedDate().equals(auditFields.getCurrentTimestamp()))
      throw new DataException("Duplicate Geographic Zone Code");
  }

}