/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.ProcessingPeriod;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ProcessingPeriodMapper {

  @Select("SELECT * FROM processing_periods WHERE scheduleId = #{scheduleId} ORDER BY startDate DESC")
  List<ProcessingPeriod> getAll(Long scheduleId);

  @Insert({"INSERT INTO processing_periods",
    "(name, description, startDate, endDate, scheduleId, numberOfMonths, createdBy,createdDate,modifiedDate,modifiedBy) VALUES(",
    "#{name}, #{description}, #{startDate}, #{endDate}, #{scheduleId}, #{numberOfMonths},#{createdBy},COALESCE(#{createdDate},NOW()),COALESCE(#{modifiedDate},NOW()),#{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  Integer insert(ProcessingPeriod period);

  @Select("SELECT * FROM processing_periods WHERE scheduleId = #{scheduleId} ORDER BY startDate DESC LIMIT 1")
  ProcessingPeriod getLastAddedProcessingPeriod(Long scheduleId);

  @Delete("DELETE FROM processing_periods WHERE id = #{id} ")
  void delete(Long id);

  @Select("SELECT * FROM processing_periods WHERE id = #{id}")
  ProcessingPeriod getById(Long id);

  @Select("SELECT id FROM processing_periods WHERE name = #{name}")
  Long getIdByName(String name);

  @Select("SELECT * FROM processing_periods " +
    "WHERE scheduleId = #{scheduleId} " +
    "AND startDate > (SELECT pp.endDate FROM processing_periods pp WHERE pp.id = #{startingPeriodId}) " +
    "AND startDate <= #{beforeDate} " +
    "AND endDate >= #{afterDate} " +
    "ORDER BY startDate")
  List<ProcessingPeriod> getAllPeriodsAfterDateAndPeriod(@Param(value = "scheduleId") Long scheduleId,
                                                         @Param(value = "startingPeriodId") Long startingPeriodId,
                                                         @Param(value = "afterDate") Date afterDate,
                                                         @Param(value = "beforeDate") Date beforeDate);

  @Select({"SELECT * FROM processing_periods",
    "WHERE scheduleId = #{scheduleId}",
    "AND endDate >= #{afterDate}",
    "AND startDate <= #{beforeDate}",
    "ORDER BY startDate"})
  List<ProcessingPeriod> getAllPeriodsAfterDate(@Param(value = "scheduleId") Long scheduleId,
                                                @Param(value = "afterDate") Date afterDate,
                                                @Param(value = "beforeDate") Date beforeDate);


  @Select({"SELECT * FROM processing_periods pp INNER JOIN processing_periods cp",
    "ON DATE(pp.endDate) =  (DATE(cp.startDate)-1)",
    "WHERE pp.scheduleId = cp.scheduleId",
    "AND cp.id = #{id}",
    "AND pp.id <> cp.id"})
  ProcessingPeriod getImmediatePreviousPeriodFor(ProcessingPeriod period);


  @Select({"SELECT * FROM processing_periods WHERE scheduleId = #{scheduleId} ",
    "AND (( startDate<=#{startDate} AND endDate>=#{startDate}) OR (startDate<=#{endDate} AND endDate>=#{endDate})",
    "OR (startDate>=#{startDate} AND endDate<=#{endDate}))"})
  List<ProcessingPeriod> getAllPeriodsForDateRange(@Param("scheduleId") Long scheduleId,
                                                   @Param("startDate") Date startDate,
                                                   @Param("endDate") Date endDate);

  @Select({"SELECT * FROM processing_periods",
    "WHERE scheduleId = #{scheduleId}",
    "AND startDate <= COALESCE(#{beforeDate}, NOW())",
    "ORDER BY startDate DESC"})
  List<ProcessingPeriod> getAllPeriodsBefore(@Param("scheduleId")Long scheduleId, @Param("beforeDate")Date beforeDate);

  @Select("SELECT * FROM processing_periods WHERE scheduleId = #{scheduleId} AND extract('year' from startdate) = #{year}  ORDER BY startDate DESC")
  List<ProcessingPeriod> getAllPeriodsForScheduleAndYear(@Param("scheduleId")Long scheduleId, @Param("year") Long year);

}
