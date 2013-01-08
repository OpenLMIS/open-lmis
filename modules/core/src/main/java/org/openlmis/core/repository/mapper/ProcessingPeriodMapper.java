package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.ProcessingPeriod;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessingPeriodMapper {

  @Select("SELECT * FROM processing_periods WHERE scheduleId = #{scheduleId} ORDER BY startDate DESC")
  public List<ProcessingPeriod> getAll(int scheduleId);

  @Insert({"INSERT INTO processing_periods",
      "(name, description, startDate, endDate, scheduleId, numberOfMonths, modifiedBy, modifiedDate) VALUES(",
      "#{name}, #{description}, #{startDate}, #{endDate}, #{scheduleId}, #{numberOfMonths}, #{modifiedBy}, DEFAULT)"})
  @Options(useGeneratedKeys = true)
  public Integer insert(ProcessingPeriod period);

  @Select("SELECT * FROM processing_periods WHERE scheduleId = #{scheduleId} ORDER BY startDate DESC LIMIT 1")
  ProcessingPeriod getLastAddedProcessingPeriod(Integer scheduleId);
}
