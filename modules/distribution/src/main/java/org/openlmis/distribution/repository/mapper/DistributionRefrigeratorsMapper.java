/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.distribution.domain.RefrigeratorProblem;
import org.openlmis.distribution.domain.RefrigeratorReading;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * It maps the RefrigeratorReading and RefrigeratorProblem entity to corresponding representation in database.
 */

@Repository
public interface DistributionRefrigeratorsMapper {


  @Insert({"INSERT INTO refrigerator_readings",
    "(temperature, functioningCorrectly, lowAlarmEvents, highAlarmEvents, ",
    "problemSinceLastTime, notes, facilityVisitId, refrigeratorId, refrigeratorSerialNumber, refrigeratorBrand, refrigeratorModel, createdBy, modifiedBy)",
    "VALUES",
    "(#{temperature}, #{functioningCorrectly}, #{lowAlarmEvents}, #{highAlarmEvents}, ",
    "#{problemSinceLastTime}, #{notes}, #{facilityVisitId}, #{refrigerator.id}, #{refrigerator.serialNumber}, #{refrigerator.brand}, #{refrigerator.model}, #{createdBy}, #{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  void insertReading(RefrigeratorReading refrigeratorReading);

  @Update({"UPDATE refrigerator_readings SET temperature = #{temperature}, functioningCorrectly = #{functioningCorrectly}, lowAlarmEvents = #{lowAlarmEvents},",
    "highAlarmEvents = #{highAlarmEvents}, problemSinceLastTime = #{problemSinceLastTime}, notes = #{notes}, facilityVisitId = #{facilityVisitId},",
    "refrigeratorId = #{refrigerator.id}, refrigeratorSerialNumber = #{refrigerator.serialNumber}, refrigeratorBrand = #{refrigerator.brand}, refrigeratorModel = #{refrigerator.model},",
    "modifiedBy = #{modifiedBy}, modifiedDate=DEFAULT WHERE id = #{id}"})
  void updateReading(RefrigeratorReading refrigeratorReading);

  @Insert({"INSERT INTO refrigerator_problems(readingId, operatorError, burnerProblem, gasLeakage, egpFault, thermostatSetting, other, otherProblemExplanation, createdBy, modifiedBy) ",
    "VALUES (#{readingId}, COALESCE(#{operatorError}, FALSE), COALESCE(#{burnerProblem}, FALSE), COALESCE(#{gasLeakage}, FALSE),",
    "COALESCE(#{egpFault}, FALSE), COALESCE(#{thermostatSetting}, FALSE), COALESCE(#{other}, FALSE), #{otherProblemExplanation}, #{createdBy}, #{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  void insertProblem(RefrigeratorProblem problem);

  @Update({"UPDATE refrigerator_problems SET readingId = #{readingId}, operatorError = COALESCE(#{operatorError}, FALSE), burnerProblem = COALESCE(#{burnerProblem}, FALSE),",
    "gasLeakage = COALESCE(#{gasLeakage}, FALSE), egpFault = COALESCE(#{egpFault}, FALSE), thermostatSetting = COALESCE(#{thermostatSetting}, FALSE),",
    "other = COALESCE(#{other}, FALSE), otherProblemExplanation = #{otherProblemExplanation}, modifiedBy = #{modifiedBy}, modifiedDate=DEFAULT WHERE id = #{id}"})
  void updateProblem(RefrigeratorProblem problem);

  @Select({"SELECT * FROM refrigerator_readings where facilityVisitId = #{facilityVisitId} ORDER BY LOWER(refrigeratorSerialNumber)"})
  @Results(value = {
    @Result(column = "refrigeratorId", property = "refrigerator.id"),
    @Result(column = "refrigeratorSerialNumber", property = "refrigerator.serialNumber"),
    @Result(column = "refrigeratorBrand", property = "refrigerator.brand"),
    @Result(column = "refrigeratorModel", property = "refrigerator.model"),
    @Result(column = "id", property = "id"),
    @Result(column = "id", property = "problem", javaType = RefrigeratorProblem.class, one = @One(select = "getProblemByReadingId")),
  })
  List<RefrigeratorReading> getBy(Long facilityVisitId);

  @Select({"SELECT * FROM refrigerator_problems WHERE readingId = #{readingId}"})
  RefrigeratorProblem getProblemByReadingId(Long readingId);

  @Select({"SELECT * FROM refrigerator_readings where id = #{id}"})
  @Results(value = {
    @Result(column = "refrigeratorId", property = "refrigerator.id"),
    @Result(column = "refrigeratorSerialNumber", property = "refrigerator.serialNumber"),
    @Result(column = "refrigeratorBrand", property = "refrigerator.brand"),
    @Result(column = "refrigeratorModel", property = "refrigerator.model"),
    @Result(column = "id", property = "id"),
    @Result(column = "id", property = "problem", javaType = RefrigeratorProblem.class, one = @One(select = "getProblemByReadingId")),
  })
  RefrigeratorReading getReading(Long id);

  @Select({"SELECT * FROM refrigerator_problems where id = #{id}"})
  RefrigeratorProblem getProblem(Long id);

  @Select({"SELECT RR.* FROM refrigerator_readings RR INNER JOIN refrigerators R ON RR.refrigeratorId = R.id WHERE RR.refrigeratorId = #{refrigeratorId} AND R.serialNumber = #{serialNumber} AND facilityVisitId = #{facilityVisitId}"})
  @Results(value = {
          @Result(column = "refrigeratorId", property = "refrigerator.id"),
          @Result(column = "refrigeratorSerialNumber", property = "refrigerator.serialNumber"),
          @Result(column = "refrigeratorBrand", property = "refrigerator.brand"),
          @Result(column = "refrigeratorModel", property = "refrigerator.model"),
          @Result(column = "id", property = "id"),
          @Result(column = "id", property = "problem", javaType = RefrigeratorProblem.class, one = @One(select = "getProblemByReadingId")),
  })
  RefrigeratorReading getByRefrigeratorIdAndSerialNumber(@Param("refrigeratorId") Long refrigeratorId, @Param("serialNumber") String serialNumber, @Param("facilityVisitId") Long facilityVisitId);
}
