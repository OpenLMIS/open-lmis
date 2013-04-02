/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.repository.FacilityApprovedProductRepository;
import org.openlmis.upload.Importable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@NoArgsConstructor
public class FacilityApprovedProductService {

  private FacilityApprovedProductRepository repository;
  private ProgramService programService;
  private ProductService productService;
  private ProgramProductService programProductService;
  private FacilityService facilityService;

  @Autowired
  public FacilityApprovedProductService(FacilityApprovedProductRepository repository,
                                        ProgramService programService, ProductService productService,
                                        ProgramProductService programProductService, FacilityService facilityService) {
    this.repository = repository;
    this.programService = programService;
    this.productService = productService;
    this.programProductService = programProductService;
    this.facilityService = facilityService;
  }

  public List<FacilityApprovedProduct> getFullSupplyFacilityApprovedProductByFacilityAndProgram(Integer facilityId, Integer programId) {
    return repository.getFullSupplyProductsByFacilityAndProgram(facilityId, programId);
  }

  public List<FacilityApprovedProduct> getNonFullSupplyFacilityApprovedProductByFacilityAndProgram(Integer facilityId, Integer programId){
    return repository.getNonFullSupplyProductsByFacilityAndProgram(facilityId, programId);
  }

  public void save(FacilityApprovedProduct facilityApprovedProduct) {
    Integer programId = programService.getIdForCode(facilityApprovedProduct.getProgramProduct().getProgram().getCode());
    Integer productId = productService.getIdForCode(facilityApprovedProduct.getProgramProduct().getProduct().getCode());
    Integer programProductId = programProductService.getIdByProgramIdAndProductId(programId, productId);
    FacilityType facilityType = facilityService.getFacilityTypeByCode(facilityApprovedProduct.getFacilityType());

    facilityApprovedProduct.getProgramProduct().getProgram().setId(programId);
    facilityApprovedProduct.getProgramProduct().getProduct().setId(productId);
    facilityApprovedProduct.getProgramProduct().setId(programProductId);
    facilityApprovedProduct.getFacilityType().setId(facilityType.getId());

    if (facilityApprovedProduct.getId() != null) {
      repository.update(facilityApprovedProduct);
    } else {
      repository.insert(facilityApprovedProduct);
    }
  }

  public FacilityApprovedProduct getFacilityApprovedProductByProgramProductAndFacilityTypeCode(FacilityApprovedProduct facilityApprovedProduct) {
    return repository.getFacilityApprovedProductByProgramProductAndFacilityTypeCode(facilityApprovedProduct);
  }
}
