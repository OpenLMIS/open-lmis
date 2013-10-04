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
import org.openlmis.core.domain.FacilityTypeApprovedProduct;
import org.openlmis.core.repository.mapper.FacilityApprovedProductMapper;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class FacilityApprovedProductRepository {

  private FacilityApprovedProductMapper facilityApprovedProductMapper;

  private FacilityMapper facilityMapper;

  private ProductMapper productMapper;

  @Autowired
  public FacilityApprovedProductRepository(FacilityApprovedProductMapper facilityApprovedProductMapper, FacilityMapper facilityMapper, ProductMapper productMapper) {
    this.facilityApprovedProductMapper = facilityApprovedProductMapper;
    this.facilityMapper = facilityMapper;
    this.productMapper = productMapper;
  }

  public List<FacilityTypeApprovedProduct> getFullSupplyProductsByFacilityAndProgram(Long facilityId, Long programId) {
    return facilityApprovedProductMapper.getFullSupplyProductsByFacilityAndProgram(facilityId, programId);
  }

  public List<FacilityTypeApprovedProduct> getNonFullSupplyProductsByFacilityAndProgram(Long facilityId, Long programId) {
    return facilityApprovedProductMapper.getNonFullSupplyProductsByFacilityAndProgram(facilityId, programId);
  }

  public List<FacilityTypeApprovedProduct> getProductsCompleteListByFacilityAndProgram(Long facilityId, Long programId) {
      return facilityApprovedProductMapper.getProductsCompleteListByFacilityAndProgram(facilityId, programId);
  }

  public List<FacilityTypeApprovedProduct> getProductsCompleteListByFacilityTypeAndProgram(Long facilityTypeId, Long programId) {
      return facilityApprovedProductMapper.getProductsCompleteListByFacilityTypeAndProgram(facilityTypeId, programId);
  }

  public List<FacilityTypeApprovedProduct> getProductsAlreadyApprovedListByFacilityTypeAndProgram(Long facilityTypeId, Long programId) {
      return facilityApprovedProductMapper.getProductsAlreadyApprovedListByFacilityTypeAndProgram(facilityTypeId, programId);
  }

  public FacilityTypeApprovedProduct getFacilityApprovedProductByProgramProductAndFacilityTypeId(Long facilityTypeId,Long programId,Long productId){
      return facilityApprovedProductMapper.getFacilityApprovedProductByProgramProductAndFacilityTypeId(facilityTypeId,programId,productId);
  }

  public void removeFacilityApprovedProductByProgramProductAndFacilityTypeId(Long facilityTypeId,Long programId,Long productId){
      facilityApprovedProductMapper.removeFacilityApprovedProductByProgramProductAndFacilityTypeId(facilityTypeId,programId,productId);
  }

  public void insert(FacilityTypeApprovedProduct facilityTypeApprovedProduct) {
    facilityApprovedProductMapper.insert(facilityTypeApprovedProduct);
  }

  public void update(FacilityTypeApprovedProduct facilityTypeApprovedProduct) {
    facilityApprovedProductMapper.updateFacilityApprovedProduct(facilityTypeApprovedProduct);
  }

  public FacilityTypeApprovedProduct getFacilityApprovedProductByProgramProductAndFacilityTypeCode(FacilityTypeApprovedProduct facilityTypeApprovedProduct) {
    return facilityApprovedProductMapper.getFacilityApprovedProductIdByProgramProductAndFacilityTypeCode(
        facilityTypeApprovedProduct.getProgramProduct().getId(), facilityTypeApprovedProduct.getFacilityType().getCode());
  }
}
