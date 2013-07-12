package org.openlmis.core.service;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgramSupportedService {

  @Autowired
  ProgramSupportedRepository repository;

  @Autowired
  ProgramService programService;

  @Autowired
  FacilityService facilityService;

  @Autowired
  FacilityProgramProductService facilityProgramProductService;

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

  public ProgramSupported getFilledByFacilityIdAndProgramId(Long facilityId, Long programId) {
    ProgramSupported programSupported = repository.getByFacilityIdAndProgramId(facilityId, programId);
    programSupported.setProgramProducts(facilityProgramProductService.getForProgramAndFacility(programId, facilityId));
    return programSupported;
  }

  public ProgramSupported getByFacilityIdAndProgramId(Long facilityId, Long programId) {
    return repository.getByFacilityIdAndProgramId(facilityId, programId);
  }

  public void updateSupportedProgram(ProgramSupported programSupported) {
    repository.updateSupportedProgram(programSupported);
  }

  public void uploadSupportedProgram(ProgramSupported programSupported) {
    programSupported.isValid();

    Facility facility = new Facility();
    facility.setCode(programSupported.getFacilityCode());
    facility = facilityService.getByCode(facility);
    programSupported.setFacilityId(facility.getId());
    Program program = programService.getByCode(programSupported.getProgram().getCode());
    programSupported.setProgram(program);

    if (programSupported.getId() == null) {
      addSupportedProgram(programSupported);
    } else {
      updateSupportedProgram(programSupported);
    }
  }

  public ProgramSupported getProgramSupported(ProgramSupported programSupported) {
    Facility facility = getFacility(programSupported);

    Program program = getProgram(programSupported);

    return getByFacilityIdAndProgramId(facility.getId(), program.getId());
  }

  private Program getProgram(ProgramSupported programSupported) {
    Program program = programService.getByCode(programSupported.getProgram().getCode());

    if (program == null)
      throw new DataException("program.code.invalid");
    return program;
  }

  private Facility getFacility(ProgramSupported programSupported) {
    Facility facility = new Facility();
    facility.setCode(programSupported.getFacilityCode());
    facility = facilityService.getByCode(facility);

    if (facility == null)
      throw new DataException("error.facility.code.invalid");
    return facility;
  }
}
