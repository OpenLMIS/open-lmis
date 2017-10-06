package org.openlmis.programs.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.openlmis.programs.domain.malaria.Implementation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImplementationMapper {
    @Insert("INSERT INTO implementations (executor, malariaprogramid) VALUES (#{executor}, #{malariaProgram.id})")
    @Options(useGeneratedKeys = true)
    int insert(Implementation implementation);

    @Insert("<script>" +
            "INSERT INTO implementations (executor, malariaprogramid) VALUES " +
            "<foreach item='implementation' collection='implementations' separator=','>" +
            "(#{implementation.executor}, #{implementation.malariaProgram.id})" +
            "</foreach>" +
            "</script>")
    int bulkInsert(@Param("implementations") List<Implementation> implementations);
}
