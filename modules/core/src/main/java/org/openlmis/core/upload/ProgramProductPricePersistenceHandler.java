/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("programProductPricePersistenceHandler")
public class ProgramProductPricePersistenceHandler extends AbstractModelPersistenceHandler {

  private ProgramProductService programProductService;
  @Autowired
  public ProgramProductPricePersistenceHandler(ProgramProductService service) {
    super(null);
    this.programProductService = service;
  }


  @Override
  protected Importable getExisting(Importable importable) {
    return null;
  }

  @Override
  protected void save(Importable existingRecord, Importable currentRecord, AuditFields auditFields) {
    ProgramProductPrice programProductPrice = (ProgramProductPrice) currentRecord;
    programProductPrice.setModifiedBy(auditFields.getUser());
    programProductPrice.setModifiedDate(auditFields.getCurrentTimestamp());
    programProductService.updateProgramProductPrice(programProductPrice);
  }

}
