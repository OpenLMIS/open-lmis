/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.equipment.service;

import org.openlmis.equipment.domain.ServiceContract;
import org.openlmis.equipment.dto.ContractDetail;
import org.openlmis.equipment.repository.ServiceContractRepository;
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

  public List<ServiceContract> getAllForEquipment(Long equipmentId){
    return repository.getAllForEquipment(equipmentId);
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
