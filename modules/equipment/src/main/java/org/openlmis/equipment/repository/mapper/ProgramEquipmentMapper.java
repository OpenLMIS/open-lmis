/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.equipment.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.equipment.domain.ProgramEquipment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramEquipmentMapper {

  @Select("select pe.*, e.name equipmentName, p.name programName " +
      "from program_equipments pe " +
      "join equipments e on e.id = pe.equipmentId " +
      "join programs p on p.id = pe.programId " +
      "where programId=#{programId} " +
      "ORDER BY displayOrder ")
  @Results(value = {
      @Result(property = "program.id", column = "programId"),
      @Result(property = "equipment.id", column = "equipmentId"),
      @Result(property = "program.name", column = "programName"),
      @Result(property = "equipment.name", column = "equipmentName")
  })
  List<ProgramEquipment> getByProgramId(@Param(value = "programId") Long programId);

  @Insert("INSERT INTO program_equipments(programId, equipmentId, displayOrder, enableTestCount, enableTotalColumn, createdBy, createdDate, modifiedBy, modifiedDate) " +
      "VALUES (#{program.id},#{equipment.id},#{displayOrder}, #{enableTestCount},#{enableTotalColumn},#{createdBy},#{createdDate},#{modifiedBy},#{modifiedDate})")
  @Options(useGeneratedKeys = true)
  void insert(ProgramEquipment programEquipment);

  @Update("UPDATE program_equipments " +
      "SET programId = #{program.id}, equipmentId = #{equipment.id}, displayOrder = #{displayOrder}, enableTestCount = #{enableTestCount}, enableTotalColumn = #{enableTotalColumn},modifiedBy = #{modifiedBy},modifiedDate = #{modifiedDate} " +
      "WHERE id = #{id}")
  void update(ProgramEquipment programEquipment);
}
