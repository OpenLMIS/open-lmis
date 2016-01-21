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
import org.openlmis.core.domain.ProcessingPeriod;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * ProcessingPeriodMapper maps the ProcessingPeriod entity to corresponding representation in database. Apart from basic
 * CRUD provides operations like getting all periods falling on, before after a date/date range, getting "n" previous
 * periods of a given period etc.
 */
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

  @Select({"SELECT * FROM processing_periods WHERE scheduleId = #{scheduleId} ",
    "AND (( startDate<=#{startDate} AND endDate>=#{startDate}) OR (startDate<=#{endDate} AND endDate>=#{endDate})",
    "OR (startDate>=#{startDate} AND endDate<=#{endDate}))"})
  List<ProcessingPeriod> getAllPeriodsForDateRange(@Param("scheduleId") Long scheduleId,
                                                   @Param("startDate") Date startDate,
                                                   @Param("endDate") Date endDate);

  @Select({"SELECT * FROM processing_periods WHERE id in ( select periodId from requisitions where programId  = #{programId} and facilityId = #{facilityId}) ",
      "AND (( startDate<=#{startDate} AND endDate>=#{startDate}) OR (startDate<=#{endDate} AND endDate>=#{endDate})",
      "OR (startDate>=#{startDate} AND endDate<=#{endDate}))"})
  List<ProcessingPeriod> getRnrPeriodsForDateRange(@Param("facilityId") Long facilityId,
                                                   @Param("programId") Long programId,
                                                   @Param("startDate") Date startDate,
                                                   @Param("endDate") Date endDate
                                                   );

  @Select({"SELECT * FROM processing_periods where id in " +
                                                  " ( select periodId from requisitions where facilityId = #{facilityId} and programId = #{programId} and emergency = false and status in ( 'INITIATED' , 'SUBMITTED' ) ) " +
                                                 " "})
  List<ProcessingPeriod> getOpenPeriods(@Param("facilityId") Long facilityId, @Param("programId") Long programId, @Param("startPeriodId") Long startPeriodId);


  @Select({"SELECT * FROM processing_periods",
    "WHERE scheduleId = #{scheduleId}",
    "AND startDate <= COALESCE(#{beforeDate}, NOW())",
    "ORDER BY startDate DESC"})
  List<ProcessingPeriod> getAllPeriodsBefore(@Param("scheduleId") Long scheduleId, @Param("beforeDate") Date beforeDate);

  @Select({"SELECT * FROM processing_periods WHERE scheduleId = #{scheduleId} AND startDate<=NOW() AND endDate>= #{programStartDate} AND endDate>=NOW()"})
  ProcessingPeriod getCurrentPeriod(@Param("scheduleId") Long scheduleId, @Param("programStartDate") Date programStartDate);

  @Select({"SELECT * FROM processing_periods WHERE scheduleId = #{currentPeriod.scheduleId}",
    "AND startDate < #{currentPeriod.startDate}",
    "ORDER BY startDate DESC LIMIT #{n}"})
  List<ProcessingPeriod> getNPreviousPeriods(@Param("currentPeriod") ProcessingPeriod currentPeriod, @Param("n") Integer n);


  @Select({"SELECT * FROM processing_periods ",
    "WHERE scheduleId = #{scheduleId} ",
    "AND startDate <= #{date} ",
    "AND endDate > #{date}"
  })
  ProcessingPeriod getPeriodForDate(@Param("scheduleId") Long scheduleId, @Param("date") Date date);

  @Select("SELECT * FROM processing_periods WHERE scheduleId = #{scheduleId} AND extract('year' from startdate) = #{year}  ORDER BY startDate DESC")
  List<ProcessingPeriod> getAllPeriodsForScheduleAndYear(@Param("scheduleId")Long scheduleId, @Param("year") Long year);

}
