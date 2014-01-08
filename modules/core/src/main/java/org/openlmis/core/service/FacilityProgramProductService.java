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

import org.apache.commons.collections.Closure;
import org.openlmis.core.domain.FacilityProgramProduct;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductISA;
import org.openlmis.core.repository.FacilityProgramProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.forAllDo;

@Service
public class FacilityProgramProductService {

  @Autowired
  private FacilityProgramProductRepository repository;

  @Autowired
  ProgramProductService programProductService;

  public List<FacilityProgramProduct> getForProgramAndFacility(Long programId, final Long facilityId) {
    List<ProgramProduct> programProducts = programProductService.getActiveByProgram(programId);
    final List<FacilityProgramProduct> facilityProgramProducts = new ArrayList<>();
    forAllDo(programProducts, new Closure() {
      @Override
      public void execute(Object o) {
        facilityProgramProducts.add(getAllocationProduct((ProgramProduct) o, facilityId));
      }
    });
    return facilityProgramProducts;
  }

  private FacilityProgramProduct getAllocationProduct(ProgramProduct programProduct, Long facilityId) {
    return new FacilityProgramProduct(programProduct, facilityId, repository.getOverriddenIsa(programProduct.getId(), facilityId));
  }

  public void insertISA(ProgramProductISA isa) {
    repository.insertISA(isa);
  }


  public void updateISA(ProgramProductISA isa) {
    repository.updateISA(isa);
  }

  public void saveOverriddenIsa(final Long facilityId, List<FacilityProgramProduct> products) {
    forAllDo(products, new Closure() {
      @Override
      public void execute(Object o) {
        FacilityProgramProduct product = (FacilityProgramProduct) o;
        product.setFacilityId(facilityId);
        repository.save(product);
      }
    });
  }

  public List<FacilityProgramProduct> getByFacilityAndProgram(Long facilityId, Long programId) {
    return repository.getByFacilityAndProgram(facilityId, programId);
  }
}