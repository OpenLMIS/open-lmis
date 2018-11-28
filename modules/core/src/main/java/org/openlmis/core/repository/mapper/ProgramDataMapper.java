package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Signature;
import org.openlmis.core.domain.moz.ProgramDataForm;
import org.openlmis.core.domain.moz.SupplementalProgram;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramDataMapper {
  @Insert("INSERT INTO program_data_forms (facilityId, supplementalProgramId, startDate, endDate, submittedTime, createdBy, modifiedBy, createdDate, modifiedDate, observation) " +
      "VALUES (#{facility.id}, #{supplementalProgram.id}, #{startDate}, #{endDate}, #{submittedTime}, #{createdBy}, #{modifiedBy}, NOW(), NOW(), #{observation})")
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
          many = @Many(select = "org.openlmis.core.repository.mapper.ProgramDataItemMapper.getByFormId")),
      @Result(property = "programDataFormSignatures", column = "id", javaType = List.class,
          many = @Many(select = "org.openlmis.core.repository.mapper.ProgramDataMapper.getSignaturesByFormId")),
      @Result(property = "programDataFormBasicItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.core.repository.mapper.ProgramDataFormBasicItemMapper.getByFormId"))
  })
  List<ProgramDataForm> getByFacilityId(Long facilityId);

  @Insert("INSERT INTO program_data_form_signatures(signatureId, programDataFormId) VALUES " +
      "(#{signature.id}, #{form.id})")
  void insertProgramDataFormSignature(@Param("form") ProgramDataForm form, @Param("signature") Signature signature);

  @Select("SELECT * FROM program_data_form_signatures " +
      "JOIN signatures " +
      "ON signatures.id = program_data_form_signatures.signatureId " +
      "WHERE program_data_form_signatures.programDataFormId = #{formId} ")
  List<Signature> getSignaturesByFormId(@Param("formId") Long formId);
}
