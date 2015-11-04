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

package org.openlmis.equipment.repository;

import org.apache.log4j.Logger;
import org.openlmis.core.domain.Pagination;
import org.openlmis.equipment.domain.*;
import org.openlmis.equipment.repository.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EquipmentInventoryRepository {

  public static Logger logger = Logger.getLogger(EquipmentInventoryRepository.class);
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
  @Autowired
  EquipmentOperationalStatusMapper equipmentOperationalStatusMapper;

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
    if(status != null) {
      inventory.setOperationalStatusId(status.getStatusId());
      inventory.setNotFunctionalStatusId(status.getNotFunctionalStatusId());
    }
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
      status.setCreatedBy(inventory.getCreatedBy());
      status.setModifiedBy(inventory.getModifiedBy());
      if (!equipmentOperationalStatusMapper.getById(status.getStatusId()).getIsBad()) {
        status.setNotFunctionalStatusId(null);
      }
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

  public String updateNonFunctionalEquipments() {
    return mapper.updateNonFunctionalEquipments();
  }
}
