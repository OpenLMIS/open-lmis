/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.repository.mapper.demographics;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.demographics.FacilityDemographicEstimate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityDemographicEstimateMapper {

  @Insert("insert into facility_demographic_estimates " +
    " (year, facilityId, demographicEstimateId, conversionFactor, value)" +
    " values " +
    " (#{year}, #{facilityId}, #{demographicEstimateId}, #{conversionFactor}, #{value}) ")
  @Options(flushCache = true, useGeneratedKeys = true)
  Integer insert(FacilityDemographicEstimate estimate);

  @Update("update facility_demographic_estimates " +
    " set " +
    " year = #{year}, " +
    " facilityId = #{facilityId}," +
    " demographicEstimateId = #{demographicEstimateId}," +
    " conversionFactor = #{conversionFactor}," +
    " value = #{value}" +
    "where id = #{id} ")
  Integer update(FacilityDemographicEstimate estimate);

  @Select("select * from facility_demographic_estimates where year = #{year} and facilityId = #{facilityId}")
  List<FacilityDemographicEstimate> getEstimatesForFacility(@Param("year") Integer year, @Param("facilityId") Long facilityId);
}
