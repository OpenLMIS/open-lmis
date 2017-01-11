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
import org.openlmis.distribution.domain.DistributionEdit;
import org.openlmis.distribution.domain.DistributionStatus;
import org.openlmis.distribution.domain.DistributionsEditHistory;
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

  @Update({"UPDATE distributions SET syncDate = CURRENT_TIMESTAMP WHERE id = #{id} AND syncDate IS NULL"})
  void updateSyncDate(@Param("id") Long id);

  @Update({"UPDATE distributions SET lastViewed = CURRENT_TIMESTAMP WHERE id = #{id}"})
  void updateLastViewed(@Param("id") Long id);

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

  @Select({"SELECT * FROM distributions WHERE status = 'SYNCED' AND programId=#{program.id} AND periodId=#{period.id} AND deliveryZoneId=#{deliveryZone.id}"})
  @Results(value = {
          @Result(property = "deliveryZone", column = "deliveryZoneId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.DeliveryZoneMapper.getById")),
          @Result(property = "period", column = "periodId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.ProcessingPeriodMapper.getById")),
          @Result(property = "program", column = "programId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById"))
  })
  Distribution getFullSyncedDistribution(Distribution distribution);

  @Select({"SELECT * FROM distributions WHERE status = 'SYNCED'"})
  @Results(value = {
          @Result(property = "deliveryZone", column = "deliveryZoneId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.DeliveryZoneMapper.getById")),
          @Result(property = "period", column = "periodId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.ProcessingPeriodMapper.getById")),
          @Result(property = "program", column = "programId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById"))
  })
  List<Distribution> getFullSyncedDistributions();

  @Select({"SELECT * FROM distributions WHERE status = 'SYNCED' AND programId = #{programId}"})
  @Results(value = {
          @Result(property = "deliveryZone", column = "deliveryZoneId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.DeliveryZoneMapper.getById")),
          @Result(property = "period", column = "periodId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.ProcessingPeriodMapper.getById")),
          @Result(property = "program", column = "programId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById"))
  })
  List<Distribution> getFullSyncedDistributionsForProgram(@Param("programId") Long programId);

  @Select({"SELECT * FROM distributions WHERE status = 'SYNCED' AND deliveryZoneId = #{deliveryZoneId} AND programId = #{programId}"})
  @Results(value = {
          @Result(property = "deliveryZone", column = "deliveryZoneId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.DeliveryZoneMapper.getById")),
          @Result(property = "period", column = "periodId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.ProcessingPeriodMapper.getById")),
          @Result(property = "program", column = "programId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById"))
  })
  List<Distribution> getFullSyncedDistributionsForProgramAndDeliveryZone(@Param("programId") Long programId, @Param("deliveryZoneId")Long deliveryZoneId);

  @Select({"SELECT * FROM distributions WHERE status = 'SYNCED' AND programId = #{programId} AND periodId = #{periodId}"})
  @Results(value = {
          @Result(property = "deliveryZone", column = "deliveryZoneId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.DeliveryZoneMapper.getById")),
          @Result(property = "period", column = "periodId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.ProcessingPeriodMapper.getById")),
          @Result(property = "program", column = "programId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById"))
  })
  List<Distribution> getFullSyncedDistributionsForProgramAndPeriod(@Param("programId") Long programId, @Param("periodId") Long periodId);

  @Select({"SELECT * FROM distributions WHERE status = 'SYNCED' AND deliveryZoneId = #{deliveryZoneId} AND programId = #{programId} AND periodId = #{periodId}"})
  @Results(value = {
          @Result(property = "deliveryZone", column = "deliveryZoneId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.DeliveryZoneMapper.getById")),
          @Result(property = "period", column = "periodId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.ProcessingPeriodMapper.getById")),
          @Result(property = "program", column = "programId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById"))
  })
  List<Distribution> getFullSyncedDistributionsForProgramAndDeliveryZoneAndPeriod(@Param("programId") Long programId, @Param("deliveryZoneId") Long deliveryZoneId, @Param("periodId") Long periodId);

  @Insert({"INSERT INTO distribution_edits (userId, distributionId) VALUES (#{userId}, #{distributionId})"})
  void insertEditInProgress(@Param("userId") Long userId, @Param("distributionId") Long distributionId);

  @Select({"SELECT * FROM distribution_edits WHERE distributionId = #{distributionId} AND userId = #{userId}"})
  @Results(value = {
          @Result(property = "user.id", column = "userId"),
          @Result(property = "distribution.id", column = "distributionId")
  })
  DistributionEdit getEditInProgressForUser(@Param("distributionId") Long distributionId, @Param("userId") Long userId);

  @Select({"SELECT * FROM distribution_edits WHERE distributionId = #{distributionId} AND userId <> #{userId} AND EXTRACT(EPOCH FROM CURRENT_TIMESTAMP - startedAt) <= #{periodInSeconds}"})
  @Results(value = {
          @Result(property = "user.id", column = "userId"),
          @Result(property = "distribution.id", column = "distributionId")
  })
  List<DistributionEdit> getEditInProgress(@Param("distributionId") Long distributionId, @Param("userId") Long userId, @Param("periodInSeconds") Long periodInSeconds);

  @Delete("DELETE FROM distribution_edits WHERE distributionId = #{distributionId} AND userId = #{userId}")
  void deleteDistributionEdit(@Param("distributionId") Long distributionId, @Param("userId") Long userId);

  @Insert({"INSERT INTO distributions_edit_history (distributionId, district, facilityId, dataScreen, editedItem, originalValue, newValue, editedBy) VALUES",
          "(#{distribution.id), #{district}, #{facility.id}, #{dataScreen}, #{editedItem}, #{originalValue}, #{newValue}, #{editedBy}"})
  void insertHistory(DistributionsEditHistory history);

  @Select({"SELECT * FROM distributions_edit_history WHERE distributionId = #{distributionId} ORDER BY editedDatetime ASC"})
  @Results(value = {
          @Result(property = "distribution", column = "distributionId", javaType = Long.class,
                  one = @One(select = "getBy")),
          @Result(property = "facility", column = "facilityId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
          @Result(property = "editedBy", column = "editedBy", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.UserMapper.getById"))
  })
  List<DistributionsEditHistory> getHistory(@Param("distributionId") Long distributionId);

  @Select({"SELECT * FROM distributions_edit_history WHERE distributionId = #{distributionId} ORDER BY editedDatetime DESC LIMIT 1"})
  @Results(value = {
          @Result(property = "distribution", column = "distributionId", javaType = Long.class,
                  one = @One(select = "getBy")),
          @Result(property = "facility", column = "facilityId", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
          @Result(property = "editedBy", column = "editedBy", javaType = Long.class,
                  one = @One(select = "org.openlmis.core.repository.mapper.UserMapper.getById"))
  })
  DistributionsEditHistory getLastHistory(Long distributionId);
}
