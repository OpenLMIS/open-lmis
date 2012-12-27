package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Schedule;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleMapper {

    @Select("SELECT id FROM processing_schedules WHERE LOWER(code) = LOWER(#{code})")
    Integer getIdForCode(String code);

    @Insert("INSERT INTO processing_schedules" +
            "(code, name, description) " +
            "VALUES(#{name}, #{code}, #{description})")
    @Options(useGeneratedKeys = true)
    Integer insert(Schedule schedule);

    @Select("SELECT * FROM processing_schedules")
    List<Schedule> getAll();
}
