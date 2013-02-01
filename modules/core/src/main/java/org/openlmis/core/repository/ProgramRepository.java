package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.openlmis.core.domain.Right.getCommaSeparatedRightNames;

@Component
@NoArgsConstructor
public class ProgramRepository {

  private ProgramMapper mapper;

  public static String PROGRAM_CODE_INVALID = "program.code.invalid";

  @Autowired
  public ProgramRepository(ProgramMapper programMapper) {
    this.mapper = programMapper;
  }

  public List<Program> getAllActive() {
    return mapper.getAllActive();
  }

  public List<Program> getByFacility(Integer facilityId) {
    return mapper.getByFacilityId(facilityId);
  }

  public List<Program> getAll() {
    return mapper.getAll();
  }

  public List<Program> getUserSupervisedActiveProgramsWithRights(Integer userId, Right... rights) {
    return mapper.getUserSupervisedActivePrograms(userId, getCommaSeparatedRightNames(rights));
  }

  public List<Program> getProgramsSupportedByFacilityForUserWithRight(Integer facilityId, Integer userId, Right... rights) {
    return mapper.getProgramsSupportedByFacilityForUserWithRight(facilityId, userId, getCommaSeparatedRightNames(rights));
  }

  public Integer getIdByCode(String code) {
    Integer programId = mapper.getIdForCode(code);

    if (programId == null) {
      throw new DataException(new OpenLmisMessage(PROGRAM_CODE_INVALID));
    }

    return programId;
  }

  public List<Program> getActiveProgramsForUserWithRights(Integer userId, Right... rights) {
    return mapper.getActiveProgramsForUserWithRights(userId, getCommaSeparatedRightNames(rights));
  }

  public Program getById(Integer id) {
    return mapper.getById(id);
  }
}
