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
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductISA;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramProductMapper {

  @Insert({"INSERT INTO program_products(programId, productId, dosesPerMonth, active, displayOrder, createdBy, modifiedBy, modifiedDate)",
    "VALUES (#{program.id},",
    "#{product.id}, #{dosesPerMonth}, #{active}, #{displayOrder}, #{createdBy}, #{modifiedBy}, #{modifiedDate})"})
  @Options(useGeneratedKeys = true)
  Integer insert(ProgramProduct programProduct);

  @Select(("SELECT id FROM program_products WHERE programId = #{programId} AND productId = #{productId}"))
  Long getIdByProgramAndProductId(@Param("programId") Long programId, @Param("productId") Long productId);

  @Update("UPDATE program_products SET currentPrice = #{currentPrice}, modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} WHERE id = #{id}")
  void updateCurrentPrice(ProgramProduct programProduct);

  @Select(("SELECT * FROM program_products WHERE programId = #{programId} AND productId = #{productId}"))
  ProgramProduct getByProgramAndProductId(@Param("programId") Long programId, @Param("productId") Long productId);

  @Update("UPDATE program_products SET  dosesPerMonth=#{dosesPerMonth}, active=#{active}, displayOrder=#{displayOrder}, modifiedBy=#{modifiedBy}, modifiedDate=#{modifiedDate} WHERE programId=#{program.id} AND productId=#{product.id}")
  void update(ProgramProduct programProduct);

  @Select({"SELECT * FROM program_products pp INNER JOIN products p ON pp.productId = p.id WHERE programId = #{id} and pp.active = true",
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


  @Select({"SELECT " +
      "   pp.id as id, pp.active, p.id as programId,p.name as programName, coalesce(pp.currentPrice,0) as currentPrice, coalesce(pp.dosesPerMonth,1) as dosesPerMonth, productId = #{id}, pp.displayOrder , pp.createdBy, pp.modifiedBy, pp.createdDate, pp.modifiedDate " +
      " FROM programs p " +
      " left outer join ( select * from program_products where productId = #{id} ) pp on p.id = pp.programId" +
      " ORDER BY p.name "})
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "program", column = "programId", javaType = Program.class,
          one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById"))

  })
  List<ProgramProduct> getOptionsByProduct(Product product);


  @Select("SELECT * FROM program_products WHERE id = #{id}")
  @Results(value = {
    @Result(property = "product", column = "productId", javaType = Product.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById"))
  })
  ProgramProduct getById(Long id);

  @Select("SELECT pp.*, pr.code AS programCode, p.active AS productActive FROM program_products pp " +
    "INNER JOIN products p ON pp.productId = p.id INNER JOIN programs pr ON pp.programId = pr.id WHERE p.code = #{code}")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "program.code", column = "programCode"),
    @Result(property = "product.active", column = "productActive")
  })
  List<ProgramProduct> getByProductCode(String code);

  @Select("SELECT DISTINCT pp.active, pr.code AS programCode, pr.name AS programName, p.code AS productCode, " +
    "       p.primaryName AS productName, p.description, p.dosesPerDispensingUnit AS unit, pc.name AS category " +
    "FROM program_products pp " +
    "INNER JOIN products p  ON pp.productid=p.id " +
    "INNER JOIN programs pr ON pr.id=pp.programid " +
    "LEFT OUTER JOIN product_categories pc ON pc.id = p.categoryId  " +
    "LEFT OUTER JOIN facility_approved_products fap ON fap.programproductid=pp.id " +
    "LEFT OUTER JOIN facility_types ft  ON ft.id=fap.facilitytypeid " +
    "WHERE " +
    " CASE " +
    "   WHEN COALESCE(#{facilityTypeCode}) IS NULL " +
    " THEN " +
    "   TRUE " +
    " ELSE LOWER(ft.code)=LOWER(#{facilityTypeCode}) " +
    " END " +
    "AND pr.id=#{programId} " +
    "AND p.active = TRUE " +
    "AND pp.active = TRUE ")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "program", column = "programCode", javaType = Program.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getByCode")),
    @Result(property = "product", column = "productCode", javaType = Product.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getByCode"))
  })
  List<ProgramProduct> getByProgramIdAndFacilityCode(@Param("programId") Long programId, @Param("facilityTypeCode") String facilityTypeCode);


  @Select({"SELECT * FROM program_products pp INNER JOIN products p ON pp.productId = p.id WHERE programId = #{id} AND p.fullsupply = FALSE"})
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "product", column = "productId", javaType = Product.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById"))
  })
  List<ProgramProduct> getNonFullSupplyProductsForProgram(Program program);
}

