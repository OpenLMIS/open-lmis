/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.equipment.repository;

import org.openlmis.equipment.domain.ColdChainEquipmentDesignation;
import org.openlmis.equipment.repository.mapper.ColdChainEquipmentDesignationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ColdChainEquipmentDesignationRepository {

  @Autowired
  ColdChainEquipmentDesignationMapper designationMapper;

  public ColdChainEquipmentDesignation getById(Long id){
    return designationMapper.getById(id);
  }

  public List<ColdChainEquipmentDesignation> getAll(){
    return designationMapper.getAll();
  }

  public void insert(ColdChainEquipmentDesignation designation){
    designationMapper.insert(designation);
  }

  public void update(ColdChainEquipmentDesignation designation){
    designationMapper.update(designation);
  }

}
