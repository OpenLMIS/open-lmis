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
import org.openlmis.core.domain.FacilityFtpDetails;
import org.openlmis.core.service.FacilityFtpDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * FacilityFtpDetailsPersistenceHandler is used for uploads of FacilityFtpDetails. It uploads each FacilityFtpDetails
 * record by record.
 */
@Component
@NoArgsConstructor
public class FacilityFtpDetailsPersistenceHandler extends AbstractModelPersistenceHandler {

  @Autowired
  private FacilityFtpDetailsService facilityFtpDetailsService;

  @Override
  protected BaseModel getExisting(BaseModel record) {
    FacilityFtpDetails facilityFtpDetails = (FacilityFtpDetails) record;
    return facilityFtpDetailsService.getByFacilityCode(facilityFtpDetails.getFacility());
  }

  @Override
  protected void save(BaseModel record) {
    facilityFtpDetailsService.save((FacilityFtpDetails) record);
  }

  @Override
  public String getMessageKey() {
    return "error.duplicate.facility.code";
  }


}
