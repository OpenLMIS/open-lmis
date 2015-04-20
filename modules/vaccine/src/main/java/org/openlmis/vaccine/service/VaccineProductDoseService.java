/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.service;

import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.ProgramProductRepository;
import org.openlmis.vaccine.domain.VaccineProductDose;
import org.openlmis.vaccine.dto.ProductDoseProtocolDTO;
import org.openlmis.vaccine.repository.ProductDoseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class VaccineProductDoseService {

  @Autowired
  private ProductDoseRepository repository;

  @Autowired
  private ProgramProductRepository programProductRepository;

  public List<ProductDoseProtocolDTO> getProductDoseForProgram( Long programId ){

    List<ProductDoseProtocolDTO> protocols= new ArrayList<>();
    List<ProgramProduct> pp = programProductRepository.getActiveByProgram(programId);
    for(ProgramProduct p : pp){
      if(!p.getProduct().getFullSupply()){
        continue;
      }
      ProductDoseProtocolDTO protocol= new ProductDoseProtocolDTO();
      protocol.setProductId(p.getProduct().getId());
      protocol.setProductName(p.getProduct().getName());
      protocol.setDoses(repository.getDosesForProduct( programId, p.getProduct().getId()));
      protocols.add(protocol);
    }
    return protocols;
  }

  public List<VaccineProductDose> getForProgram(Long programId){
    return repository.getProgramProductDoses(programId);
  }

  public void save(List<ProductDoseProtocolDTO> protocols){
    for(ProductDoseProtocolDTO protocol : protocols){
      for(VaccineProductDose dose : protocol.getDoses()){
        if(dose.getId() == null){
          repository.insert(dose);
        }else{
          repository.update(dose);
        }
      }
    }
  }
}
