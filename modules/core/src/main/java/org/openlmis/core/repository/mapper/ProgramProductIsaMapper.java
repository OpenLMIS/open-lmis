/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.ProgramProductISA;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramProductIsaMapper {

  @Insert({"INSERT INTO program_product_isa",
    "(programProductId, whoRatio, dosesPerYear, wastageFactor, bufferPercentage, minimumValue, maximumValue, adjustmentValue,createdBy,createdDate,modifiedBy,modifiedDate)",
    "VALUES (#{programProductId}, #{whoRatio}, #{dosesPerYear}, #{wastageFactor}, #{bufferPercentage},",
    "#{minimumValue}, #{maximumValue}, #{adjustmentValue},#{createdBy},COALESCE(#{createdDate}, NOW()),#{modifiedBy},COALESCE(#{modifiedDate}, NOW()) )"})
  @Options(useGeneratedKeys = true)
  void insert(ProgramProductISA programProductISA);

  @Update({"UPDATE program_product_isa SET whoRatio = #{whoRatio} , dosesPerYear = #{dosesPerYear}, ",
    "wastageFactor = #{wastageFactor}, bufferPercentage = #{bufferPercentage}, minimumValue = #{minimumValue}, ",
    "maximumValue = #{maximumValue}, adjustmentValue = #{adjustmentValue},modifiedBy=#{modifiedBy},modifiedDate=(COALESCE(#{modifiedDate}, NOW())) where id = #{id}"})
  void update(ProgramProductISA programProductISA);

  @Select("SELECT * FROM program_product_isa WHERE programProductId = #{programProductId}")
  ProgramProductISA getIsaByProgramProductId(Long programProductId);
}
