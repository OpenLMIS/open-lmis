/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("programProductPersistenceHandler")
public class ProgramProductPersistenceHandler extends AbstractModelPersistenceHandler {

  private ProgramProductService programProductService;

  @Autowired
  public ProgramProductPersistenceHandler(ProgramProductService programProductService) {
    this.programProductService = programProductService;
  }

  @Override
  protected Importable getExisting(Importable importable) {
    return programProductService.getProgramProductByProgramAndProductCode((ProgramProduct) importable);
  }

  @Override
  protected void save(Importable existingRecord, Importable currentRecord, AuditFields auditFields) {
    ProgramProduct programProduct = (ProgramProduct) currentRecord;
    programProduct.setModifiedBy(auditFields.getUser());
    programProduct.setModifiedDate(auditFields.getCurrentTimestamp());
    if(existingRecord != null) programProduct.setId(((ProgramProduct) existingRecord).getId());
    programProductService.save(programProduct);
  }

  @Override
  protected void throwExceptionIfAlreadyProcessedInCurrentUpload(Importable importable, AuditFields auditFields) {
    ProgramProduct programProduct = (ProgramProduct) importable;
    if (programProduct != null && programProduct.getModifiedDate().equals(auditFields.getCurrentTimestamp())) {
      throw new DataException("Duplicate entry for Product Code and Program Code combination found");
    }
  }

 }


