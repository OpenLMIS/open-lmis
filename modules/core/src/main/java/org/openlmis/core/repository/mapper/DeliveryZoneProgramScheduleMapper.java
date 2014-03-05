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
import org.openlmis.core.domain.DeliveryZoneProgramSchedule;
import org.openlmis.core.domain.ProcessingSchedule;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DeliveryZoneProgramScheduleMapper maps the DeliveryZoneProgramSchedule mapping entity to corresponding representation
 * in database.
 */
@Repository
public interface DeliveryZoneProgramScheduleMapper {

  @Insert({"INSERT INTO delivery_zone_program_schedules(deliveryZoneId, programId, scheduleId, createdBy, modifiedBy, modifiedDate)",
      "VALUES(#{deliveryZone.id}, #{program.id}, #{schedule.id}, #{createdBy}, #{modifiedBy}, #{modifiedDate})"})
  @Options(useGeneratedKeys = true)
  void insert(DeliveryZoneProgramSchedule deliveryZoneProgramSchedule);

  @Update({"UPDATE delivery_zone_program_schedules SET scheduleId = #{schedule.id}, modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate}",
      "WHERE deliveryZoneId = #{deliveryZone.id} AND programId = #{program.id}"})
  void update(DeliveryZoneProgramSchedule deliveryZoneProgramSchedule);

  @Select({"SELECT DZPS.* FROM delivery_zone_program_schedules DZPS INNER JOIN programs P ON P.id = DZPS.programId",
      "INNER JOIN delivery_zones DZ ON DZ.id = DZPS.deliveryZoneId WHERE LOWER(P.code) = LOWER(#{programCode}) AND",
      "LOWER(DZ.code) = LOWER(#{deliveryZoneCode})"})
  @Results({
      @Result(property = "program.id", column = "programId"),
      @Result(property = "deliveryZone.id", column = "deliveryZoneId"),
      @Result(property = "schedule.id", column = "scheduleId")
  })
  DeliveryZoneProgramSchedule getByDeliveryZoneCodeAndProgramCode(@Param("deliveryZoneCode") String deliveryZoneCode,
                                                                  @Param("programCode") String programCode);

  @Select("SELECT programId FROM delivery_zone_program_schedules WHERE deliveryZoneId = #{deliveryZoneId}")
  List<Long> getProgramsIdsForDeliveryZones(Long deliveryZoneId);

  @Select("SELECT scheduleId as id FROM delivery_zone_program_schedules WHERE deliveryZoneId = #{zoneId} AND programId = #{programId}")
  ProcessingSchedule getProcessingScheduleByZoneAndProgram(@Param("zoneId") long zoneId, @Param("programId") long programId);
}
