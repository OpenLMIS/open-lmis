/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.FacilityApprovedProduct;
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
  protected void save(Importable modelClass, AuditFields auditFields) {
    FacilityApprovedProduct facilityApprovedProduct = (FacilityApprovedProduct) modelClass;
    facilityApprovedProduct.setModifiedBy(auditFields.getUser());
    facilityApprovedProductService.save(facilityApprovedProduct);
  }
}
