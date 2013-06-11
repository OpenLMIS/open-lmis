/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductISA;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramProductMapper {

  @Insert({"INSERT INTO program_products(programId, productId, dosesPerMonth, active, modifiedBy, modifiedDate)",
    "VALUES (#{program.id},",
    "#{product.id}, #{dosesPerMonth}, #{active}, #{modifiedBy}, #{modifiedDate})"})
  @Options(useGeneratedKeys = true)
  Integer insert(ProgramProduct programProduct);

  @Select(("SELECT id FROM program_products where programId = #{programId} and productId = #{productId}"))
  Long getIdByProgramAndProductId(@Param("programId") Long programId, @Param("productId") Long productId);

  @Update("update program_products set currentPrice = #{currentPrice}, modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} where id = #{id}")
  void updateCurrentPrice(ProgramProduct programProduct);

  @Select(("SELECT * FROM program_products where programId = #{programId} and productId = #{productId}"))
  ProgramProduct getByProgramAndProductId(@Param("programId") Long programId, @Param("productId") Long productId);

  @Update("UPDATE program_products SET  dosesPerMonth=#{dosesPerMonth}, active=#{active}, modifiedBy=#{modifiedBy}, modifiedDate=#{modifiedDate} WHERE programId=#{program.id} AND productId=#{product.id}")
  void update(ProgramProduct programProduct);

  @Select("SELECT * from program_products where programId = #{id}")
  @Results(value = {
    @Result(property = "program", column = "programId", javaType = Program.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById")),
    @Result(property = "product", column = "productId", javaType = Product.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById"))
  })
  List<ProgramProduct> getByProgram(Program program);

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
      one = @One(select = "org.openlmis.core.repository.mapper.ProgramProductMapper.getISAById"))
  })
  List<ProgramProduct> getWithISAByProgram(Long programId);

  @Select("SELECT * FROM program_product_isa WHERE id = #{id}")
  ProgramProductISA getISAById(Long id);

  @Update({"UPDATE program_product_isa SET whoRatio = #{whoRatio} , dosesPerYear = #{dosesPerYear}, ",
    "wastageRate = #{wastageRate}, bufferPercentage = #{bufferPercentage}, minimumValue = #{minimumValue}, ",
    "adjustmentValue = #{adjustmentValue} where id = #{id}"})
  void updateISA(ProgramProductISA programProductISA);

  @Update({"UPDATE program_products SET programProductISAId = #{programProductISA.id} WHERE id = #{programProductId}"})
  void updateProgramProductForISA(@Param("programProductId")Long programProductId, @Param("programProductISA") ProgramProductISA programProductISA);
}