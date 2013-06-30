/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.AllocationProgramProduct;
import org.openlmis.core.domain.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityProgramProductMapper {


  @Insert("INSERT INTO facility_program_products(programProductId, facilityId, overriddenIsa) VALUES " +
    "(#{programProductId}, #{facilityId}, #{overriddenIsa})")
  @Options(useGeneratedKeys = true)
  void insert(AllocationProgramProduct allocationProgramProduct);

  @Select({"SELECT overriddenIsa FROM facility_program_products WHERE programProductId = #{programProductId} AND",
    "facilityId = #{facilityId}"})
  Integer getOverriddenIsa(@Param("programProductId") Long programProductId, @Param("facilityId") Long facilityId);

  @Delete("DELETE FROM facility_program_products WHERE facilityId = #{facilityId} AND programProductId = #{programProductId}")
  void removeFacilityProgramProductMapping(@Param("programProductId") Long programProductId, @Param("facilityId") Long facilityId);

  @Select({"SELECT fpp.*, pp.productId as productId FROM facility_program_products fpp, program_products pp,products p WHERE fpp.facilityId = #{facilityId} ",
    "AND pp.programId=#{programId} AND fpp.programProductId = pp.id ANd pp.productId = p.id ORDER BY ",
    "p.displayOrder NULLS LAST, p.code"})
  @Results(value = {
    @Result(property = "product", column = "productId", javaType = Product.class,
      one = @One(select = "org.openlmis.core.repository.mapper.ProductMapper.getById"))
  })
  List<AllocationProgramProduct> getByFacilityAndProgram(@Param("facilityId") Long facilityId, @Param("programId") Long programId);
}
