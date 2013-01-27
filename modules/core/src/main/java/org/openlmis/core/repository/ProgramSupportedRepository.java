package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
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
    try {
      mapper.addSupportedProgram(programSupported);
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException("Facility has already been mapped to the program");
    } catch (DataIntegrityViolationException integrityViolationException) {
      throw new DataException("Invalid reference data 'Program Code'");
    }
  }

  public void addSupportedProgramsFor(Facility facility) {
    List<Program> supportedPrograms = facility.getSupportedPrograms();
    for (Program supportedProgram : supportedPrograms) {
      ProgramSupported programSupported = new ProgramSupported(facility.getId(), supportedProgram.getId(),
          supportedProgram.getActive(), new DateTime().toDate(), facility.getModifiedDate(), facility.getModifiedBy());
      programSupported.setModifiedDate(DateTime.now().toDate());
      programSupported.setStartDate(DateTime.now().toDate());
      addSupportedProgram(programSupported);
    }
  }

  public void updateSupportedPrograms(Facility facility, List<Program> previouslySupportedPrograms) {
    deleteObsoleteProgramMappings(facility, previouslySupportedPrograms);
    addUpdatableProgramMappings(facility, previouslySupportedPrograms);
  }

  private void deleteObsoleteProgramMappings(Facility facility, List<Program> previouslySupportedPrograms) {
    List<Program> supportedPrograms = facility.getSupportedPrograms();
    for (Program previouslySupportedProgram : previouslySupportedPrograms) {
      if (!(supportedPrograms.contains(previouslySupportedProgram))) {
        deleteSupportedPrograms(facility.getId(), previouslySupportedProgram.getId());
      }
    }
  }

  private void addUpdatableProgramMappings(Facility facility, List<Program> previouslySupportedPrograms) {
    for (Program supportedProgram : facility.getSupportedPrograms()) {
      if (!(previouslySupportedPrograms).contains(supportedProgram)) {
        ProgramSupported newProgramsSupported = new ProgramSupported(facility.getId(), supportedProgram.getId(),
            supportedProgram.getActive(), null, facility.getModifiedDate(), facility.getModifiedBy());
        newProgramsSupported.setModifiedDate(DateTime.now().toDate());
        newProgramsSupported.setStartDate(DateTime.now().toDate());
        addSupportedProgram(newProgramsSupported);
      }
    }
  }
}
