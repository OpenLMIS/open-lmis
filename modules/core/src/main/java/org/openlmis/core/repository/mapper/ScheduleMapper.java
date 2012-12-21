package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Schedule;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleMapper {

    @Select("SELECT id FROM schedules WHERE LOWER(code) = LOWER(#{code})")
    Integer getIdForCode(String code);

    @Select("INSERT INTO schedules" +
            "(code, name, description) " +
            "VALUES(#{code}, #{name}, #{description}) returning id")
    Integer insert(Schedule schedule);
}
