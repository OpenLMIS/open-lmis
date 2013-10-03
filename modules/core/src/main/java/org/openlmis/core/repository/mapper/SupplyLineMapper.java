/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplyLineMapper {

  @Insert("INSERT INTO supply_lines " +
    "(description, supervisoryNodeId, programId, supplyingFacilityId, exportOrders, createdBy, modifiedBy, modifiedDate)" +
    "VALUES (#{description}, #{supervisoryNode.id}, #{program.id}, #{supplyingFacility.id}, #{exportOrders}, #{createdBy}, #{modifiedBy}, #{modifiedDate})")
  @Options(useGeneratedKeys = true)
  Integer insert(SupplyLine supplyLine);

  @Select("SELECT * FROM supply_lines WHERE supervisoryNodeId = #{supervisoryNode.id} AND programId = #{program.id}")
  @Results(value = {
    @Result(property = "supplyingFacility", javaType = Facility.class, column = "supplyingFacilityId",
      one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))
  })
  SupplyLine getSupplyLineBy(@Param(value = "supervisoryNode") SupervisoryNode supervisoryNode, @Param(value = "program") Program program);

  @Update("UPDATE supply_lines " +
    "SET description = #{description}, supervisoryNodeId = #{supervisoryNode.id}, programId = #{program.id}, " +
    "supplyingFacilityId = #{supplyingFacility.id}, exportOrders =#{exportOrders},modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} " +
    "WHERE id = #{id}")
  void update(SupplyLine supplyLine);

  @Select("SELECT * FROM supply_lines WHERE supervisoryNodeId = #{supervisoryNode.id} AND programId = #{program.id} " +
    "AND supplyingFacilityId = #{supplyingFacility.id}")
  @Results(value = {
    @Result(property = "supervisoryNode.id", column = "supervisoryNodeId"),
    @Result(property = "program.id", column = "programId"),
    @Result(property = "supplyingFacility.id", column = "supplyingFacilityId")
  })
  SupplyLine getSupplyLineBySupervisoryNodeProgramAndFacility(SupplyLine supplyLine);

  @Select({"SELECT SL.*, F.name AS facilityName, F.id AS facilityId FROM supply_lines SL",
    "INNER JOIN facilities F ON SL.supplyingFacilityId = F.id WHERE SL.id = #{id}"})
  @Results(value = {
    @Result(property = "supervisoryNode.id", column = "supervisoryNodeId"),
    @Result(property = "program.id", column = "programId"),
    @Result(property = "supplyingFacility.id", column = "facilityId"),
    @Result(property = "supplyingFacility.name", column = "facilityName")
  })
  SupplyLine getById(Long id);
}
