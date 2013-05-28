/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.repository.mapper.FacilityApprovedProductMapper;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class FacilityApprovedProductRepository {

  public static final String FACILITY_APPROVED_PRODUCT_DUPLICATE = "facilityApprovedProduct.duplicate.found";

  private FacilityApprovedProductMapper facilityApprovedProductMapper;

  private FacilityMapper facilityMapper;

  private ProductMapper productMapper;

  @Autowired
  public FacilityApprovedProductRepository(FacilityApprovedProductMapper facilityApprovedProductMapper, FacilityMapper facilityMapper, ProductMapper productMapper) {
    this.facilityApprovedProductMapper = facilityApprovedProductMapper;
    this.facilityMapper = facilityMapper;
    this.productMapper = productMapper;
  }

  public List<FacilityApprovedProduct> getFullSupplyProductsByFacilityAndProgram(Long facilityId, Long programId) {
    return facilityApprovedProductMapper.getFullSupplyProductsByFacilityAndProgram(facilityId, programId);
  }

  public List<FacilityApprovedProduct> getNonFullSupplyProductsByFacilityAndProgram(Long facilityId, Long programId) {
    return facilityApprovedProductMapper.getNonFullSupplyProductsByFacilityAndProgram(facilityId, programId);
  }

  public void insert(FacilityApprovedProduct facilityApprovedProduct) {
    facilityApprovedProductMapper.insert(facilityApprovedProduct);
  }

  public void update(FacilityApprovedProduct facilityApprovedProduct) {
      facilityApprovedProductMapper.updateFacilityApprovedProduct(facilityApprovedProduct);
  }

  public FacilityApprovedProduct getFacilityApprovedProductByProgramProductAndFacilityTypeCode(FacilityApprovedProduct facilityApprovedProduct) {
    return facilityApprovedProductMapper.getFacilityApprovedProductIdByProgramProductAndFacilityTypeCode(
      facilityApprovedProduct.getProgramProduct().getId(), facilityApprovedProduct.getFacilityType().getCode());
  }
}
