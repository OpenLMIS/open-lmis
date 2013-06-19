/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.distribution.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.distribution.domain.AllocationProgramProduct;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityProgramProductMapper {


  @Insert("INSERT INTO facility_program_products(programProductId, facilityId, overriddenIsa) VALUES " +
    "(#{programProductId}, #{facilityId}, #{overriddenIsa})")
  void insert(AllocationProgramProduct allocationProgramProduct);

  @Select({"SELECT overriddenIsa FROM facility_program_products WHERE programProductId = #{programProductId} AND",
  "facilityId = #{facilityId}"})
  Integer getOverriddenIsa(@Param("programProductId")Long programProductId, @Param("facilityId") Long facilityId);

  @Delete("DELETE FROM facility_program_products WHERE facilityId = #{facilityId} AND programProductId = #{programProductId}")
  void removeFacilityProgramProductMapping(@Param("programProductId") Long programProductId, @Param("facilityId") Long facilityId);
}
