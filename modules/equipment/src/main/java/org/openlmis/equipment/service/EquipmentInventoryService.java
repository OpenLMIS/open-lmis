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

import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.service.FacilityService;
import org.openlmis.equipment.domain.EquipmentInventory;
import org.openlmis.equipment.repository.EquipmentInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.openlmis.core.domain.RightName.*;

@Component
public class EquipmentInventoryService {

  @Autowired
  EquipmentInventoryRepository repository;

  @Autowired
  private FacilityService facilityService;

  public List<EquipmentInventory> getInventoryForFacility(Long facilityId, Long programId){
    return repository.getFacilityInventory(facilityId, programId );
  }

  public List<EquipmentInventory> getInventory(Long userId, Long typeId, Long programId, Long equipmentTypeId, Pagination pagination) {
    long[] facilityIds = getFacilityIds(userId, typeId, programId);

    return repository.getInventory(programId, equipmentTypeId, facilityIds, pagination);
  }

  public Integer getInventoryCount(Long userId, Long typeId, Long programId, Long equipmentTypeId) {
    long[] facilityIds = getFacilityIds(userId, typeId, programId);

    return repository.getInventoryCount(programId, equipmentTypeId, facilityIds);
  }

  private long[] getFacilityIds(Long userId, Long typeId, Long programId) {
    // Get list of facilities
    List<Facility> facilities;
    if (typeId == 0) {
      facilities = Arrays.asList(facilityService.getHomeFacility(userId));
    } else {
      facilities = facilityService.getUserSupervisedFacilities(userId, programId, MANAGE_EQUIPMENT_INVENTORY);
    }

    // From facilities, get facility ids
    long[] facilityIds = new long[facilities.size()];
    int index = 0;
    for (Facility f : facilities) {
      facilityIds[index++] = f.getId();
    }

    return facilityIds;
  }

  public EquipmentInventory getInventoryById(Long id){
    return repository.getInventoryById(id);
  }

  public void save(EquipmentInventory inventory){
    if(inventory.getId() == null){
      repository.insert(inventory);
    } else{
      repository.update(inventory);
    }
  }

  public void updateStatus(EquipmentInventory inventory){
    repository.updateStatus(inventory);
  }

}
