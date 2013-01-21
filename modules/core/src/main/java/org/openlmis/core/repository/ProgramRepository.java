package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;
import org.openlmis.core.repository.mapper.RoleRightsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.openlmis.core.domain.Right.getCommaSeparatedRightNames;

@Component
@NoArgsConstructor
public class ProgramRepository {

  private ProgramMapper programMapper;
  private ProgramSupportedMapper programSupportedMapper;
  private RoleRightsMapper roleRightsMapper;

  public static String PROGRAM_CODE_INVALID = "program.code.invalid";

  @Autowired
  public ProgramRepository(ProgramMapper programMapper, ProgramSupportedMapper programSupportedMapper, RoleRightsMapper roleRightsMapper) {
    this.programMapper = programMapper;
    this.programSupportedMapper = programSupportedMapper;
    this.roleRightsMapper = roleRightsMapper;
  }

  public List<Program> getAllActive() {
    return programMapper.getAllActive();
  }

  public List<Program> getByFacility(Integer facilityId) {
    return programMapper.getActiveByFacility(facilityId);
  }

  public List<Program> getAll() {
    return programMapper.getAll();
  }

  public List<Program> getUserSupervisedActiveProgramsWithRights(Integer userId, Right... rights) {
    return programMapper.getUserSupervisedActivePrograms(userId, getCommaSeparatedRightNames(rights));
  }

  public List<Program> getProgramsSupportedByFacilityForUserWithRight(Integer facilityId, Integer userId, Right... rights) {
    return programMapper.getProgramsSupportedByFacilityForUserWithRight(facilityId, userId, getCommaSeparatedRightNames(rights));
  }

  public Integer getIdByCode(String code) {
    Integer programId = programMapper.getIdForCode(code);

    if (programId == null) {
      throw new DataException(new OpenLmisMessage(PROGRAM_CODE_INVALID));
    }

    return programId;
  }

  public List<Program> getActiveProgramsForUserWithRights(Integer userId, Right... rights) {
    return programMapper.getActiveProgramsForUserWithRights(userId, getCommaSeparatedRightNames(rights));
  }
}
