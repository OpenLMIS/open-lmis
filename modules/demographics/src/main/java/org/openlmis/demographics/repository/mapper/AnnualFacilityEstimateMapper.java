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
import org.openlmis.demographics.domain.AnnualFacilityEstimateEntry;
import org.openlmis.demographics.dto.EstimateFormLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnualFacilityEstimateMapper {

  @Insert("insert into facility_demographic_estimates " +
    " (year, facilityId, demographicEstimateId, conversionFactor, programId , value)" +
    " values " +
    " (#{year}, #{facilityId}, #{demographicEstimateId}, #{conversionFactor}, #{programId}, #{value}) ")
  @Options(flushCache = true, useGeneratedKeys = true)
  Integer insert(AnnualFacilityEstimateEntry estimate);

  @Update("update facility_demographic_estimates " +
    " set " +
    " conversionFactor = #{conversionFactor}," +
    " value = #{value}, " +
    " modifiedBy = #{modifiedBy}, " +
    " modifiedDate = NOW()" +
    " where id = #{id} ")
  Integer update(AnnualFacilityEstimateEntry estimate);

  @Select("select * from facility_demographic_estimates where year = #{year} and facilityId = #{facilityId} and programId = #{programId} and demographicEstimateId = #{demographicEstimateId}")
  AnnualFacilityEstimateEntry getEntryBy(@Param("year") Integer year, @Param("facilityId") Long facilityId, @Param("programId") Long programId, @Param("demographicEstimateId") Long categoryId);

  @Update("update facility_demographic_estimates " +
    " set " +
    " isFinal = true, " +
    " modifiedBy = #{modifiedBy}, " +
    " modifiedDate = NOW()" +
    " where id = #{id} ")
  Integer finalizeEstimate(AnnualFacilityEstimateEntry estimate);

  @Update("update facility_demographic_estimates " +
    " set " +
    " isFinal = false, " +
    " modifiedBy = #{modifiedBy}," +
    " modifiedDate = NOW()" +
    "where id = #{id} ")
  Integer undoFinalize(AnnualFacilityEstimateEntry estimate);


  @Select("select * from facility_demographic_estimates where year = #{year} and facilityId = #{facilityId} and programId = #{programId}")
  List<AnnualFacilityEstimateEntry> getEstimatesForFacility(@Param("year") Integer year, @Param("facilityId") Long facilityId, @Param("programId") Long programId);


  @Select("select s.* from facility_demographic_estimates s " +
    " join demographic_estimate_categories c on c.id = s.demographicEstimateId " +
    " where year = #{year} and facilityId = #{facilityId} " +
    "   and programId = #{programId} " +
    " order by c.id")
  @Results(value = {
    @Result(column = "demographicEstimateId", property = "demographicEstimateId"),
    @Result(property = "category", column = "demographicEstimateId", one = @One(select = "org.openlmis.demographics.repository.mapper.EstimateCategoryMapper.getById"))
  }
  )
  List<AnnualFacilityEstimateEntry> getEstimatesForFacilityWithDetails(@Param("year") Integer year, @Param("facilityId") Long facilityId, @Param("programId") Long programId);


  @Select("select distinct f.name, f.id, f.code, gz.id as parentId, gz.name as parentName " +
    " from facilities f " +
    "     join geographic_zones gz on gz.id = f.geographicZoneId " +
    "     join programs_supported ps on ps.facilityId = f.id  and ps.programId = #{programId} " +
    "     join requisition_group_members m on m.facilityId = f.id " +
    " where m.requisitionGroupId  = ANY(#{requisitionGroupIds}::INTEGER[]) " +
    " order by gz.name, f.name")
  List<EstimateFormLineItem> getFacilityList(@Param("programId") Long programId, @Param("requisitionGroupIds") String requsitionGroupIds);
}
