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

import org.openlmis.core.domain.ConfigurationSettingKey;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.FacilityService;
import org.openlmis.email.service.EmailService;
import org.openlmis.equipment.domain.EquipmentInventory;
import org.openlmis.equipment.domain.MaintenanceRequest;
import org.openlmis.equipment.domain.Vendor;
import org.openlmis.equipment.dto.Log;
import org.openlmis.equipment.repository.EquipmentInventoryRepository;
import org.openlmis.equipment.repository.MaintenanceRequestRepository;
import org.openlmis.equipment.repository.ServiceContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MaintenanceRequestService {

  private static final String USER = "user";
  private static final String REQUEST = "request";
  private static final String VENDOR = "vendor";
  private static final String FACILITY = "facility";
  private static final String EQUIPMENT = "equipment";

  @Autowired
  private MaintenanceRequestRepository repository;

  @Autowired
  EquipmentInventoryRepository equipmentInventoryRepository;

  @Autowired
  ServiceContractRepository serviceContractRepository;

  @Autowired
  EmailService emailService;

  @Autowired
  VendorUserService vendorUserService;

  @Autowired
  VendorService vendorService;

  @Autowired
  FacilityService facilityService;

  @Autowired
  ConfigurationSettingService settingService;


  public List<MaintenanceRequest> getAll() {
    return repository.getAll();
  }

  public List<MaintenanceRequest> getAllForFacility(Long facilityId) {
    return repository.getAllForFacility(facilityId);
  }

  public List<MaintenanceRequest> getAllForVendor(Long vendorId) {
    return repository.getAllForVendor(vendorId);
  }

  public List<MaintenanceRequest> getOutstandingForVendor(Long vendorId) {
    return repository.getOutstandingForVendor(vendorId);
  }

  public List<MaintenanceRequest> getOutstandingForUser(Long userId) {
    return repository.getOutstandingForUser(userId);
  }

  public MaintenanceRequest getById(Long id) {
    return repository.getById(id);
  }

  public void save(MaintenanceRequest request) {
    if (!request.hasId()) {
      repository.insert(request);
      notifyVendor(request);
    } else {
      repository.update(request);
    }
  }

  private void notifyVendor(MaintenanceRequest request) {
    EquipmentInventory equipmentInventory = equipmentInventoryRepository.getInventoryById(request.getInventoryId());
    Vendor vendor = vendorService.getById(request.getVendorId());

    Facility facility = facilityService.getById(request.getFacilityId());
    Map model = new HashMap();
    model.put(REQUEST, request);
    model.put(VENDOR, vendor);
    model.put(FACILITY, facility);
    model.put(EQUIPMENT, equipmentInventory);

    List<User> users = vendorUserService.getAllUsersForVendor(request.getUserId());

    String template = settingService.getConfigurationStringValue(ConfigurationSettingKey.VENDOR_MAINTENANCE_REQUEST_EMAIL_TEMPLATE);

    String vendorEmailSubject = "A new maintenance request submitted";
    for (User user : users) {
      if (model.containsKey(USER)) {
        model.remove(USER);
      }
      model.put(USER, user);
      emailService.queueHtmlMessage(user.getEmail(), vendorEmailSubject, template, model);
    }

  }

  public List<Log> getFullHistory(Long inventoryId) {
    return repository.getFullHistory(inventoryId);
  }
}
