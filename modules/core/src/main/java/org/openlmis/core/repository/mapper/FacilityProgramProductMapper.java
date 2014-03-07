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
import org.openlmis.core.domain.FacilityProgramProduct;
import org.springframework.stereotype.Repository;
/**
 * FacilityApprovedProductMapper maps the FacilityTypeApprovedProduct mapping entity to corresponding representation in database.
 */
@Repository
public interface FacilityProgramProductMapper {


  @Insert("INSERT INTO facility_program_products(programProductId, facilityId, overriddenIsa) VALUES " +
    "(#{id}, #{facilityId}, #{overriddenIsa})")
  @Options(useGeneratedKeys = true)
  void insert(FacilityProgramProduct facilityProgramProduct);

  @Select({"SELECT overriddenIsa FROM facility_program_products WHERE programProductId = #{programProductId} AND",
    "facilityId = #{facilityId}"})
  Integer getOverriddenIsa(@Param("programProductId") Long programProductId, @Param("facilityId") Long facilityId);

  @Delete("DELETE FROM facility_program_products WHERE facilityId = #{facilityId} AND programProductId = #{programProductId}")
  void removeFacilityProgramProductMapping(@Param("programProductId") Long programProductId, @Param("facilityId") Long facilityId);

}
