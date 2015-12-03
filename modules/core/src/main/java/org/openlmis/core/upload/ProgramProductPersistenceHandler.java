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

import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ProgramProductPersistenceHandler is used for uploads of ProgramProduct. It uploads each ProgramProduct
 * record by record.
 */
@Component
public class ProgramProductPersistenceHandler extends AbstractModelPersistenceHandler {

  @Autowired
  private ProgramProductService programProductService;

  @Autowired
  private ProgramService programService;

  @Override
  protected BaseModel getExisting(BaseModel record) {
    return programProductService.getByProgramAndProductCode((ProgramProduct) record);
  }

  @Override
  protected void save(BaseModel record) {
    programProductService.save((ProgramProduct) record);
  }

  @Override
  public void postProcess(AuditFields auditFields) {
    programService.notifyProgramChange();
  }

  @Override
  public String getMessageKey() {
    return "error.duplicate.program.product.combination";
  }

}


