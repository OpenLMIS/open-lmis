/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.allocation.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.allocation.domain.DeliveryZoneProgramSchedule;
import org.springframework.stereotype.Repository;


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
}
