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
import org.openlmis.vaccine.domain.demographics.FacilityDemographicEstimate;
import org.openlmis.vaccine.dto.DemographicEstimateLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityDemographicEstimateMapper {

  @Insert("insert into facility_demographic_estimates " +
          " (year, facilityId, demographicEstimateId, conversionFactor, programId , value)" +
    " values " +
          " (#{year}, #{facilityId}, #{demographicEstimateId}, #{conversionFactor}, #{programId}, #{value}) ")
  @Options(flushCache = true, useGeneratedKeys = true)
  Integer insert(FacilityDemographicEstimate estimate);

  @Update("update facility_demographic_estimates " +
          " set " +
          " conversionFactor = #{conversionFactor}," +
          " value = #{value}, " +
          " modifiedBy = #{modifiedBy}, " +
          " modifiedDate = NOW()" +
          " where id = #{id} ")
  Integer update(FacilityDemographicEstimate estimate);

    @Update("update facility_demographic_estimates " +
            " set " +
            " isFinal = true, " +
            " modifiedBy = #{modifiedBy}, " +
            " modifiedDate = NOW()" +
            " where id = #{id} ")
    Integer finalize(FacilityDemographicEstimate estimate);

    @Update("update facility_demographic_estimates " +
            " set " +
            " isFinal = false, " +
            " modifiedBy = #{modifiedBy}," +
            " modifiedDate = NOW()" +
            "where id = #{id} ")
    Integer undoFinalize(FacilityDemographicEstimate estimate);


    @Select("select * from facility_demographic_estimates where year = #{year} and facilityId = #{facilityId} and programId = #{programId}")
    List<FacilityDemographicEstimate> getEstimatesForFacility(@Param("year") Integer year, @Param("facilityId") Long facilityId, @Param("programId") Long programId);


    @Select("select s.* from facility_demographic_estimates s " +
            " join demographic_estimate_categories c on c.id = s.demographicEstimateId " +
            " where year = #{year} and facilityId = #{facilityId} " +
            "   and programId = #{programId} " +
            " order by c.id")
    @Results(value = {
            @Result(column = "demographicEstimateId", property = "demographicEstimateId"),
            @Result(property = "category", column = "demographicEstimateId", one = @One(select = "org.openlmis.vaccine.repository.mapper.demographics.DemographicEstimateCategoryMapper.getById"))
    }
    )
    List<FacilityDemographicEstimate> getEstimatesForFacilityWithDetails(@Param("year") Integer year, @Param("facilityId") Long facilityId, @Param("programId") Long programId);


    @Select("select f.name, f.id, f.code, gz.id as parentId, gz.name as parentName " +
            " from facilities f " +
            "     join geographic_zones gz on gz.id = f.geographicZoneId " +
            "     join programs_supported ps on ps.facilityId = f.id  and ps.programId = #{programId} " +
            "     join requisition_group_members m on m.facilityId = f.id " +
            " where m.requisitionGroupId  = ANY(#{requisitionGroupIds}::INTEGER[]) " +
            " order by gz.name, f.name")
    List<DemographicEstimateLineItem> getFacilityList(@Param("programId") Long programId, @Param("requisitionGroupIds") String requsitionGroupIds);
}
