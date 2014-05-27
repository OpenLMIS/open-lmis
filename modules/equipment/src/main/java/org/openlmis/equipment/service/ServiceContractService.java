/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.equipment.service;

import org.openlmis.equipment.domain.ServiceContract;
import org.openlmis.equipment.domain.ServiceType;
import org.openlmis.equipment.dto.ContractDetail;
import org.openlmis.equipment.repository.ServiceContractRepository;
import org.openlmis.equipment.repository.ServiceTypeRepository;
import org.openlmis.equipment.repository.mapper.ServiceContractMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceContractService {

  @Autowired
  private ServiceContractRepository repository;

  @Autowired
  private ServiceContractMapper mapper;

  public List<ServiceContract> getAll(){
    return repository.getAll();
  }

  public List<ServiceContract> getAllForFacility(Long facilityId){
    return repository.getAllForFacility(facilityId);
  }

  public List<ServiceContract> getAllForVendor(Long vendorId){
    return repository.getAllForVendor(vendorId);
  }

  public ServiceContract getById(Long id){
    return repository.getById(id);
  }

  public void save(ServiceContract contract){
    if(contract.getId() == null){
      repository.insert(contract);
    }else{
      repository.update(contract);

      // save the mappings
      mapper.deleteFacilities(contract.getId());
      for(ContractDetail detail: contract.getFacilities()){
        if(detail.getIsActive()){
          mapper.insertFacilities(contract.getId(), detail.getId());
        }
      }

      mapper.deleteEquipments(contract.getId());
      for(ContractDetail detail: contract.getEquipments()){
        if(detail.getIsActive()){
          mapper.insertEquipment(contract.getId(), detail.getId());
        }
      }

      mapper.deleteServiceTypes(contract.getId());
      for(ContractDetail detail: contract.getServiceTypes()){
        if(detail.getIsActive()){
          mapper.insertServiceTypes(contract.getId(), detail.getId());
        }
      }
    }
  }

}
