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
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.domain.FacilityTypeApprovedProduct;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityApprovedProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Exposes the services for handling FacilityApprovedProduct entity.
 */

@Service
@NoArgsConstructor
public class FacilityApprovedProductService {

  public static final String FACILITY_TYPE_DOES_NOT_EXIST = "facilityType.invalid";
  public static final String FACILITY_APPROVED_PRODUCT_DOES_NOT_EXIST = "facility.approved.product.does.not.exist";

  @Autowired
  private FacilityApprovedProductRepository repository;

  @Autowired
  private ProgramService programService;

  @Autowired
  private ProductService productService;

  @Autowired
  private ProgramProductService programProductService;

  @Autowired
  private FacilityService facilityService;

  public List<FacilityTypeApprovedProduct> getFullSupplyFacilityApprovedProductByFacilityAndProgram(Long facilityId, Long programId) {
    return repository.getFullSupplyProductsByFacilityAndProgram(facilityId, programId);
  }

  public List<FacilityTypeApprovedProduct> getNonFullSupplyFacilityApprovedProductByFacilityAndProgram(Long facilityId, Long programId) {
    return repository.getNonFullSupplyProductsByFacilityAndProgram(facilityId, programId);
  }

  public void save(FacilityTypeApprovedProduct facilityTypeApprovedProduct) {
    fillProgramProductIds(facilityTypeApprovedProduct);
    FacilityType facilityType = facilityService.getFacilityTypeByCode(facilityTypeApprovedProduct.getFacilityType());
    if (facilityType == null) throw new DataException(FACILITY_TYPE_DOES_NOT_EXIST);

    facilityTypeApprovedProduct.getFacilityType().setId(facilityType.getId());

    if (facilityTypeApprovedProduct.getId() != null) {
      if(repository.get(facilityTypeApprovedProduct.getId()) == null){
        throw new DataException(FACILITY_APPROVED_PRODUCT_DOES_NOT_EXIST);
      }
      repository.update(facilityTypeApprovedProduct);
    } else {
      repository.insert(facilityTypeApprovedProduct);
    }
  }

  public FacilityTypeApprovedProduct getFacilityApprovedProductByProgramProductAndFacilityTypeCode(FacilityTypeApprovedProduct facilityTypeApprovedProduct) {
    fillProgramProductIds(facilityTypeApprovedProduct);
    return repository.getFacilityApprovedProductByProgramProductAndFacilityTypeCode(facilityTypeApprovedProduct);
  }

  public List<FacilityTypeApprovedProduct> getAllBy(Long facilityTypeId, Long programId, String searchParam, Pagination pagination) {
    return repository.getAllBy(facilityTypeId, programId, searchParam, pagination);
  }

  public Integer getTotalSearchResultCount(Long facilityTypeId, Long programId, String searchParam) {
    return repository.getTotalSearchResultCount(facilityTypeId, programId, searchParam);
  }

  private void fillProgramProductIds(FacilityTypeApprovedProduct facilityTypeApprovedProduct) {
    Long programId = programService.getIdForCode(facilityTypeApprovedProduct.getProgramProduct().getProgram().getCode());
    Long productId = productService.getIdForCode(facilityTypeApprovedProduct.getProgramProduct().getProduct().getCode());
    Long programProductId = programProductService.getIdByProgramIdAndProductId(programId, productId);
    facilityTypeApprovedProduct.getProgramProduct().getProgram().setId(programId);
    facilityTypeApprovedProduct.getProgramProduct().getProduct().setId(productId);
    facilityTypeApprovedProduct.getProgramProduct().setId(programProductId);
  }

  public void saveAll(List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts, Long userId) {
    for (FacilityTypeApprovedProduct facilityTypeApprovedProduct : facilityTypeApprovedProducts) {
      facilityTypeApprovedProduct.setCreatedBy(userId);
      save(facilityTypeApprovedProduct);
    }
  }

  public void delete(Long id) {
    repository.delete(id);
  }
}
