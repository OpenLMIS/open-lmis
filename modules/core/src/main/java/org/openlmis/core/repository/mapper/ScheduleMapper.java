package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Schedule;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleMapper {

    @Select("SELECT id FROM schedules WHERE LOWER(code) = LOWER(#{code})")
    Integer getIdForCode(String code);

    @Insert("INSERT INTO schedules" +
            "(code, name, description) " +
            "VALUES(#{name}, #{code}, #{description})")
    @Options(useGeneratedKeys = true)
    Integer insert(Schedule schedule);

    @Select("SELECT * FROM schedules")
    List<Schedule> getAll();
}
