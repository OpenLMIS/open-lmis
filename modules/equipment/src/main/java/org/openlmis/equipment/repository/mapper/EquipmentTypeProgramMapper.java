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

package org.openlmis.equipment.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.equipment.domain.ProgramEquipmentType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentTypeProgramMapper {

  @Select("select pet.*, et.name equipmentTypeName, p.name programName " +
      "from equipment_type_programs pet " +
      "join equipment_types et on et.id = pet.equipmentTypeId " +
      "join programs p on p.id = pet.programId " +
      "where programId=#{programId} " +
      "ORDER BY equipmentTypeName ")
  @Results(value = {
      @Result(property = "program.id", column = "programId"),
      @Result(property = "equipmentType.id", column = "equipmentTypeId"),
      @Result(property = "program.name", column = "programName"),
      @Result(property = "equipmentType.name", column = "equipmentTypeName")
  })
  List<ProgramEquipmentType> getByProgramId(@Param(value = "programId") Long programId);

  @Insert("INSERT INTO equipment_type_programs(programId, equipmentTypeId, displayOrder, enableTestCount, enableTotalColumn, createdBy, createdDate, modifiedBy, modifiedDate) " +
      "VALUES (#{program.id},#{equipmentType.id},#{displayOrder}, #{enableTestCount},#{enableTotalColumn},#{createdBy},#{createdDate},#{modifiedBy},#{modifiedDate})")
  @Options(useGeneratedKeys = true)
  void insert(ProgramEquipmentType programEquipmentType);

  @Update("UPDATE equipment_type_programs " +
      "SET programId = #{program.id}, equipmentTypeId = #{equipmentType.id}, displayOrder = #{displayOrder}, enableTestCount = #{enableTestCount}, enableTotalColumn = #{enableTotalColumn},modifiedBy = #{modifiedBy},modifiedDate = #{modifiedDate} " +
      "WHERE id = #{id}")
  void update(ProgramEquipmentType programEquipmentType);

  @Delete("DELETE FROM equipment_type_programs WHERE id = #{programEquipmentTypeId}")
  void remove(Long programEquipmentTypeId);
}
