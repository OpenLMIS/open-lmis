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
import java.util.Map;

@Repository
public interface ProgramProductMapper {

  @Insert({"INSERT INTO program_products(programId, productId, dosesPerMonth, active, createdBy, modifiedBy, modifiedDate)",
    "VALUES (#{program.id},",
    "#{product.id}, #{dosesPerMonth}, #{active}, #{createdBy}, #{modifiedBy}, #{modifiedDate})"})
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

  @Select({"SELECT * FROM program_products pp INNER JOIN products p ON pp.productId = p.id WHERE programId = #{id} ",
    "ORDER BY p.displayOrder NULLS LAST, p.code"})
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "program", column = "programId", javaType = Program.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById")),
    @Result(property = "product", column = "productId", javaType = Product.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById")),
    @Result(property = "programProductIsa", column = "id", javaType = ProgramProductISA.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProgramProductIsaMapper.getIsaByProgramProductId"))
  })
  List<ProgramProduct> getByProgram(Program program);

  @Select("SELECT * from program_products where id = #{id}")
  @Results(value = {
    @Result(property = "product", column = "productId", javaType = Product.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById"))
  })
  ProgramProduct getById(Long id);

  @Select("SELECT pp.*, pr.code AS programCode, p.active as productActive FROM program_products pp " +
    "INNER JOIN products p ON pp.productId = p.id INNER JOIN programs pr ON pp.programId = pr.id WHERE p.code = #{code}")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "program.code", column = "programCode"),
    @Result(property = "product.active", column = "productActive")
  })
  List<ProgramProduct> getByProductCode(String code);

  @SelectProvider(type = ProgramProductByFacilityTypeCodeAndProgramId.class, method = "getProgramProductByProgramIdAndFacilityTypeCode")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "program", column = "programCode", javaType = Program.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getByCode")),
    @Result(property = "product", column = "productCode", javaType = Product.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getByCode"))
  })
  List<ProgramProduct> getByProgramIdAndFacilityCode(@Param("programId") Long programId, @Param("facilityTypeCode") String facilityTypeCode);

  public class ProgramProductByFacilityTypeCodeAndProgramId {

    public static String getProgramProductByProgramIdAndFacilityTypeCode(Map<String, Object> params) {
      String facilityTypeCode = (String) params.get("facilityTypeCode");
      Long programId = (Long) params.get("programId");

      StringBuilder buffer = new StringBuilder();
      buffer.append("SELECT pp.active, pr.code AS programCode, pr.name as programName, p.code as productCode, p.primaryName as productName, ");
      buffer.append("p.description, p.dosesPerDispensingUnit as unit, pc.name as category ");
      buffer.append("FROM program_products pp INNER JOIN products p ON pp.productId = p.id ");
      buffer.append("INNER JOIN product_categories pc ON pc.id = p.categoryId ");
      buffer.append("INNER JOIN programs pr ON pp.programId = pr.id ");
      if (facilityTypeCode == null) {
        buffer.append("WHERE pr.id = ").append(programId).append(" AND p.active = TRUE AND pp.active = TRUE ");
      } else {
        buffer.append("INNER JOIN facility_approved_products fap ON fap.programProductId = pp.id ");
        buffer.append("INNER JOIN facility_types ft ON ft.id = fap.facilityTypeId ");
        buffer.append("WHERE LOWER(ft.code) = LOWER('").append(facilityTypeCode).append("') AND pr.id = ")
          .append(programId).append(" AND p.active = TRUE AND pp.active = TRUE");
      }

      return buffer.toString();
    }

  }

}

