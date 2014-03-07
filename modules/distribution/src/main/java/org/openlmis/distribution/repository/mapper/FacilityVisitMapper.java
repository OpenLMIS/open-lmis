/*
 *
 *  * This program is part of the OpenLMIS logistics management information system platform software.
 *  * Copyright © 2013 VillageReach
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  *  
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 *
 */

package org.openlmis.distribution.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.distribution.domain.FacilityVisit;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * It maps the FacilityVisit entity to corresponding representation in database.
 */

@Repository
public interface FacilityVisitMapper {

  @Insert({"INSERT INTO facility_visits (distributionId, facilityId, facilityCatchmentPopulation, createdBy, modifiedBy)",
    "VALUES (#{distributionId}, #{facilityId}, #{facilityCatchmentPopulation}, #{createdBy}, #{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  public void insert(FacilityVisit facilityVisit);

  @Select("SELECT * FROM facility_visits WHERE distributionId = #{distributionId} AND facilityId = #{facilityId}")
  @Results({
    @Result(property = "verifiedBy.name", column = "verifiedByName"),
    @Result(property = "verifiedBy.title", column = "verifiedByTitle"),
    @Result(property = "confirmedBy.name", column = "confirmedByName"),
    @Result(property = "confirmedBy.title", column = "confirmedByTitle")
  })
  public FacilityVisit getBy(@Param(value = "facilityId") Long facilityId, @Param(value = "distributionId") Long distributionId);

  @Update({"UPDATE facility_visits SET visited = #{visited}, visitDate = #{visitDate}, vehicleId = #{vehicleId}, ",
    "confirmedByName = #{confirmedBy.name}, confirmedByTitle = #{confirmedBy.title}, ",
    "verifiedByName = #{verifiedBy.name}, verifiedByTitle = #{verifiedBy.title}, ",
    "observations = #{observations}, synced = #{synced}, modifiedBy = #{modifiedBy}, modifiedDate = DEFAULT," +
      "reasonForNotVisiting = #{reasonForNotVisiting}, otherReasonDescription = #{otherReasonDescription} WHERE id = #{id}"})
  public void update(FacilityVisit facilityVisit);


  @Select({"SELECT * FROM facility_visits WHERE id = #{id}"})
  public FacilityVisit getById(Long id);


  @Select({"SELECT * FROM facility_visits WHERE distributionId = #{distributionId} AND synced = false"})
  List<FacilityVisit> getUnSyncedFacilities(Long distributionId);

  @Select({"SELECT count(*) FROM facility_visits WHERE distributionId = #{distributionId} AND synced = false"})
  Integer getUnsyncedFacilityCountForDistribution(Long distributionId);
}
