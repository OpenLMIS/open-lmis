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

import org.apache.log4j.Logger;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.service.FacilityService;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentInventory;
import org.openlmis.equipment.repository.EquipmentInventoryRepository;
import org.openlmis.equipment.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.openlmis.core.domain.RightName.MANAGE_EQUIPMENT_INVENTORY;

@Component
public class EquipmentInventoryService {

  @Autowired
  EquipmentInventoryRepository repository;
  Logger logger = Logger.getLogger(EquipmentInventoryService.class);
  @Autowired
  private FacilityService facilityService;
  @Autowired
  private EquipmentService equipmentService;
  @Autowired
  private EquipmentRepository equipmentRepository;

  public List<EquipmentInventory> getInventoryForFacility(Long facilityId, Long programId){
    return repository.getFacilityInventory(facilityId, programId);
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
    // First, may need to save equipment into equipment list
    // Only need to do this for non-CCE
    Equipment equipment = inventory.getEquipment();
    if (!equipment.getEquipmentType().isColdChain()) {
      Boolean equipmentFound = false;
      Long equipmentTypeId = equipment.getEquipmentTypeId();

      // Check to see if equipment already exists in db
      List<Equipment> equipments = equipmentService.getAllByType(equipmentTypeId);
      for (Equipment e : equipments) {
        if (e.equalsByMakeAndModel(equipment)) {
          // Equipment already exists in db
          equipmentFound = true;
          equipment = e;
          break;
        }
      }

      equipment.setModifiedBy(inventory.getModifiedBy());
      if (!equipmentFound) {
        equipment.setCreatedBy(inventory.getCreatedBy());
        equipmentRepository.insert(equipment);
      } else {
        equipmentRepository.update(equipment);
      }

      // Make sure equipmentId is set for the inventory save, equipment.id is filled in after insert
      inventory.setEquipmentId(equipment.getId());
    }

    if(inventory.getId() == null){
      repository.insert(inventory);
    } else{
      repository.update(inventory);
    }
  }

  public void updateStatus(EquipmentInventory inventory){
    repository.updateStatus(inventory);
  }

  public void updateNonFunctionalEquipments() {
    repository.updateNonFunctionalEquipments();
  }

}
