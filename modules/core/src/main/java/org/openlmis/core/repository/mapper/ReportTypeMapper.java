package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ReportType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportTypeMapper {

    @Select({"SELECT * FROM reports_type"})
    @Results(value = {
            @Result(property = "program", column = "programid", javaType = Program.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById"))})
    List<ReportType> getAll();

    @Insert({"INSERT INTO reports_type (code, programid, name, description) ",
            "VALUES (#{code}, #{programId}, #{name}, #{description})"})
    void insert(ReportType reportType);

    @Select("SELECT * FROM reports_type WHERE id = #{id}")
    ReportType getById(Long id);

    @Select("SELECT * FROM reports_type WHERE code = #{code}")
    @Results(value = {
            @Result(property = "program", column = "programid", javaType = Program.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.ProgramMapper.getById"))})
    ReportType getByCode(String code);

}