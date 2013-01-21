package org.openlmis.core.upload;

import org.openlmis.core.domain.ProgramProductCost;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.upload.Importable;

public class ProgramProductCostPersistenceHandler extends AbstractModelPersistenceHandler {
  private ProgramProductService programProductService;

  public ProgramProductCostPersistenceHandler(ProgramProductService service) {

    this.programProductService = service;
  }

  @Override
  protected void save(Importable modelClass, String modifiedBy) {
    ProgramProductCost programProductCost = (ProgramProductCost) modelClass;
    programProductCost.setModifiedBy(modifiedBy);
    programProductService.save(programProductCost);
  }
}
