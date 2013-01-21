package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.ProcessingPeriod;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ProcessingPeriodMapper {

  @Select("SELECT * FROM processing_periods WHERE scheduleId = #{scheduleId} ORDER BY startDate DESC")
  List<ProcessingPeriod> getAll(Integer scheduleId);

  @Insert({"INSERT INTO processing_periods",
      "(name, description, startDate, endDate, scheduleId, numberOfMonths, modifiedBy, modifiedDate) VALUES(",
      "#{name}, #{description}, #{startDate}, #{endDate}, #{scheduleId}, #{numberOfMonths}, #{modifiedBy}, DEFAULT)"})
  @Options(useGeneratedKeys = true)
  Integer insert(ProcessingPeriod period);

  @Select("SELECT * FROM processing_periods WHERE scheduleId = #{scheduleId} ORDER BY startDate DESC LIMIT 1")
  ProcessingPeriod getLastAddedProcessingPeriod(Integer scheduleId);

  @Delete("DELETE FROM processing_periods WHERE id = #{id} ")
  void delete(Integer id);

  @Select("SELECT * FROM processing_periods WHERE id = #{id}")
  ProcessingPeriod getById(Integer id);

  @Select("SELECT * FROM processing_periods " +
      "WHERE scheduleId = #{scheduleId} " +
      "AND startDate > (SELECT pp.endDate FROM processing_periods pp WHERE pp.id = #{startingPeriodId}) " +
      "AND startDate <= #{beforeDate} " +
      "AND endDate >= #{afterDate} " +
      "ORDER BY startDate")
  List<ProcessingPeriod> getAllPeriodsAfterDateAndPeriod(@Param(value = "scheduleId") Integer scheduleId,
                                                         @Param(value = "startingPeriodId") Integer startingPeriodId,
                                                         @Param(value = "afterDate") Date afterDate,
                                                         @Param(value = "beforeDate") Date beforeDate);

  @Select("SELECT * FROM processing_periods " +
      "WHERE scheduleId = #{scheduleId} " +
      "AND endDate >= #{afterDate} " +
      "AND startDate <= #{beforeDate} " +
      "ORDER BY startDate")
  List<ProcessingPeriod> getAllPeriodsAfterDate(@Param(value = "scheduleId") Integer scheduleId,
                                                @Param(value = "afterDate") Date afterDate,
                                                @Param(value = "beforeDate") Date beforeDate);


}
