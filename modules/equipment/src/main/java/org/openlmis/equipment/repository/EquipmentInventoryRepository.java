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

import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentInventory;
import org.openlmis.equipment.domain.EquipmentType;
import org.openlmis.equipment.repository.mapper.ColdChainEquipmentMapper;
import org.openlmis.equipment.repository.mapper.EquipmentInventoryMapper;
import org.openlmis.equipment.repository.mapper.EquipmentMapper;
import org.openlmis.equipment.repository.mapper.EquipmentTypeMapper;
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

  public static Logger logger = Logger.getLogger(EquipmentInventoryRepository.class);

  public List<EquipmentInventory> getFacilityInventory(Long facilityId, Long programId){
    return mapper.getInventoryByFacilityAndProgram(facilityId, programId);
  }

  public List<EquipmentInventory> getInventory(Long programId, Long equipmentTypeId, long[] facilityIds){
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

    List<EquipmentInventory> inventories = mapper.getInventory(programId, equipmentTypeId, str.toString());
    for (EquipmentInventory inventory : inventories) {
      setEquipmentToInventory(inventory);
    }
    return inventories;
  }

  public EquipmentInventory getInventoryById(Long id){
    EquipmentInventory inventory = mapper.getInventoryById(id);
    setEquipmentToInventory(inventory);
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

  public void insert(EquipmentInventory inventory){
    mapper.insert(inventory);
  }

  public void update(EquipmentInventory inventory){
    mapper.update(inventory);
  }

  public void updateStatus(EquipmentInventory inventory){
    mapper.updateStatus(inventory);
  }

}
