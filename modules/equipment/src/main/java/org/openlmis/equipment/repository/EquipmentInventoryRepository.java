/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.equipment.repository;

import org.openlmis.core.domain.Pagination;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentInventory;
import org.openlmis.equipment.domain.EquipmentInventoryStatus;
import org.openlmis.equipment.domain.EquipmentType;
import org.openlmis.equipment.repository.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.apache.log4j.Logger;

@Repository
public class EquipmentInventoryRepository {

  @Autowired
  EquipmentInventoryMapper mapper;

  @Autowired
  EquipmentMapper equipmentMapper;

  @Autowired
  EquipmentTypeMapper equipmentTypeMapper;

  @Autowired
  ColdChainEquipmentMapper coldChainEquipmentMapper;

  @Autowired
  EquipmentInventoryStatusMapper equipmentInventoryStatusMapper;

  public static Logger logger = Logger.getLogger(EquipmentInventoryRepository.class);

  public List<EquipmentInventory> getFacilityInventory(Long facilityId, Long programId){
    List<EquipmentInventory> inventories = mapper.getInventoryByFacilityAndProgram(facilityId, programId);
    for (EquipmentInventory inventory : inventories) {
      setEquipmentToInventory(inventory);
      setStatusToInventory(inventory);
    }
    return inventories;
  }

  public List<EquipmentInventory> getInventory(Long programId, Long equipmentTypeId, long[] facilityIds, Pagination pagination) {
    String strFacilityIds = getFacilityIdString(facilityIds);

    List<EquipmentInventory> inventories = mapper.getInventory(programId, equipmentTypeId, strFacilityIds, pagination);
    for (EquipmentInventory inventory : inventories) {
      setEquipmentToInventory(inventory);
      setStatusToInventory(inventory);
    }
    return inventories;
  }

  public Integer getInventoryCount(Long programId, Long equipmentTypeId, long[] facilityIds) {
    String strFacilityIds = getFacilityIdString(facilityIds);

    return mapper.getInventoryCount(programId, equipmentTypeId, strFacilityIds);
  }

  private String getFacilityIdString(long[] facilityIds) {
    // Convert ids into string format for the mapper to use
    StringBuilder str = new StringBuilder();
    if (facilityIds.length == 0) {
      str.append("{}");
    } else {
      str.append("{");
      for (int i = 0; i < facilityIds.length-1; i++) {
        str.append(facilityIds[i]);
        str.append(",");
      }
      str.append(facilityIds[facilityIds.length-1]);
      str.append("}");
    }

    return str.toString();
  }

  public EquipmentInventory getInventoryById(Long id){
    EquipmentInventory inventory = mapper.getInventoryById(id);
    setEquipmentToInventory(inventory);
    setStatusToInventory(inventory);
    return inventory;
  }

  private void setEquipmentToInventory(EquipmentInventory inventory) {
    Long equipmentId = inventory.getEquipmentId();
    Equipment equipment = equipmentMapper.getById(equipmentId);
    EquipmentType equipmentType = equipmentTypeMapper.getEquipmentTypeById(equipment.getEquipmentTypeId());
    if (equipmentType.isColdChain()) {
      equipment = coldChainEquipmentMapper.getById(equipmentId);
    }
    inventory.setEquipment(equipment);
  }

  private void setStatusToInventory(EquipmentInventory inventory) {
    EquipmentInventoryStatus status = equipmentInventoryStatusMapper.getCurrentStatus(inventory.getId());
    inventory.setOperationalStatusId(status.getStatusId());
    inventory.setNotFunctionalStatusId(status.getNotFunctionalStatusId());
  }

  public void insert(EquipmentInventory inventory){
    mapper.insert(inventory);
    updateStatus(inventory);
  }

  public void update(EquipmentInventory inventory){
    mapper.update(inventory);
    updateStatus(inventory);
  }

  public void updateStatus(EquipmentInventory inventory){
    EquipmentInventoryStatus existingStatus = equipmentInventoryStatusMapper.getCurrentStatus(inventory.getId());
    EquipmentInventoryStatus status = getStatusFromInventory(inventory);
    if (!status.equals(existingStatus)) {
      equipmentInventoryStatusMapper.insert(status);
    }
  }

  private EquipmentInventoryStatus getStatusFromInventory(EquipmentInventory inventory) {
    EquipmentInventoryStatus inventoryStatus = new EquipmentInventoryStatus();
    inventoryStatus.setInventoryId(inventory.getId());
    inventoryStatus.setStatusId(inventory.getOperationalStatusId());
    inventoryStatus.setNotFunctionalStatusId(inventory.getNotFunctionalStatusId());
    return inventoryStatus;
  }
}
