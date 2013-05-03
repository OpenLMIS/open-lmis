/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.ProgramProduct;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityApprovedProductMapper {

  @Insert("INSERT INTO facility_approved_products(" +
      "facilityTypeId, programProductId, maxMonthsOfStock, modifiedBy, modifiedDate) values " +
      "(#{facilityType.id}, #{programProduct.id}, #{maxMonthsOfStock}, #{modifiedBy}, #{modifiedDate})")
  @Options(useGeneratedKeys = true)
  Integer insert(FacilityApprovedProduct facilityApprovedProduct);

  @Select({"SELECT fap.*",
      "FROM facility_approved_products fap ",
      "INNER JOIN facilities f ON f.typeId = fap.facilityTypeId",
      "INNER JOIN program_products pp ON pp.id = fap.programProductId",
      "INNER JOIN products p ON p.id = pp.productId ",
      "INNER JOIN product_categories pc ON pc.id = p.categoryId ",
      "WHERE",
      "pp.programId = #{programId}",
      "AND f.id = #{facilityId}",
      "AND p.fullSupply = #{fullSupply}",
      "AND p.active = TRUE",
      "AND pp.active = TRUE",
      "ORDER BY pc.displayOrder, pc.name, p.displayOrder NULLS LAST, p.code"})
  @Results(value = {
      @Result(property = "programProduct", column = "programProductID", javaType = ProgramProduct.class,
          one = @One(select = "org.openlmis.core.repository.mapper.ProgramProductMapper.getById")),
      @Result(property = "facilityType.id", column = "facilityTypeId")})
  List<FacilityApprovedProduct> getProductsByFacilityProgramAndFullSupply(@Param("facilityId") Long facilityId,
                                                                          @Param("programId") Long programId,
                                                                          @Param("fullSupply") Boolean fullSupply);

  @Select({"SELECT fap.id, fap.facilityTypeId, fap.programProductId, fap.maxMonthsOfStock, fap.modifiedDate, fap.modifiedBy",
      "FROM facility_approved_products fap, facility_types ft",
      "where fap.programProductId = #{programProductId} and",
      "ft.code = #{facilityTypeCode} and ft.id = fap.facilityTypeId"})
  FacilityApprovedProduct getFacilityApprovedProductIdByProgramProductAndFacilityTypeCode(@Param("programProductId") Long programProductId,
                                                                                          @Param("facilityTypeCode") String facilityTypeCode);

  @Update("UPDATE facility_approved_products set " +
      "facilityTypeId=#{facilityType.id}, programProductId=#{programProduct.id}, maxMonthsOfStock=#{maxMonthsOfStock}, modifiedBy=#{modifiedBy}, modifiedDate=#{modifiedDate} " +
      "where id=#{id}")
  void updateFacilityApprovedProduct(FacilityApprovedProduct facilityApprovedProduct);

}
