package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.repository.ProgramProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ProgramProductService {

  private ProgramProductRepository programProductRepository;

  @Autowired
  public ProgramProductService(ProgramProductRepository programProductRepository) {
    this.programProductRepository = programProductRepository;
  }

  public Integer getIdByProgramIdAndProductId(Integer programId, Integer productId){
    return programProductRepository.getIdByProgramIdAndProductId(programId, productId);
  }

  public void save(ProgramProductPrice programProductPrice) {
    ProgramProduct programProduct = programProductRepository.getProgramProductByProgramAndProductCode(programProductPrice.getProgramProduct());
    programProductPrice.getProgramProduct().setId(programProduct.getId());

    programProductRepository.updateCurrentPrice(programProductPrice.getProgramProduct());
    programProductRepository.updatePriceHistory(programProductPrice);
  }
}
