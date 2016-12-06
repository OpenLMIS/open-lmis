package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.moz.SupplementalProgram;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplementalProgramMapper {

  @Select("SELECT * FROM supplemental_programs WHERE code = #{code}")
  SupplementalProgram getSupplementalProgramByCode(String code);

  @Select("SELECT * FROM supplemental_programs WHERE id = #{id}")
  SupplementalProgram getById(Long id);
}
