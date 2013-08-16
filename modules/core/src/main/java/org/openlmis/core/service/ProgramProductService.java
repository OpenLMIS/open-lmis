/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProgramProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class ProgramProductService {

  @Autowired
  private ProgramProductRepository programProductRepository;

  @Autowired
  private ProgramService programService;

  @Autowired
  private ProductService productService;


  public Long getIdByProgramIdAndProductId(Long programId, Long productId) {
    return programProductRepository.getIdByProgramIdAndProductId(programId, productId);
  }

  public void updateProgramProductPrice(ProgramProductPrice programProductPrice) {
    programProductPrice.validate();

    ProgramProduct programProduct = programProductPrice.getProgramProduct();
    ProgramProduct programProductWithId = programProductRepository.getByProgramAndProductCode(programProduct);
    if (programProductWithId == null)
      throw new DataException("programProduct.product.program.invalid");

    programProduct.setId(programProductWithId.getId());
    programProduct.setModifiedBy(programProductPrice.getModifiedBy());
    programProduct.setModifiedDate(programProductPrice.getModifiedDate());

    programProductRepository.updateCurrentPrice(programProduct);
    programProductRepository.updatePriceHistory(programProductPrice);
  }

  public void save(ProgramProduct programProduct) {
    if (programProduct.getId() == null) {
      boolean globalProductStatus = productService.isActive(programProduct.getProduct().getCode());
      if (globalProductStatus && programProduct.isActive())
        programService.setFeedSendFlag(programProduct.getProgram(), true);
    } else {
      ProgramProduct existingProgramProduct = programProductRepository.getById(programProduct.getId());
      if (existingProgramProduct.getProduct().getActive() && (existingProgramProduct.isActive() != programProduct.isActive())) {
        programService.setFeedSendFlag(programProduct.getProgram(), true);
      }
    }
    programProductRepository.save(programProduct);
    // log the price change here.
    if(programProduct.getId() != null){
      ProgramProductPrice priceChange = new ProgramProductPrice();
      priceChange.setProgramProduct(programProduct);
      priceChange.setPricePerDosage(programProduct.getCurrentPrice());
      this.updateProgramProductPrice(priceChange);
    }

  }

  public ProgramProduct getByProgramAndProductCode(ProgramProduct programProduct) {
    return programProductRepository.getByProgramAndProductCode(programProduct);
  }

  public ProgramProductPrice getProgramProductPrice(ProgramProduct programProduct) {
    populateProgramProductIds(programProduct);
    return programProductRepository.getProgramProductPrice(programProduct);
  }

  private void populateProgramProductIds(ProgramProduct programProduct) {
    Long programId = programService.getIdForCode(programProduct.getProgram().getCode());
    Long productId = productService.getIdForCode(programProduct.getProduct().getCode());
    programProduct.setId(programProductRepository.getIdByProgramIdAndProductId(programId, productId));
  }

  public List<ProgramProduct> getByProgram(Program program) {
    return programProductRepository.getByProgram(program);
  }

  public List<ProgramProduct> getByProductCode(String productCode) {
    return programProductRepository.getByProductCode(productCode);
  }
}
