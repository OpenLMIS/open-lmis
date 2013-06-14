/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.distribution.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.distribution.domain.AllocationProgramProduct;
import org.openlmis.distribution.domain.ProgramProductISA;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllocationProgramProductMapper {

  @Insert({"INSERT INTO program_product_isa (whoRatio, dosesPerYear, wastageRate, bufferPercentage, minimumValue, adjustmentValue)",
    "VALUES (#{whoRatio}, #{dosesPerYear}, #{wastageRate}, #{bufferPercentage} ," +
      "#{minimumValue}, #{adjustmentValue} )"})
  @Options(useGeneratedKeys = true)
  Integer insertISA(ProgramProductISA programProductISA);

  @Select("SELECT * from program_products where programId = #{programId}")
  @Results(value = {
    @Result(property = "program", column = "programId", javaType = Program.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById")),
    @Result(property = "product", column = "productId", javaType = Product.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById")),
    @Result(property = "programProductISA", column = "programProductISAId", javaType = ProgramProductISA.class,
      one = @One(select = "org.openlmis.distribution.repository.mapper.AllocationProgramProductMapper.getISAById"))
  })
  List<AllocationProgramProduct> getWithISAByProgram(Long programId);

  @Select("SELECT * FROM program_product_isa WHERE id = #{id}")
  ProgramProductISA getISAById(Long id);

  @Update({"UPDATE program_product_isa SET whoRatio = #{whoRatio} , dosesPerYear = #{dosesPerYear}, ",
    "wastageRate = #{wastageRate}, bufferPercentage = #{bufferPercentage}, minimumValue = #{minimumValue}, ",
    "adjustmentValue = #{adjustmentValue} where id = #{id}"})
  void updateISA(ProgramProductISA programProductISA);

  @Update({"UPDATE program_products SET programProductISAId = #{programProductISA.id} WHERE id = #{programProductId}"})
  void updateProgramProductForISA(@Param("programProductId")Long programProductId, @Param("programProductISA") ProgramProductISA programProductISA);


}
