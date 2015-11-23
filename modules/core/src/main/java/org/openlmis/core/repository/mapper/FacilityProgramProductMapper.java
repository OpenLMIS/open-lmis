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
import org.apache.ibatis.mapping.StatementType;
import org.openlmis.core.domain.*;
import org.springframework.stereotype.Repository;

/**
 * FacilityApprovedProductMapper maps the FacilityTypeApprovedProduct mapping entity to corresponding representation in database.
 */
@Repository
public interface FacilityProgramProductMapper {

  @Insert("INSERT INTO facility_program_products(programProductId, facilityId) VALUES (#{id}, #{facilityId})")
  @Options(useGeneratedKeys = true)
  void insert(FacilityProgramProduct facilityProgramProduct);

  //Note that insertISA is kept distinct from the above insert() implementation partly to be congruous
  //with the existing ProgramProductIsaMapper's insert() method that relies on a preexisting ProgramProduct.
  @Insert(value = "SELECT fn_insert_isa" +
          "(" +
            "#{ppi.whoRatio}::numeric, " +
            "#{ppi.dosesPerYear}::int," +
            "#{ppi.wastageFactor}::numeric," +
            "#{ppi.bufferPercentage}::numeric," +
            "#{ppi.minimumValue}::int," +
            "#{ppi.maximumValue}::int," +
            "#{ppi.adjustmentValue}::int," +
            "#{ppi.createdBy}::int," +
            "COALESCE(#{ppi.createdDate}, NOW())::timestamp ," +
            "#{ppi.modifiedBy}::int," +
            "COALESCE(#{ppi.modifiedDate}, NOW())::timestamp ," +
            "#{ppi.populationSource}::int," +
            "#{ppi.programProductId}::int ," +
            "#{facilityId}::int" +
          ")"
    )
  @Options(statementType = StatementType.CALLABLE)
  @SelectKey(
            statement = "SELECT id FROM isa_coefficients ORDER BY id DESC LIMIT 1",
            resultType = Long.class,
            before = false,
            keyColumn = "id",
            keyProperty = "ppi.isa.id"
  )
  void insertISA(@Param(value = "facilityId")Long facilityId, @Param(value = "ppi")ProgramProductISA ppi);


  @Select("SELECT ic.* FROM isa_coefficients ic JOIN facility_program_products fpp \n" +
          "ON fpp.isaCoefficientsId = ic.id \n" +
          "WHERE fpp.programproductid = #{programProductId} AND facilityid = #{facilityId}")
  ISA getOverriddenIsa(@Param("programProductId") Long programProductId, @Param("facilityId") Long facilityId);

  @Insert("SELECT fn_delete_facility_program_product_isa(#{programProductId}::int, #{facilityId}::int, false)")
  @Options(statementType=StatementType.CALLABLE)
  void deleteOverriddenIsa(@Param("programProductId") Long programProductId, @Param("facilityId") Long facilityId);


  /*
    NOTE that we cast a JDBC long to a PostgreSQL int, which works but isn't ideal. This mismatch is ultimately
    a symptom of org.openlmis.core.domain.BaseModel.id being defined as an Long, even while ID values in our
    schema are defined as integer/serial (32-bit) values. */
  @Insert("SELECT fn_delete_facility_program_product_isa(#{programProductId}::int, #{facilityId}::int, true)")
  @Options(statementType=StatementType.CALLABLE)
  void removeFacilityProgramProduct(@Param("programProductId") Long programProductId, @Param("facilityId") Long facilityId);

  @Select("SELECT f.id AS facilityid" +
          "   ,pg.id AS programid" +
          "   ,pd.id AS productid" +
          "   ,fpp.isacoefficientsid" +
          " FROM facility_program_products fpp" +
          "   JOIN facilities f ON f.id = fpp.facilityid" +
          "   JOIN program_products pp ON pp.id = fpp.programproductid" +
          "   JOIN programs pg ON pg.id = pp.programid" +
          "   JOIN products pd ON pd.id = pp.productid" +
          " WHERE f.code = #{facilityCode}" +
          "   AND pg.code = #{programCode}" +
          "   AND pd.code = #{productCode}")
  @Results({
          @Result(property = "program", column = "programId", javaType = Program.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById")),
          @Result(property = "product", column = "productId", javaType = Product.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById"))
  })
  FacilityProgramProduct getByCodes(@Param("facilityCode")String facilityCode,
                                    @Param("programCode")String programCode,
                                    @Param("productCode")String productCode);
}
