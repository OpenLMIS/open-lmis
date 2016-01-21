/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProgramProductIsaMapper;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
import org.openlmis.core.repository.mapper.ProgramProductPriceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ProgramProductRepository is Repository class for DeliveryZoneMember related database operations.
 */

@Component
@NoArgsConstructor
public class ProgramProductRepository {

  @Autowired
  private ProgramProductMapper mapper;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ProgramProductPriceMapper programProductPriceMapper;

  @Autowired
  private ProgramProductIsaMapper programProductIsaMapper;

  public void save(ProgramProduct programProduct) {
    Long programId = programRepository.getIdByCode(programProduct.getProgram().getCode());
    programProduct.getProgram().setId(programId);

    validateProductCode(programProduct.getProduct().getCode());

    Long productId = productRepository.getIdByCode(programProduct.getProduct().getCode());
    programProduct.getProduct().setId(productId);

    try {
      if (programProduct.getId() == null) {
        mapper.insert(programProduct);
      } else {
        mapper.update(programProduct);
      }
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException("error.duplicate.product.code.program.code");
    }
  }

  public Long getIdByProgramIdAndProductId(Long programId, Long productId) {
    Long programProductId = mapper.getIdByProgramAndProductId(programId, productId);

    if (programProductId == null)
      throw new DataException("programProduct.product.program.invalid");

    return programProductId;
  }

  private void validateProductCode(String code) {
    if (code == null || code.isEmpty() || productRepository.getIdByCode(code) == null) {
      throw new DataException("product.code.invalid");
    }
  }

  public void updateCurrentPrice(ProgramProduct programProduct) {
    mapper.updateCurrentPrice(programProduct);
  }

  public ProgramProduct getByProgramAndProductCode(ProgramProduct programProduct) {
    return getByProgramAndProductId(programRepository.getIdByCode(programProduct.getProgram().getCode()),
      productRepository.getIdByCode(programProduct.getProduct().getCode()));
  }

  public ProgramProduct getByProgramAndProductId(Long programId, Long productId) {
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

  public List<ProgramProduct> getByProgram(Program program) {
    return mapper.getByProgram(program);
  }

  public ProgramProduct getById(Long id) {
    return mapper.getById(id);
  }

  public List<ProgramProduct> getByProductCode(String code) {
    return mapper.getByProductCode(code);
  }

  public List<ProgramProduct> getProgramProductsBy(Long programId, String facilityTypeCode) {
    return mapper.getByProgramIdAndFacilityTypeCode(programId, facilityTypeCode);
  }

  public List<ProgramProduct> getNonFullSupplyProductsForProgram(Program program) {
    return mapper.getNonFullSupplyProductsForProgram(program);
  }

  public List<ProgramProduct> searchByProgram(String searchParam, Pagination pagination) {
    return mapper.searchByProgram(searchParam, pagination);
  }

  public Integer getTotalSearchResultCount(String searchParam) {
    return mapper.getTotalSearchResultCount(searchParam);
  }

  public List<ProgramProduct> searchByProduct(String searchParam, Pagination pagination) {
    return mapper.searchByProduct(searchParam, pagination);
  }

  public List<ProgramProduct> getActiveByProgram(Long programId) {
    return mapper.getActiveByProgram(programId);
  }

  @Transactional
  public void insertISA(ProgramProductISA programProductISA)
  {
    programProductIsaMapper.insert(programProductISA);
  }

  public void updateISA(ProgramProductISA programProductISA) {
    programProductIsaMapper.update(programProductISA);
  }
}
