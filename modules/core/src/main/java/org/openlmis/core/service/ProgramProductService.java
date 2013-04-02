/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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

  public void updateProgramProductPrice(ProgramProductPrice programProductPrice) {
    programProductPrice.validate();

    ProgramProduct programProduct = programProductPrice.getProgramProduct();
    ProgramProduct programProductWithId = programProductRepository.getProgramProductByProgramAndProductCode(programProduct);
    programProduct.setId(programProductWithId.getId());
    programProduct.setModifiedBy(programProductPrice.getModifiedBy());
    programProduct.setModifiedDate(programProductPrice.getModifiedDate());

    programProductRepository.updateCurrentPrice(programProduct);
    programProductRepository.updatePriceHistory(programProductPrice);
  }

  public void save(ProgramProduct programProduct) {
    programProductRepository.save(programProduct);
  }

  public ProgramProduct getProgramProductByProgramAndProductCode(ProgramProduct programProduct) {
    return programProductRepository.getProgramProductByProgramAndProductCode(programProduct);
  }
}
