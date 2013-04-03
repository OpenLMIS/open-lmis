/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.service.FacilityApprovedProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class FacilityApprovedProductPersistenceHandler extends AbstractModelPersistenceHandler {

  public static final String DUPLICATE_FACILITY_APPROVED_PRODUCT = "Duplicate facility approved product.";
  private FacilityApprovedProductService facilityApprovedProductService;

  @Autowired
  public FacilityApprovedProductPersistenceHandler(FacilityApprovedProductService facilityApprovedProductService) {
    super(DUPLICATE_FACILITY_APPROVED_PRODUCT);
    this.facilityApprovedProductService = facilityApprovedProductService;
  }

  @Override
  protected BaseModel getExisting(BaseModel record) {
    return facilityApprovedProductService.getFacilityApprovedProductByProgramProductAndFacilityTypeCode((FacilityApprovedProduct) record);
  }

  @Override
  protected void save(BaseModel record) {
    facilityApprovedProductService.save((FacilityApprovedProduct) record);
  }
}
