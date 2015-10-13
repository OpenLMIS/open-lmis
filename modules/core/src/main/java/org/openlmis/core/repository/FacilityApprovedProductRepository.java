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

import org.openlmis.core.domain.FacilityTypeApprovedProduct;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.repository.mapper.FacilityApprovedProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FacilityApprovedProductRepository is repository class for FacilityApprovedProduct related database operations.
 */

@Component
public class FacilityApprovedProductRepository {

  @Autowired
  private FacilityApprovedProductMapper mapper;

  public List<FacilityTypeApprovedProduct> getFullSupplyProductsByFacilityAndProgram(Long facilityId, Long programId) {
    return mapper.getFullSupplyProductsBy(facilityId, programId);
  }

  public List<FacilityTypeApprovedProduct> getNonFullSupplyProductsByFacilityAndProgram(Long facilityId, Long programId) {
    return mapper.getNonFullSupplyProductsBy(facilityId, programId);
  }

  public void insert(FacilityTypeApprovedProduct facilityTypeApprovedProduct) {
    mapper.insert(facilityTypeApprovedProduct);
  }

  public void update(FacilityTypeApprovedProduct facilityTypeApprovedProduct) {
    mapper.update(facilityTypeApprovedProduct);
  }

  public FacilityTypeApprovedProduct getFacilityApprovedProductByProgramProductAndFacilityTypeCode(
    FacilityTypeApprovedProduct facilityTypeApprovedProduct) {
    return mapper.getBy(facilityTypeApprovedProduct.getProgramProduct().getId(), facilityTypeApprovedProduct.getFacilityType().getCode());
  }

  public List<FacilityTypeApprovedProduct> getAllBy(Long facilityTypeId, Long programId, String searchParam, Pagination pagination) {
    return mapper.getAllBy(facilityTypeId, programId, searchParam, pagination);
  }

  public List<FacilityTypeApprovedProduct> getAllByFacilityAndProgramId( Long facilityId, Long programId)
  {
    return mapper.getAllByFacilityAndProgramId(facilityId, programId);
  }

  public Integer getTotalSearchResultCount(Long facilityTypeId, Long programId, String searchParam) {
    return mapper.getTotalSearchResultCount(facilityTypeId, programId, searchParam);
  }

  public void delete(Long id) {
    mapper.delete(id);
  }

  public FacilityTypeApprovedProduct get(Long id){
    return mapper.get(id);
  }
}
