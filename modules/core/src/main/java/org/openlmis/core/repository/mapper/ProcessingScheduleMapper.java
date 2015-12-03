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

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.ProcessingSchedule;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * ProcessingScheduleMapper maps the ProcessingSchedule entity to corresponding representation in database.
 */
@Repository
public interface ProcessingScheduleMapper {

  @Select("SELECT id FROM processing_schedules WHERE LOWER(code) = LOWER(#{code})")
  Long getIdForCode(String code);

  @Insert({"INSERT INTO processing_schedules",
    "(code, name, description, createdBy,createdDate,modifiedDate,modifiedBy)",
    "VALUES(#{code}, #{name}, #{description}, #{createdBy},COALESCE(#{createdDate},NOW()),COALESCE(#{modifiedDate},NOW()),#{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  Integer insert(ProcessingSchedule schedule);

  @Select("SELECT * FROM processing_schedules ORDER BY LOWER(code)")
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
