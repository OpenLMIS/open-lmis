/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplyLineMapper {

  @Insert("INSERT INTO supply_lines " +
    "(description,supervisoryNodeId,programId,supplyingFacilityId,modifiedBy, modifiedDate)" +
    "VALUES (#{description},#{supervisoryNode.id},#{program.id},#{supplyingFacility.id},#{modifiedBy},#{modifiedDate})")
  @Options(useGeneratedKeys = true)
  Integer insert(SupplyLine supplyLine);

  @Select("SELECT * FROM supply_lines WHERE supervisoryNodeId = #{supervisoryNode.id} AND programId = #{program.id}")
  @Results(value = {
    @Result(property = "supplyingFacility.id", column = "supplyingFacilityId")
  })
  SupplyLine getSupplyLineBy(@Param(value = "supervisoryNode")SupervisoryNode supervisoryNode, @Param(value = "program")Program program);
}
