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
