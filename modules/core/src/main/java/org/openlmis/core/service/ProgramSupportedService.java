package org.openlmis.core.service;

import org.apache.log4j.Logger;
import org.ict4h.atomfeed.server.service.EventService;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.dto.ProgramSupportedEventDTO;
import org.openlmis.core.event.ProgramSupportedEvent;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.ArrayList;
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
  EventService eventService;


  @Autowired
  FacilityProgramProductService facilityProgramProductService;

  Logger logger = Logger.getLogger(ProgramSupportedService.class);


  public List<ProgramSupported> getAllByFacilityId(Long facilityId) {
    return repository.getAllByFacilityId(facilityId);
  }

  public void updateSupportedPrograms(Facility facility) {
    Facility facilityForNotification = cloneFacility(facility);
    repository.updateSupportedPrograms(facility);
    notifyProgramSupportedUpdated(facilityForNotification);
  }

  private Facility cloneFacility(Facility facility) {
    Facility facilityForNotification = new Facility();
    facilityForNotification.setCode(facility.getCode());
    ArrayList<ProgramSupported> supportedPrograms = new ArrayList<>();
    for (ProgramSupported programSupported : facility.getSupportedPrograms()) {
      supportedPrograms.add(programSupported);
    }
    facilityForNotification.setSupportedPrograms(supportedPrograms);
    return facilityForNotification;
  }

  public ProgramSupported getFilledByFacilityIdAndProgramId(Long facilityId, Long programId) {
    ProgramSupported programSupported = repository.getByFacilityIdAndProgramId(facilityId, programId);
    programSupported.setProgramProducts(facilityProgramProductService.getForProgramAndFacility(programId, facilityId));
    return programSupported;
  }

  public ProgramSupported getByFacilityIdAndProgramId(Long facilityId, Long programId) {
    return repository.getByFacilityIdAndProgramId(facilityId, programId);
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
      repository.addSupportedProgram(programSupported);
    } else {
      repository.updateSupportedProgram(programSupported);
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

  public void notifyProgramSupportedUpdated(Facility facility) {
    try {
      ProgramSupportedEventDTO programSupportedEventDTO = new ProgramSupportedEventDTO(
        facility.getCode(), facility.getSupportedPrograms());
      eventService.notify(new ProgramSupportedEvent(programSupportedEventDTO));
    } catch (URISyntaxException e) {
      logger.error("Failed to generate program supported event feed", e);
    }
  }
}
