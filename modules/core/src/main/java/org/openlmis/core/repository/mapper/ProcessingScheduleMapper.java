/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.ProcessingSchedule;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessingScheduleMapper {

  @Select("SELECT id FROM processing_schedules WHERE LOWER(code) = LOWER(#{code})")
  Long getIdForCode(String code);

  @Insert({"INSERT INTO processing_schedules",
    "(code, name, description, createdBy,createdDate,modifiedDate,modifiedBy)",
    "VALUES(#{code}, #{name}, #{description}, #{createdBy},COALESCE(#{createdDate},NOW()),COALESCE(#{modifiedDate},NOW()),#{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  Integer insert(ProcessingSchedule schedule);

  @Select("SELECT * FROM processing_schedules")
  List<ProcessingSchedule> getAll();

  @Update({"UPDATE processing_schedules SET code = #{code}, name = #{name}, description = #{description},",
    "modifiedBy = #{modifiedBy}, modifiedDate = DEFAULT",
    "WHERE id = #{id}"})
  Integer update(ProcessingSchedule schedule);

  @Select("SELECT * FROM processing_schedules WHERE id = #{id}")
  ProcessingSchedule get(Long id);

  @Select("SELECT * FROM processing_schedules WHERE code = #{code}")
  ProcessingSchedule getByCode(String code);
}
