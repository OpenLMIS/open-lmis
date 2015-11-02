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
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.FacilityApprovedProductRepository;
import org.openlmis.core.repository.FacilityProgramProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.forAllDo;

/**
 * Exposes the services for handling FacilityProgramProduct entity.
 */

@Service
public class FacilityProgramProductService {

  @Autowired
  private FacilityProgramProductRepository repository;

  @Autowired
  private FacilityApprovedProductRepository facilityApprovedProductRepository;

  @Autowired
  private FacilityService facilityService;

  @Autowired
  ProgramProductService programProductService;

  public List<FacilityProgramProduct> getForProgramAndFacility(Long programId, final Long facilityId) {
    List<ProgramProduct> programProducts = programProductService.getByProgram(new Program(programId));
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

  public List<StockRequirements> getStockRequirements(final Long facilityId, Long programId)
  {
    //temporarily get facility in order to access its catchment population
    Facility facility = facilityService.getById(facilityId);

    List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts = facilityApprovedProductRepository.getAllByFacilityAndProgramId(facilityId, programId);

    List<StockRequirements> stockRequirements = new ArrayList<>();

    List<FacilityProgramProduct> programProductsByProgram = getActiveProductsForProgramAndFacility(programId, facilityId);
    for (FacilityProgramProduct facilityProgramProduct : programProductsByProgram)
    {
      StockRequirements requirements = new StockRequirements();
      requirements.setFacilityId(facilityId);

      //set our ISA to the most specific one possible
      ISA facilityIsa = facilityProgramProduct.getOverriddenIsa();
      if(facilityIsa != null)
        requirements.setIsa(facilityIsa);
      else if(facilityProgramProduct.getProgramProductIsa() != null)
        requirements.setIsa(facilityProgramProduct.getProgramProductIsa().getIsa());

      //set productId
      Long productId = facilityProgramProduct.getProduct().getId();
      requirements.setProductId(productId);
      requirements.setProductName(facilityProgramProduct.getProduct().getPrimaryName());

      //set catchmentPopulation
      requirements.setPopulation(facility.getCatchmentPopulation());
      //set minStock, maxStock, and eop
      for(FacilityTypeApprovedProduct facilityTypeApprovedProduct : facilityTypeApprovedProducts)
      {


        if(productId.equals(facilityTypeApprovedProduct.getProgramProduct().getProduct().getId()))
        {
          ProgramProduct programProduct = facilityTypeApprovedProduct.getProgramProduct();
          ProductCategory category = programProduct.getProductCategory();
          requirements.setProductCategory(category.getName());
          requirements.setMinMonthsOfStock(facilityTypeApprovedProduct.getMinMonthsOfStock());
          requirements.setMaxMonthsOfStock(facilityTypeApprovedProduct.getMaxMonthsOfStock());
          requirements.setEop(facilityTypeApprovedProduct.getEop());
          break;
        }
      }
      stockRequirements.add(requirements);
    }

    return stockRequirements;
  }

  public void insertISA(Long facilityId, ProgramProductISA isa)
  {
    repository.insertISA(facilityId, isa);
  }


  public void deleteISA(Long facilityId, ProgramProductISA isa)
  {
    deleteISA(facilityId, isa.getProgramProductId());
  }

  public void deleteISA(Long facilityId, Long programProductId)
  {
    repository.deleteOverriddenIsa(programProductId, facilityId);
  }



  public void updateISA(ProgramProductISA isa)
  {
    repository.updateISA(isa);
  }

  public void save(final Long facilityId, List<FacilityProgramProduct> products) {
    forAllDo(products, new Closure() {
      @Override
      public void execute(Object o) {
        FacilityProgramProduct product = (FacilityProgramProduct) o;
        product.setFacilityId(facilityId);
        repository.save(product);
      }
    });
  }

  public List<FacilityProgramProduct> getActiveProductsForProgramAndFacility(Long programId, Long facilityId) {
    return FacilityProgramProduct.filterActiveProducts(getForProgramAndFacility(programId, facilityId));
  }

  public ISA getOverriddenIsa(Long programProductId, Long facilityId)
  {
    return repository.getOverriddenIsa(programProductId,facilityId);
  }


}