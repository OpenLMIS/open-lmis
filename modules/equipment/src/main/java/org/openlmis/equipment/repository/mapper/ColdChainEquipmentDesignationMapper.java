/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.equipment.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.equipment.domain.ColdChainEquipmentDesignation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColdChainEquipmentDesignationMapper {

  @Select("SELECT * from equipment_cold_chain_equipment_designations order by name")
  List<ColdChainEquipmentDesignation> getAll();

  @Select("SELECT * from equipment_cold_chain_equipment_designations where id = #{id}")
  ColdChainEquipmentDesignation getById(@Param("id") Long id);

  @Insert("INSERT into equipment_cold_chain_equipment_designations (name,createdby, createddate, modifiedby, modifieddate) " +
      "values " +
      "(#{name},#{createdBy}, NOW(), #{modifiedBy}, NOW())")
  @Options(useGeneratedKeys = true)
  void insert(ColdChainEquipmentDesignation coldChainEquipmentDesignation);

  @Update("UPDATE equipment_cold_chain_equipment_designations " +
      "set " +
      "name = #{name},modifiedBy = #{modifiedby}, modifieddate = NOW() " +
      "WHERE id = #{id}")
  void update(ColdChainEquipmentDesignation coldChainEquipmentDesignation);


}
