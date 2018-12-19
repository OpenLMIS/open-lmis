package org.openlmis.restapi.service;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.domain.Regimen;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.ProgramSupportedRepository;
import org.openlmis.core.repository.RegimenRepository;
import org.openlmis.restapi.domain.ProgramWithRegimens;
import org.openlmis.restapi.domain.RegimenForRest;
import org.openlmis.restapi.domain.RegimenLineItemForRest;
import org.openlmis.restapi.domain.Report;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestProgramsService {

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private ProgramSupportedRepository programSupportedRepository;

  @Autowired
  private RegimenRepository regimenRepository;

  @Transactional
  public void associate(Long parentProgramId, List<String> programCodes) {
    for (String programCode: programCodes) {
      programRepository.associateParent(parentProgramId, programCode);
    }
  }

  @Transactional
  public List<ProgramWithRegimens> getAllProgramWithRegimenByFacilityId(Long facilityId) {
    List<ProgramWithRegimens> programWithRegimensList = new ArrayList<>();

    List<ProgramSupported> programSupporteds = programSupportedRepository.getAllByFacilityId(facilityId);

    for (ProgramSupported programSupported: programSupporteds) {
      Program program = programRepository.getProgramWithParentById(programSupported.getProgram().getId());
      ProgramWithRegimens programWithRegimens = createProgramWithRegimens(program);
      programWithRegimensList.add(programWithRegimens);
    }
    return programWithRegimensList;
  }

  private ProgramWithRegimens createProgramWithRegimens(Program program) {
    ProgramWithRegimens programWithRegimens = new ProgramWithRegimens();

    programWithRegimens.setId(program.getId());
    programWithRegimens.setCode(program.getCode());
    programWithRegimens.setName(program.getName());
    programWithRegimens.setParentCode(program.getParent() != null ? program.getParent().getCode() : null);
    programWithRegimens.setIsSupportEmergency(program.getIsSupportEmergency());
    programWithRegimens.setRegimens(getRegimensByProgramAndIsCustom(program));

    return programWithRegimens;
  }

  private List<RegimenForRest> getRegimensByProgramAndIsCustom(Program program) {
    List<RegimenForRest> result = new ArrayList<>();
    List<Regimen> regimenList = regimenRepository.getRegimensByProgramAndIsCustom(program.getId(), false);
    for (Regimen regimen: regimenList) {
      result.add(RegimenForRest.convertFromRegimenLineItem(regimen));
    }
    return result;
  }

}
