/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityRepository;
import org.openlmis.core.repository.ProgramProductRepository;
import org.openlmis.core.repository.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.trimToNull;

/**
 * Exposes the services for handling ProgramProduct entity.
 */

@Component
@NoArgsConstructor
public class ProgramProductService {

  @Autowired
  private ProgramProductRepository programProductRepository;

  @Autowired
  private ProgramService programService;

  @Autowired
  private ProductService productService;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private FacilityRepository facilityRepository;

  @Autowired
  private ProductCategoryService categoryService;

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
    validateAndSetProductCategory(programProduct);
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

  public List<ProgramProduct> getProgramProductsBy(String programCode, String facilityTypeCode) {
    FacilityType facilityType = new FacilityType();
    if ((facilityTypeCode = trimToNull(facilityTypeCode)) != null) {
      facilityType = facilityRepository.getFacilityTypeByCode(new FacilityType(facilityTypeCode));
    }
    return programProductRepository.getProgramProductsBy(programRepository.getIdByCode(trimToEmpty(programCode)), facilityType.getCode());
  }

  public List<ProgramProduct> getNonFullSupplyProductsForProgram(Program program) {
    return programProductRepository.getNonFullSupplyProductsForProgram(program);
  }

  private void validateAndSetProductCategory(ProgramProduct programProduct) {
    ProductCategory category = programProduct.getProductCategory();
    if (category == null) return;
    String categoryCode = category.getCode();
    if (categoryCode == null || categoryCode.isEmpty()) return;
    Long categoryId = categoryService.getProductCategoryIdByCode(category.getCode());
    if (categoryId == null) {
      throw new DataException("error.reference.data.invalid.product");
    }
    category.setId(categoryId);
  }

  public ProgramProduct getByProgramAndProductId(Long programId, Long productId) {
    return programProductRepository.getByProgramAndProductId(programId, productId);
  }
}
