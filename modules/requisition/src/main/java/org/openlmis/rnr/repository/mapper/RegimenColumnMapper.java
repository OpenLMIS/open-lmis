/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.rnr.domain.RegimenColumn;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * It maps the RegimenTemplate and RegimenColumn entity to corresponding representations in database.
 */

@Repository
public interface RegimenColumnMapper {

  @Insert({"INSERT INTO program_regimen_columns",
    "(programId, name, label, visible, dataType, displayOrder, createdBy)",
    "VALUES (#{programId}, #{regimenColumn.name}, #{regimenColumn.label}, #{regimenColumn.visible}, #{regimenColumn.dataType}, #{regimenColumn.displayOrder}, #{regimenColumn.createdBy})"})
   void insert(@Param("regimenColumn") RegimenColumn regimenColumn, @Param("programId") Long programId);

  @Select("SELECT * FROM program_regimen_columns WHERE programId = #{programId} ORDER BY displayOrder")
  List<RegimenColumn> getAllRegimenColumnsByProgramId(Long programId);

  @Update("UPDATE program_regimen_columns SET label = #{label}, visible = #{visible}, dataType = #{dataType}, displayOrder = #{displayOrder} , " +
    "modifiedBy = #{modifiedBy}, modifiedDate = CURRENT_TIMESTAMP WHERE id = #{id}")
  void update(RegimenColumn regimenColumn);

  @Select("SELECT * from master_regimen_columns order by displayOrder")
  List<RegimenColumn> getMasterRegimenColumns();
}
