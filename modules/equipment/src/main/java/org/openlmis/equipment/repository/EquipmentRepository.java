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


import org.openlmis.core.domain.Pagination;
import org.openlmis.core.exception.DataException;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentType;
import org.openlmis.equipment.repository.mapper.EquipmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EquipmentRepository {

  @Autowired
  EquipmentMapper mapper;

  public Equipment getById(Long id){
    return mapper.getById(id);
  }

  public List<Equipment> getAll(){
    return mapper.getAll();
  }

  public List<Equipment> getAllByType(Long equipmentTypeId) {
    return mapper.getAllByType(equipmentTypeId);
  }
  public List<Equipment> getByType(Long equipmentTypeId, Pagination page) {
    return mapper.getByType(equipmentTypeId, page);
  }

  public Integer getCountByType(Long equipmentTypeId)
  {
    return mapper.getCountByType(equipmentTypeId);
  }

  public List<EquipmentType> getTypesByProgram(Long programId){
    return mapper.getTypesByProgram(programId);
  }

  public void insert(Equipment equipment){
    mapper.insert(equipment);
  }

  public void update(Equipment equipment){
    mapper.update(equipment);
  }

  public void remove(Long id){
    Equipment equipment = getById(id);
    if (equipment != null && equipment.isRemovable()) {
      mapper.remove(id);
    } else {
      throw new DataException("message.equipment.cannot.remove.inventory.exists");
    }
  }

}
