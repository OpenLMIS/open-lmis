package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.moz.ProgramDataColumn;
import org.openlmis.core.domain.moz.SupplementalProgram;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramDataColumnMapper {
  @Select("SELECT * FROM program_data_columns WHERE code=#{code}")
  @Results({
      @Result(
          property = "supplementalProgram", column = "supplementalProgramId", javaType = SupplementalProgram.class,
          many = @Many(select = "org.openlmis.core.repository.mapper.SupplementalProgramMapper.getById"))
  })
  ProgramDataColumn getColumnByCode(String code);

  @Select("SELECT * FROM program_data_columns WHERE id=#{id}")
  @Results({
      @Result(
          property = "supplementalProgram", column = "supplementalProgramId", javaType = SupplementalProgram.class,
          many = @Many(select = "org.openlmis.core.repository.mapper.SupplementalProgramMapper.getById"))
  })
  ProgramDataColumn getColumnById(Long id);

  @Select("SELECT * FROM program_data_columns")
  List<ProgramDataColumn> getAll();
}
