package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProgramSupportedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Repository
public class ProgramSupportedRepository {

  private ProgramSupportedMapper programSupportedMapper;

  @Autowired
  public ProgramSupportedRepository(ProgramSupportedMapper programSupportedMapper) {
    this.programSupportedMapper = programSupportedMapper;
  }

  public Date getProgramStartDate(Integer facilityId, Integer programId) {
    return programSupportedMapper.getBy(facilityId, programId).getStartDate();
  }

  public void deleteSupportedPrograms(Integer facilityId, Integer programId) {
    programSupportedMapper.delete(facilityId, programId);
  }

  public void addSupportedProgram(ProgramSupported programSupported) {
    try {
      programSupportedMapper.addSupportedProgram(programSupported);
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException("Facility has already been mapped to the program");
    } catch (DataIntegrityViolationException integrityViolationException) {
      throw new DataException("Invalid reference data 'Program Code'");
    }
  }

  public void addSupportedProgramsFor(Facility facility) {
    for (ProgramSupported supportedProgram : facility.getSupportedPrograms()) {
      supportedProgram.setModifiedBy(facility.getModifiedBy());
      supportedProgram.setStartDate(DateTime.now().toDate());
      addSupportedProgram(supportedProgram);
    }
  }

  public void updateSupportedPrograms(Facility facility, List<ProgramSupported> previouslySupportedPrograms) {
    deleteObsoleteProgramMappings(facility, previouslySupportedPrograms);
    addUpdatableProgramMappings(facility, previouslySupportedPrograms);
  }

  private void deleteObsoleteProgramMappings(Facility facility, List<ProgramSupported> previouslySupportedPrograms) {
    List<ProgramSupported> supportedPrograms = facility.getSupportedPrograms();
    for (ProgramSupported previouslySupportedProgram : previouslySupportedPrograms) {
      if (!(supportedPrograms.contains(previouslySupportedProgram))) {
        deleteSupportedPrograms(facility.getId(), previouslySupportedProgram.getProgramId());
      }
    }
  }

  private void addUpdatableProgramMappings(Facility facility, List<ProgramSupported> previouslySupportedPrograms) {
    for (ProgramSupported supportedProgram : facility.getSupportedPrograms()) {
      if (!(previouslySupportedPrograms).contains(supportedProgram)) {
        supportedProgram.setModifiedBy(facility.getModifiedBy());
        supportedProgram.setStartDate(DateTime.now().toDate());
        addSupportedProgram(supportedProgram);
      }
    }
  }

  public List<ProgramSupported> getAllByFacilityId(Integer facilityId) {
    return programSupportedMapper.getAllByFacilityId(facilityId);
  }
}
