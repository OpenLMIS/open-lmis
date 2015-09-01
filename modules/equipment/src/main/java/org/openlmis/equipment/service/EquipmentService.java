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

import org.openlmis.core.domain.Pagination;
import org.openlmis.equipment.domain.ColdChainEquipment;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentType;
import org.openlmis.equipment.repository.ColdChainEquipmentRepository;
import org.openlmis.equipment.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EquipmentService {

  @Autowired
  private EquipmentRepository repository;

  @Autowired
  EquipmentTypeService equipmentTypeService;

  @Autowired
  ColdChainEquipmentRepository coldChainEquipmentRepository;

  public List<Equipment> getAll(){
      return repository.getAll();
  }

  public List<ColdChainEquipment> getAllCCE(Long equipmentTypeId, Pagination page){
    return coldChainEquipmentRepository.getAll(equipmentTypeId,page);
  }

  public List<Equipment> getAllByType(Long equipmentTypeId) {
    return repository.getAllByType(equipmentTypeId);
  }
  public List<Equipment> getByType(Long equipmentTypeId, Pagination page) {
    return repository.getByType(equipmentTypeId, page);
  }

  public Equipment getById(Long id){
    return repository.getById(id);

  }

  public Equipment getByTypeAndId(Long id,Long equipmentTypeId) {

    EquipmentType equipmentType=equipmentTypeService.getTypeById(equipmentTypeId);

    if (equipmentType.isColdChain()) {
      return coldChainEquipmentRepository.getById(id);
    } else {
      return repository.getById(id);
    }
  }
  public List<EquipmentType> getTypesByProgram(Long programId) {
    return repository.getTypesByProgram(programId);
  }

  public Integer getEquipmentsCountByType(Long equipmentTypeId)
  {
    return repository.getCountByType(equipmentTypeId);
  }

  public Integer getCCECountByType(Long equipmentTypeId)
  {
    return coldChainEquipmentRepository.getCountByType(equipmentTypeId);
  }
  public void saveEquipment(Equipment equipment){
      repository.insert(equipment);
  }
  public void saveColdChainEquipment(ColdChainEquipment coldChainEquipment){
      coldChainEquipmentRepository.insert(coldChainEquipment);
  }

  public void updateEquipment(Equipment equipment) {
     repository.update(equipment);
  }

  public void updateColdChainEquipment(ColdChainEquipment coldChainEquipment) {
    coldChainEquipmentRepository.update(coldChainEquipment);
  }


  public void removeEquipment(Long id) {
    repository.remove(id);
  }

  public void removeCCE(Long id) {
    coldChainEquipmentRepository.remove(id);
  }
}
