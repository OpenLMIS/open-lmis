package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.moz.ProgramDataColumn;
import org.openlmis.core.domain.moz.ProgramDataItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramDataItemMapper {
  @Insert("INSERT INTO program_data_items (formId, name, programDataColumnId, value, createdBy, modifiedBy, createdDate, modifiedDate) " +
      "VALUES (#{programDataForm.id}, #{name}, #{programDataColumn.id}, #{value}, #{createdBy}, #{modifiedBy}, NOW(), NOW())")
  @Options(useGeneratedKeys = true)
  void insert(ProgramDataItem programDataItem);

  @Select("SELECT * FROM program_data_items WHERE formId = #{formId}")
  @Results({
      @Result(
          property = "programDataColumn", column = "programDataColumnId", javaType = ProgramDataColumn.class,
          many = @Many(select = "org.openlmis.core.repository.mapper.ProgramDataColumnMapper.getColumnById"))
  })
  List<ProgramDataItem> getByFormId(Long formId);
}
