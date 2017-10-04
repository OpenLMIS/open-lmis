package org.openlmis.programs.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.openlmis.programs.domain.malaria.Implementation;
import org.springframework.stereotype.Repository;

@Repository
public interface ImplementationMapper {
    @Insert("INSERT INTO implementations (executor, malariaprogramid) VALUES (#{executor}, #{malariaProgram.id})")
    @Options(useGeneratedKeys=true)
    int insert(Implementation implementation);
}
