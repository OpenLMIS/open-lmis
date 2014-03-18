/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.distribution.repository.mapper;


import org.apache.ibatis.annotations.*;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.DistributionStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * It maps the Distribution entity to corresponding representation in database.
 */

@Repository
public interface DistributionMapper {

  @Insert({"INSERT INTO distributions",
    "(deliveryZoneId, programId, periodId, status, createdBy, modifiedBy)",
    "VALUES",
    "(#{deliveryZone.id}, #{program.id}, #{period.id}, #{status}, #{createdBy}, #{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  void insert(Distribution distribution);

  @Select({"SELECT * FROM distributions where programId=#{program.id}",
    "AND periodId=#{period.id}",
    "AND deliveryZoneId=#{deliveryZone.id}"})
  @Results(value = {
    @Result(property = "program.id", column = "programId"),
    @Result(property = "period.id", column = "periodId"),
    @Result(property = "deliveryZone.id", column = "deliveryZoneId")
  })
  Distribution get(Distribution distribution);

  @Update({"UPDATE distributions SET status =  #{status}, modifiedBy = #{modifiedBy}, modifiedDate = DEFAULT WHERE id = #{id}"})
  void updateDistributionStatus(@Param("id") Long id, @Param("status") DistributionStatus status, @Param("modifiedBy") Long modifiedBy);

  @Select({"SELECT periodId from distributions where deliveryZoneId = #{deliveryZoneId} AND programId = #{programId} and status = 'SYNCED'"})
  List<Long> getSyncedPeriodsForDeliveryZoneAndProgram(@Param("deliveryZoneId") Long deliveryZoneId, @Param("programId") Long programId);

  @Select({"SELECT * FROM distributions WHERE id = #{distributionId}"})
  @Results(value = {
    @Result(property = "deliveryZone", column = "deliveryZoneId", javaType = Long.class,
      one = @One(select = "org.openlmis.core.repository.mapper.DeliveryZoneMapper.getById")),
    @Result(property = "period.id", column = "periodId"),
    @Result(property = "program.id", column = "programId")
  })
  Distribution getBy(Long distributionId);
}
