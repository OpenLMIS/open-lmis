
/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.RegimenColumn;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegimenColumnMapper {

  @Insert({"INSERT INTO program_regimen_columns",
    "(programId, name, label, visible, dataType, createdBy)",
    "VALUES (#{programId}, #{regimenColumn.name}, #{regimenColumn.label}, #{regimenColumn.visible}, #{regimenColumn.dataType}, #{regimenColumn.createdBy})"})
  public void insert(@Param("regimenColumn")RegimenColumn regimenColumn, @Param("programId")Long programId);

  @Select("SELECT * FROM program_regimen_columns WHERE programId = #{programId} ORDER BY id")
  List<RegimenColumn> getAllRegimenColumnsByProgramId(Long programId);

  @Update("UPDATE program_regimen_columns SET label = #{label}, visible = #{visible}, dataType = #{dataType}, " +
    "modifiedBy = #{modifiedBy}, modifiedDate = CURRENT_TIMESTAMP WHERE id = #{id}")
  void update(RegimenColumn regimenColumn);

  @Select("SELECT * from master_regimen_columns")
  List<RegimenColumn> getMasterRegimenColumns();
}
