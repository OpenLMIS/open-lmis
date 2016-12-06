package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.moz.ProgramDataForm;
import org.openlmis.core.domain.moz.SupplementalProgram;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramDataMapper {
  @Insert("INSERT INTO program_data_forms (facilityId, supplementalProgramId, startDate, endDate, submittedTime, createdBy, modifiedBy, createdDate, modifiedDate) " +
      "VALUES (#{facility.id}, #{supplementalProgram.id}, #{startDate}, #{endDate}, #{submittedTime}, #{createdBy}, #{modifiedBy}, NOW(), NOW())")
  @Options(useGeneratedKeys = true)
  void insert(ProgramDataForm programDataForm);

  @Select("SELECT * FROM program_data_forms WHERE facilityId = #{facilityId}")
  @Results({
      @Result(
          property = "facility", column = "facilityId", javaType = Facility.class,
          many = @Many(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById")),
      @Result(
          property = "supplementalProgram", column = "supplementalProgramId", javaType = SupplementalProgram.class,
          many = @Many(select = "org.openlmis.core.repository.mapper.SupplementalProgramMapper.getById")),
      @Result(property = "programDataItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.core.repository.mapper.ProgramDataItemMapper.getByFormId"))
  })
  List<ProgramDataForm> getByFacilityId(Long facilityId);
}
