package org.openlmis.core.upload;

import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.upload.Importable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("programProductPricePersistenceHandler")
public class ProgramProductPricePersistenceHandler extends AbstractModelPersistenceHandler {

  private ProgramProductService programProductService;
  @Autowired
  public ProgramProductPricePersistenceHandler(ProgramProductService service) {

    this.programProductService = service;
  }

  @Override
  protected void save(Importable modelClass, Integer modifiedBy) {
    ProgramProductPrice programProductPrice = (ProgramProductPrice) modelClass;
    programProductPrice.setModifiedBy(modifiedBy);
    programProductService.save(programProductPrice);
  }
}
