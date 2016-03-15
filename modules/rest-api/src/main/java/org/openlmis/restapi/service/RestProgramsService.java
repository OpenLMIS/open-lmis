package org.openlmis.restapi.service;

import org.openlmis.core.domain.Program;
import org.openlmis.core.repository.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RestProgramsService {

  @Autowired
  private ProgramRepository programRepository;

  @Transactional
  public void associate(Long parentProgramId, List<String> programCodes) {
    for (String programCode: programCodes) {
      programRepository.associateParent(parentProgramId, programCode);
    }
  }
}
