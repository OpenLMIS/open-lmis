package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.openlmis.rnr.domain.Schedule;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleMapper {


    @Select("SELECT id FROM schedule WHERE LOWER(code) = LOWER(#{code})")
    Integer getIdForCode(String code);

    @Select("INSERT INTO schedule(code, name, description) VALUES(#{code}, #{name}, #{description}) returning id")
    Integer insert(Schedule schedule);
}
