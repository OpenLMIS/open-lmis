/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProgramProduct;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramProductMapper {

  @Insert({"INSERT INTO program_products(programId, productId, dosesPerMonth, active, modifiedBy, modifiedDate)",
    "VALUES (#{program.id},",
    "(SELECT id FROM products WHERE LOWER(code) = LOWER(#{product.code})),",
    "#{dosesPerMonth}, #{active}, #{modifiedBy}, #{modifiedDate})"})
  @Options(useGeneratedKeys = true)
  Integer insert(ProgramProduct programProduct);
  // TODO : use programId

  // Used by FacilityApprovedProductMapper
  @SuppressWarnings("unused")
  @Select("SELECT * FROM program_products WHERE id = #{id}")
  @Results(value = {
    @Result(property = "product", column = "productId", javaType = Product.class, one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById")),
    @Result(property = "program", column = "programId", javaType = Product.class, one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById"))
  })
  ProgramProduct getById(Integer id);


  @Select(("SELECT id FROM program_products where programId = #{programId} and productId = #{productId}"))
  Integer getIdByProgramAndProductId(@Param("programId") Integer programId, @Param("productId") Integer productId);

  @Update("update program_products set currentPrice = #{currentPrice}, modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} where id = #{id}")
  void updateCurrentPrice(ProgramProduct programProduct);

  @Select(("SELECT * FROM program_products where programId = #{programId} and productId = #{productId}"))
  ProgramProduct getByProgramAndProductId(@Param("programId") Integer programId, @Param("productId") Integer productId);

  @Update("UPDATE program_products SET  dosesPerMonth=#{dosesPerMonth}, active=#{active}, modifiedBy=#{modifiedBy}, modifiedDate=#{modifiedDate} WHERE programId=#{program.id} AND productId=#{product.id}")
  void updateProgramProduct(ProgramProduct programProduct);
}