/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.FacilityApprovedProductMapper;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.Boolean.*;

@Component
@NoArgsConstructor
public class FacilityApprovedProductRepository {

  public static final String FACILITY_APPROVED_PRODUCT_DUPLICATE = "facilityApprovedProduct.duplicate.found";
  public static final String FACILITY_TYPE_DOES_NOT_EXIST = "facilityType.invalid";

  private FacilityApprovedProductMapper facilityApprovedProductMapper;

  private FacilityMapper facilityMapper;

  private ProductMapper productMapper;

  @Autowired
  public FacilityApprovedProductRepository(FacilityApprovedProductMapper facilityApprovedProductMapper, FacilityMapper facilityMapper, ProductMapper productMapper) {
    this.facilityApprovedProductMapper = facilityApprovedProductMapper;
    this.facilityMapper = facilityMapper;
    this.productMapper = productMapper;
  }

  public List<FacilityApprovedProduct> getFullSupplyProductsByFacilityAndProgram(Integer facilityId, Integer programId) {
    return facilityApprovedProductMapper.getProductsByFacilityProgramAndFullSupply(facilityId, programId, TRUE);
  }

  public List<FacilityApprovedProduct> getNonFullSupplyProductsByFacilityAndProgram(Integer facilityId, Integer programId) {
    return facilityApprovedProductMapper.getProductsByFacilityProgramAndFullSupply(facilityId, programId, FALSE);
  }

  public void insert(FacilityApprovedProduct facilityApprovedProduct) {
    FacilityApprovedProduct savedFacilityApprovedProduct = facilityApprovedProductMapper.getFacilityApprovedProductIdByProgramProductAndFacilityTypeCode(
      facilityApprovedProduct.getProgramProduct().getId(), facilityApprovedProduct.getFacilityType().getCode());

    if (savedFacilityApprovedProduct != null && facilityApprovedProduct.getModifiedDate().equals(savedFacilityApprovedProduct.getModifiedDate())) {
      throw new DataException(FACILITY_APPROVED_PRODUCT_DUPLICATE);
    }
    try {
      if (savedFacilityApprovedProduct == null) {
        facilityApprovedProductMapper.insert(facilityApprovedProduct);
      } else {
        facilityApprovedProduct.setId(savedFacilityApprovedProduct.getId());
        facilityApprovedProduct.getFacilityType().setId(facilityMapper.getFacilityTypeIdForCode(facilityApprovedProduct.getFacilityType().getCode()));
        facilityApprovedProductMapper.updateFacilityApprovedProduct(facilityApprovedProduct);
      }
    } catch (DataIntegrityViolationException e) {
      throw new DataException(FACILITY_TYPE_DOES_NOT_EXIST);
    }
  }

}
