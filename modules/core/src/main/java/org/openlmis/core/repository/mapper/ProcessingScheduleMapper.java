package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.ProcessingSchedule;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessingScheduleMapper {

    @Select("SELECT id FROM processing_schedules WHERE LOWER(code) = LOWER(#{code})")
    Integer getIdForCode(String code);

    @Insert("INSERT INTO processing_schedules" +
            "(code, name, description) " +
            "VALUES(#{code}, #{name}, #{description})")
    @Options(useGeneratedKeys = true)
    Integer insert(ProcessingSchedule schedule);

    @Select("SELECT * FROM processing_schedules")
    List<ProcessingSchedule> getAll();

    @Update("UPDATE processing_schedules set code = #{code}, name = #{name}, description = #{description}, " +
            "modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} " +
            "where id = #{id}")
    Integer update(ProcessingSchedule schedule);

    @Select("SELECT * FROM processing_schedules where id = #{id}")
    ProcessingSchedule get(Integer id);
}
