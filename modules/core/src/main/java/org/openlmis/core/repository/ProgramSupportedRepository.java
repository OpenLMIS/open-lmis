package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;

@NoArgsConstructor
@Repository
public class ProgramSupportedRepository {

  private ProgramSupportedMapper mapper;

  @Autowired
  public ProgramSupportedRepository(ProgramSupportedMapper programSupportedMapper) {
    this.mapper = programSupportedMapper;
  }

  public Date getProgramStartDate(Integer facilityId, Integer programId) {
    return mapper.getBy(facilityId, programId).getStartDate();
  }

  public void deleteSupportedPrograms(Integer facilityId, Integer programId) {
    mapper.delete(facilityId, programId);
  }

  public void addSupportedProgram(ProgramSupported programSupported) {
    mapper.addSupportedProgram(programSupported);
  }
}
