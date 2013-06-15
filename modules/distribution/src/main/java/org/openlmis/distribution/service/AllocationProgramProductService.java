/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.distribution.service;

import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.distribution.domain.AllocationProgramProduct;
import org.openlmis.distribution.domain.ProgramProductISA;
import org.openlmis.distribution.repository.AllocationProgramProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AllocationProgramProductService {

  @Autowired
  private AllocationProgramProductRepository repository;

  @Autowired
  ProgramProductService programProductService;

  public List<AllocationProgramProduct> get(Long programId) {
    List<ProgramProduct> programProducts = programProductService.getByProgram(new Program(programId));
    List<AllocationProgramProduct> allocationProgramProducts = new ArrayList<>();
    for (ProgramProduct programProduct : programProducts) {
      AllocationProgramProduct allocationProgramProduct = new AllocationProgramProduct(programProduct);
      allocationProgramProduct.setProgramProductISA(repository.getIsa(allocationProgramProduct.getId()));
      allocationProgramProducts.add(allocationProgramProduct);
    }
    return allocationProgramProducts;
  }

  public void insertISA(ProgramProductISA isa) {
    repository.insertISA(isa);
  }

  public void updateISA(ProgramProductISA isa) {
    repository.updateISA(isa);
  }
}
