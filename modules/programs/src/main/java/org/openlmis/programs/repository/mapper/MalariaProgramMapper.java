package org.openlmis.programs.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.openlmis.programs.domain.malaria.MalariaProgram;
import org.springframework.stereotype.Repository;

@Repository
public interface MalariaProgramMapper {
    @Insert("INSERT INTO malaria_programs (username, reporteddate, periodstartdate, periodenddate)" +
            "VALUES (#{username}, #{reportedDate}, #{periodStartDate}, #{periodEndDate})")
    @Options(useGeneratedKeys=true)
    int insert(MalariaProgram malariaProgram);
}


