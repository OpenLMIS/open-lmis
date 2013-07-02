package org.openlmis.core.service;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgramSupportedService {

  @Autowired
  ProgramSupportedRepository repository;

  public List<ProgramSupported> getAllByFacilityId(Long facilityId) {
    return repository.getAllByFacilityId(facilityId);
  }

  public void addSupportedProgram(ProgramSupported programSupported) {
    repository.addSupportedProgram(programSupported);
  }

  public void addSupportedProgramsFor(Facility facility) {
    repository.addSupportedProgramsFor(facility);
  }

  public void updateSupportedPrograms(Facility facility, List<ProgramSupported> programsForFacility) {
    repository.updateSupportedPrograms(facility, programsForFacility);
  }

  public ProgramSupported getByFacilityIdAndProgramId(Long facilityId, Long programId) {
    return repository.getByFacilityIdAndProgramId(facilityId, programId);
  }

  public void updateSupportedProgram(ProgramSupported programSupported) {
    repository.updateSupportedProgram(programSupported);
  }
}
