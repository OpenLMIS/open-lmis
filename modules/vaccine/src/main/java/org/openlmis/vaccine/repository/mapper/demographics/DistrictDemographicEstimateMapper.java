/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.vaccine.repository.mapper.demographics;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.vaccine.domain.demographics.DistrictDemographicEstimate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictDemographicEstimateMapper {

  @Insert("insert into district_demographic_estimates " +
    " (year, districtId, demographicEstimateId, conversionFactor, value)" +
    " values " +
    " (#{year}, #{districtId}, #{demographicEstimateId}, #{conversionFactor}, #{value}) ")
  @Options(flushCache = true, useGeneratedKeys = true)
  Integer insert(DistrictDemographicEstimate estimate);

  @Update("update district_demographic_estimates " +
    " set " +
    " year = #{year}, " +
    " districtId = #{districtId}," +
    " demographicEstimateId = #{demographicEstimateId}," +
    " conversionFactor = #{conversionFactor}," +
    " value = #{value}" +
    "where id = #{id} ")
  Integer update(DistrictDemographicEstimate estimate);

  @Select("select * from district_demographic_estimates where year = #{year} and districtId = #{districtId}")
  List<DistrictDemographicEstimate> getEstimatesForDistrict(@Param("year") Integer year, @Param("districtId") Long districtId);

  @Select("select * from geographic_zones " +
    "     where levelId = (select max(levelNumber) from geographic_levels)" +
    "     order by name")
  List<GeographicZone> getDistricts();
}
