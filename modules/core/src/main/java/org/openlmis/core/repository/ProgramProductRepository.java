/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
import org.openlmis.core.repository.mapper.ProgramProductPriceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ProgramProductRepository {
  public static final String PROGRAM_PRODUCT_INVALID = "programProduct.product.program.invalid";

  private ProgramProductMapper mapper;
  private ProgramRepository programRepository;
  private ProductRepository productRepository;
  private ProgramProductPriceMapper programProductPriceMapper;


  @Autowired
  public ProgramProductRepository(ProgramRepository programRepository, ProgramProductMapper programProductMapper,
                                  ProductRepository productRepository, ProgramProductPriceMapper programProductPriceMapper) {
    this.mapper = programProductMapper;
    this.programRepository = programRepository;
    this.productRepository = productRepository;
    this.programProductPriceMapper = programProductPriceMapper;
  }


  public void save(ProgramProduct programProduct) {
    Integer programId = programRepository.getIdByCode(programProduct.getProgram().getCode());
    programProduct.getProgram().setId(programId);

    validateProductCode(programProduct.getProduct().getCode());

    Integer productId = productRepository.getIdByCode(programProduct.getProduct().getCode());
    programProduct.getProduct().setId(productId);

    try {
      if (programProduct.getId() == null) {
        mapper.insert(programProduct);
      } else {
        mapper.update(programProduct);
      }
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException("Duplicate entry for Product Code and Program Code combination found");
    }
  }

  public Integer getIdByProgramIdAndProductId(Integer programId, Integer productId) {
    Integer programProductId = mapper.getIdByProgramAndProductId(programId, productId);

    if (programProductId == null)
      throw new DataException(PROGRAM_PRODUCT_INVALID);

    return programProductId;
  }

  private void validateProductCode(String code) {
    if (code == null || code.isEmpty() || productRepository.getIdByCode(code) == null) {
      throw new DataException("Invalid Product Code");
    }
  }

  public void updateCurrentPrice(ProgramProduct programProduct) {
    mapper.updateCurrentPrice(programProduct);
  }

  public ProgramProduct getByProgramAndProductCode(ProgramProduct programProduct) {
    return getByProgramAndProductCode(programRepository.getIdByCode(programProduct.getProgram().getCode()),
        productRepository.getIdByCode(programProduct.getProduct().getCode()));
  }

  public ProgramProduct getByProgramAndProductCode(Integer programId, Integer productId) {
    return mapper.getByProgramAndProductId(programId, productId);
  }

  public void updatePriceHistory(ProgramProductPrice programProductPrice) {
    programProductPriceMapper.closeLastActivePrice(programProductPrice);
    programProductPriceMapper.insertNewCurrentPrice(programProductPrice);
  }

  public void updateProgramProduct(ProgramProduct programProduct) {
    mapper.update(programProduct);
  }

  public ProgramProductPrice getProgramProductPrice(ProgramProduct programProduct) {
    return programProductPriceMapper.get(programProduct);
  }
}
