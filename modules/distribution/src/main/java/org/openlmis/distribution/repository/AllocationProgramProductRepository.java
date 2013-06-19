/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.distribution.repository;

import org.openlmis.distribution.domain.AllocationProgramProduct;
import org.openlmis.distribution.domain.ProgramProductISA;
import org.openlmis.distribution.repository.mapper.FacilityProgramProductMapper;
import org.openlmis.distribution.repository.mapper.IsaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllocationProgramProductRepository {

  @Autowired
  IsaMapper isaMapper;

  @Autowired
  FacilityProgramProductMapper mapper;

  public void insertISA(ProgramProductISA programProductISA) {
    isaMapper.insert(programProductISA);
  }

  public void updateISA(ProgramProductISA programProductISA) {
    isaMapper.update(programProductISA);
  }

  public ProgramProductISA getIsa(Long id) {
    return isaMapper.getIsa(id);
  }

  public AllocationProgramProduct getByProgramProductId(Long programProductId) {
    ProgramProductISA isa = isaMapper.getIsa(programProductId);
    AllocationProgramProduct allocationProgramProduct = new AllocationProgramProduct();
    allocationProgramProduct.setProgramProductIsa(isa);
    allocationProgramProduct.setProgramProductId(programProductId);
    return allocationProgramProduct;
  }

  public Integer getOverriddenIsa(Long programProductId, Long facilityId) {
    return mapper.getOverriddenIsa(programProductId, facilityId);
  }

  public void save(AllocationProgramProduct product) {
    mapper.removeFacilityProgramProductMapping(product.getFacilityId(), product.getProgramProductId());
    mapper.insert(product);
  }
}
