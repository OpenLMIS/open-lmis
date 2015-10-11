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

import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.email.service.EmailService;
import org.openlmis.equipment.domain.EquipmentInventory;
import org.openlmis.equipment.domain.MaintenanceLog;
import org.openlmis.equipment.domain.MaintenanceRequest;
import org.openlmis.equipment.domain.ServiceContract;
import org.openlmis.equipment.repository.EquipmentInventoryRepository;
import org.openlmis.equipment.repository.MaintenanceLogRepository;
import org.openlmis.equipment.repository.ServiceContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MaintenanceLogService {

  @Autowired
  private MaintenanceLogRepository repository;

  @Autowired
  EquipmentInventoryRepository equipmentInventoryRepository;

  @Autowired
  ServiceContractRepository serviceContractRepository;

  @Autowired
  VendorUserService vendorUserService;

  @Autowired
  VendorService vendorService;

  @Autowired
  EmailService emailService;

  @Autowired
  ConfigurationSettingService settingService;

  public List<MaintenanceLog> getAll() {
    return repository.getAll();
  }

  public List<MaintenanceLog> getAllForFacility(Long facilityId) {
    return repository.getAllForFacility(facilityId);
  }

  public List<MaintenanceLog> getAllForVendor(Long vendorId) {
    return repository.getAllForVendor(vendorId);
  }

  public MaintenanceLog getById(Long id) {
    return repository.getById(id);
  }

  public void save(MaintenanceLog log) {
    if (log.getId() == null) {
      repository.insert(log);
    } else {
      repository.update(log);
    }
  }


  public void save(MaintenanceRequest maintenanceRequest) {
    EquipmentInventory equipmentInventory = equipmentInventoryRepository.getInventoryById(maintenanceRequest.getInventoryId());
    List<ServiceContract> serviceContracts = serviceContractRepository.getAllForEquipment(equipmentInventory.getEquipmentId());
    Long serviceContractId = null;
    if (serviceContracts != null && !serviceContracts.isEmpty()) {
      serviceContractId = serviceContracts.get(0).getId();
    }
    MaintenanceLog log = new MaintenanceLog();
    log.setUserId(maintenanceRequest.getUserId());
    log.setFacilityId(maintenanceRequest.getFacilityId());
    log.setEquipmentId(equipmentInventory.getEquipmentId());
    log.setVendorId(maintenanceRequest.getVendorId());
    log.setContractId(serviceContractId);
    log.setModifiedDate(maintenanceRequest.getModifiedDate());
    log.setRequestId(maintenanceRequest.getId());
    this.save(log);
  }
}