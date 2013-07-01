/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.apache.commons.collections.Closure;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.AllocationProgramProduct;
import org.openlmis.core.domain.ProgramProductISA;
import org.openlmis.core.repository.AllocationProgramProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.forAllDo;

@Service
public class AllocationProgramProductService {

  @Autowired
  private AllocationProgramProductRepository repository;

  @Autowired
  ProgramProductService programProductService;

  public List<AllocationProgramProduct> get(Long programId) {
    List<ProgramProduct> programProducts = programProductService.getByProgram(new Program(programId));
    final List<AllocationProgramProduct> allocationProgramProducts = new ArrayList<>();
    forAllDo(programProducts, new Closure() {
      @Override
      public void execute(Object o) {
        allocationProgramProducts.add(getAllocationProduct((ProgramProduct) o));
      }
    });
    return allocationProgramProducts;
  }

  public List<AllocationProgramProduct> getForProgramAndFacility(Long programId, final Long facilityId) {
    List<ProgramProduct> programProducts = programProductService.getByProgram(new Program(programId));
    final List<AllocationProgramProduct> allocationProgramProducts = new ArrayList<>();
    forAllDo(programProducts, new Closure() {
      @Override
      public void execute(Object o) {
        allocationProgramProducts.add(getAllocationProduct((ProgramProduct) o, facilityId));
      }
    });
    return allocationProgramProducts;
  }

  private AllocationProgramProduct getAllocationProduct(ProgramProduct programProduct) {
    AllocationProgramProduct allocationProgramProduct = repository.getByProgramProductId(programProduct.getId());
    allocationProgramProduct.fillFrom(programProduct);
    return allocationProgramProduct;
  }

  private AllocationProgramProduct getAllocationProduct(ProgramProduct programProduct, Long facilityId) {
    AllocationProgramProduct allocationProgramProduct = getAllocationProduct(programProduct);
    allocationProgramProduct.setOverriddenIsa(repository.getOverriddenIsa(programProduct.getId(), facilityId));
    return allocationProgramProduct;
  }

  public void insertISA(ProgramProductISA isa) {
    repository.insertISA(isa);
  }


  public void updateISA(ProgramProductISA isa) {
    repository.updateISA(isa);
  }

  public void saveOverriddenIsa(final Long facilityId, List<AllocationProgramProduct> products) {
    forAllDo(products, new Closure() {
      @Override
      public void execute(Object o) {
        AllocationProgramProduct product = (AllocationProgramProduct) o;
        product.setFacilityId(facilityId);
        repository.save(product);
      }
    });
  }

  public List<AllocationProgramProduct> getByFacilityAndProgram(Long facilityId, Long programId) {
    return repository.getByFacilityAndProgram(facilityId, programId);
  }
}