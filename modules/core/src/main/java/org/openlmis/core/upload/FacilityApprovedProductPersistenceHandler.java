/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityApprovedProductService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class FacilityApprovedProductPersistenceHandler extends AbstractModelPersistenceHandler {

  private FacilityApprovedProductService facilityApprovedProductService;

  @Autowired
  public FacilityApprovedProductPersistenceHandler(FacilityApprovedProductService facilityApprovedProductService) {
    this.facilityApprovedProductService = facilityApprovedProductService;
  }

  @Override
  protected Importable getExisting(Importable importable) {
    FacilityApprovedProduct facilityApprovedProduct = (FacilityApprovedProduct)importable;
    return facilityApprovedProductService.getFacilityApprovedProductByProgramProductAndFacilityTypeCode(facilityApprovedProduct);
  }

  @Override
  protected void save(Importable existingRecord, Importable currentRecord, AuditFields auditFields) {
    FacilityApprovedProduct facilityApprovedProduct = (FacilityApprovedProduct) currentRecord;
    FacilityApprovedProduct existingFacilityApprovedProduct = (FacilityApprovedProduct) existingRecord;
    if(existingFacilityApprovedProduct != null)
      facilityApprovedProduct.setId(existingFacilityApprovedProduct.getId());
    facilityApprovedProduct.setModifiedBy(auditFields.getUser());
    facilityApprovedProduct.setModifiedDate(auditFields.getCurrentTimestamp());
    facilityApprovedProductService.save(facilityApprovedProduct);
  }

  @Override
  protected void throwExceptionIfAlreadyProcessedInCurrentUpload(Importable importable, AuditFields auditFields) {
    FacilityApprovedProduct facilityApprovedProduct = (FacilityApprovedProduct)importable;
    if (facilityApprovedProduct.getModifiedDate().equals(auditFields.getCurrentTimestamp())) {
      throw new DataException("Duplicate facility approved product.");
    }
  }

}
