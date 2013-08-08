package org.openlmis.distribution.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.distribution.domain.RefrigeratorReading;
import org.springframework.stereotype.Repository;

@Repository
public interface RefrigeratorReadingMapper {

  @Insert({"INSERT INTO distribution_refrigerator_readings",
    "(temperature, functioningCorrectly, lowAlarmEvents, highAlarmEvents, ",
      "problemSinceLastTime, problemList, notes, refrigeratorSerialNumber, facilityId, distributionId, createdBy, modifiedBy)",
    "VALUES",
    "(#{temperature}, #{functioningCorrectly}, #{lowAlarmEvents}, #{highAlarmEvents}, ",
      "#{problemSinceLastTime}, #{problemList}, #{notes}, #{refrigeratorSerialNumber}, #{facilityId}, #{distributionId}, #{createdBy}, #{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  void insert(RefrigeratorReading refrigeratorReading);
}
