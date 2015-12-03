/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FacilityPersistenceHandler is used for uploads of Facility. It uploads each Facility record by record.
 */
@Component
@NoArgsConstructor
public class FacilityPersistenceHandler extends AbstractModelPersistenceHandler {

  private FacilityService facilityService;

  @Autowired
  public FacilityPersistenceHandler(FacilityService facilityService) {
    this.facilityService = facilityService;
  }

  @Override
  protected BaseModel getExisting(BaseModel record) {
    return facilityService.getByCode((Facility) record);
  }

  @Override
  protected void save(BaseModel record) {
    facilityService.save((Facility) record);
  }

  @Override
  public void postProcess(AuditFields auditFields) {
    List<Facility> facilities = facilityService.getAllParentsByModifiedDate(auditFields.getCurrentTimestamp());
    for (Facility facility : facilities) {
      facilityService.updateAndNotifyForVirtualFacilities(facility);
    }
  }

  @Override
  public String getMessageKey() {
    return "error.duplicate.facility.code";
  }

}
