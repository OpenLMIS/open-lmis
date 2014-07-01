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

import org.openlmis.equipment.domain.EquipmentInventory;
import org.openlmis.equipment.domain.MaintenanceLog;
import org.openlmis.equipment.domain.MaintenanceRequest;
import org.openlmis.equipment.domain.ServiceContract;
import org.openlmis.equipment.repository.EquipmentInventoryRepository;
import org.openlmis.equipment.repository.MaintenanceLogRepository;
import org.openlmis.equipment.repository.MaintenanceRequestRepository;
import org.openlmis.equipment.repository.ServiceContractRepository;
import org.openlmis.equipment.repository.mapper.MaintenanceLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class MaintenanceLogService {

  @Autowired
  private MaintenanceLogRepository repository;

  @Autowired
  EquipmentInventoryRepository equipmentInventoryRepository;

  @Autowired
  ServiceContractRepository serviceContractRepository;

  public List<MaintenanceLog> getAll(){
    return repository.getAll();
  }

  public List<MaintenanceLog> getAllForFacility(Long facilityId){
    return repository.getAllForFacility(facilityId);
  }

  public List<MaintenanceLog> getAllForVendor(Long vendorId){
    return repository.getAllForFacility(vendorId);
  }

  public MaintenanceLog getById(Long id){
    return repository.getById(id);
  }

  public void save(MaintenanceLog log){
    if(log.getId() == null){
      repository.insert(log);
    }else{
      repository.update(log);
    }
  }

  public void save(MaintenanceRequest maintenanceRequest){
    EquipmentInventory equipmentInventory = equipmentInventoryRepository.getInventoryById(maintenanceRequest.getInventoryId());
    ServiceContract serviceContract = serviceContractRepository.getAllForEquipment(equipmentInventory.getEquipmentId()).get(0);
    MaintenanceLog log;
    log = new MaintenanceLog(
        maintenanceRequest.getUserId(),
        maintenanceRequest.getFacilityId(),
        equipmentInventory.getEquipmentId(),
        maintenanceRequest.getVendorId(),
        serviceContract.getId(),
        maintenanceRequest.getModifiedDate(),
        maintenanceRequest.getMaintenanceDetails().getServicePerformed(),
        maintenanceRequest.getMaintenanceDetails().getFinding(),
        maintenanceRequest.getVendorComment(),
        maintenanceRequest.getId(),
        maintenanceRequest.getMaintenanceDetails().getNextVisitDate());
    this.save(log);
  }
}