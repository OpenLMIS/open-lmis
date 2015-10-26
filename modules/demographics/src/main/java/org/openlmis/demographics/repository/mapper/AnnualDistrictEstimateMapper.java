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

package org.openlmis.demographics.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.demographics.dto.EstimateFormLineItem;
import org.openlmis.demographics.domain.AnnualDistrictEstimateEntry;
import org.openlmis.demographics.domain.AnnualFacilityEstimateEntry;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnualDistrictEstimateMapper {

  @Insert("insert into district_demographic_estimates " +
    " (year, districtId, demographicEstimateId, programId , conversionFactor, value)" +
    " values " +
    " (#{year}, #{districtId}, #{demographicEstimateId}, #{programId}, #{conversionFactor}, #{value}) ")
  @Options(flushCache = true, useGeneratedKeys = true)
  Integer insert(AnnualDistrictEstimateEntry estimate);

  @Select("select * from district_demographic_estimates where id = #{id}")
  AnnualDistrictEstimateEntry getById(@Param("id") Long id);

  @Update("update district_demographic_estimates " +
    " set " +
    " conversionFactor = #{conversionFactor}," +
    " value = #{value}" +
    "where id = #{id} ")
  Integer update(AnnualDistrictEstimateEntry estimate);

  @Update("update district_demographic_estimates " +
    " set " +
    " isFinal = true" +
    " where id = #{id} ")
  Integer finalizeEstimate(AnnualDistrictEstimateEntry estimate);

  @Update("update district_demographic_estimates " +
    " set " +
    " isFinal = false" +
    " where id = #{id} ")
  Integer undoFinalize(AnnualDistrictEstimateEntry estimate);

  @Select("select * from district_demographic_estimates where year = #{year} and districtId = #{districtId} and programId = #{programId} and demographicEstimateId = #{demographicEstimateId}")
  AnnualDistrictEstimateEntry getEntryBy(@Param("year") Integer year, @Param("districtId") Long districtId, @Param("programId") Long programId, @Param("demographicEstimateId")Long categoryId);


  @Select("select * from district_demographic_estimates " +
    " where " +
    "     year = #{year} and districtId = #{districtId} and programId = #{programId}")
  List<AnnualDistrictEstimateEntry> getEstimatesForDistrict(@Param("year") Integer year, @Param("districtId") Long districtId, @Param("programId") Long programId);

  @Select("select r.id as parentId, r.name as parentName, z.* from geographic_zones z join geographic_zones r on r.id = z.parentId " +
    "     where z.levelId = (select max(levelNumber) from geographic_levels) and z.parentId in (select gz.parentId from facilities ff join geographic_zones gz on gz.id = ff.geographicZoneId where ff.id  = Any(#{facilities}::INTEGER[]))" +
    "     order by r.name, z.name")
  List<EstimateFormLineItem> getDistrictLineItems(@Param("facilities") String facilityIds);

  @Select("select z.name as name, e.demographicEstimateId, f.geographicZoneId as facilityId, sum(e.value) as value " +
    " from facility_demographic_estimates e " +
    " join facilities f on e.facilityId = f.id " +
    " join geographic_zones z on z.id = f.geographicZoneId " +
    " where e.year = #{year} and f.geographicZoneId = #{districtId} and e.programId = #{programId} " +
    "group by z.name, e.demographicEstimateId, f.geographicZoneId ")
  List<AnnualFacilityEstimateEntry> getFacilityEstimateAggregate(@Param("year") Integer year, @Param("districtId") Long districtId, @Param("programId") Long programId);

}
