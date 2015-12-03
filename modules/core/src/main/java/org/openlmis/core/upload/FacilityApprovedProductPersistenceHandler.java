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
import org.openlmis.core.domain.FacilityTypeApprovedProduct;
import org.openlmis.core.service.FacilityApprovedProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * FacilityApprovedProductPersistenceHandler is used for uploads of FacilityTypeApprovedProduct.
 * It uploads each FacilityTypeApprovedProduct record by record.
 */
@Component
@NoArgsConstructor
public class FacilityApprovedProductPersistenceHandler extends AbstractModelPersistenceHandler {

  private FacilityApprovedProductService facilityApprovedProductService;

  @Autowired
  public FacilityApprovedProductPersistenceHandler(FacilityApprovedProductService facilityApprovedProductService) {
    this.facilityApprovedProductService = facilityApprovedProductService;
  }

  @Override
  protected BaseModel getExisting(BaseModel record) {
    return facilityApprovedProductService.getFacilityApprovedProductByProgramProductAndFacilityTypeCode((FacilityTypeApprovedProduct) record);
  }

  @Override
  protected void save(BaseModel record) {
    facilityApprovedProductService.save((FacilityTypeApprovedProduct) record);
  }

  @Override
  public String getMessageKey() {
    return "error.duplicate.facility.approved.product";
  }
}
