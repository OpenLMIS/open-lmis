package org.openlmis.restapi.service;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.moz.ProgramDataColumn;
import org.openlmis.core.domain.moz.ProgramDataForm;
import org.openlmis.core.domain.moz.ProgramDataItem;
import org.openlmis.core.domain.moz.SupplementalProgram;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProgramDataRepository;
import org.openlmis.core.repository.SyncUpHashRepository;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProgramDataColumnMapper;
import org.openlmis.core.repository.mapper.SupplementalProgramMapper;
import org.openlmis.restapi.domain.ProgramDataFormDTO;
import org.openlmis.restapi.domain.ProgramDataFormItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class RestProgramDataService {

  @Autowired
  private SupplementalProgramMapper supplementalProgramMapper;

  @Autowired
  private ProgramDataRepository programDataRepository;

  @Autowired
  private FacilityMapper facilityMapper;

  @Autowired
  private ProgramDataColumnMapper programDataColumnMapper;

  @Autowired
  private SyncUpHashRepository syncUpHashRepository;

  public void createProgramDataForm(ProgramDataFormDTO requestBodyData, long userId) {
    if (syncUpHashRepository.hashExists(requestBodyData.getSyncUpHash())) {
      return;
    }

    Facility facility = facilityMapper.getById(requestBodyData.getFacilityId());
    if (facility == null) {
      throw new DataException("error.facility.unknown");
    }

    ProgramDataForm programDataForm = convertRequestBodyDataToProgramDataForm(requestBodyData, userId, facility);

    programDataRepository.createProgramDataForm(programDataForm);
    syncUpHashRepository.save(requestBodyData.getSyncUpHash());
  }

  public ProgramDataForm convertRequestBodyDataToProgramDataForm(ProgramDataFormDTO requestBodyData, long userId, Facility facility) {
    SupplementalProgram supplementalProgram = supplementalProgramMapper.getSupplementalProgramByCode(requestBodyData.getProgramCode());
    ProgramDataForm programDataForm = new ProgramDataForm(facility, supplementalProgram, requestBodyData.getPeriodBegin(),
        requestBodyData.getPeriodEnd(), requestBodyData.getSubmittedTime(), new ArrayList<ProgramDataItem>());
    programDataForm.setCreatedBy(userId);
    programDataForm.setModifiedBy(userId);

    programDataForm.setProgramDataItems(new ArrayList<ProgramDataItem>());
    for (ProgramDataFormItemDTO programDataFormItemDTO : requestBodyData.getProgramDataFormItems()) {
      ProgramDataColumn programDataColumn = programDataColumnMapper.getColumnByCode(programDataFormItemDTO.getColumnCode());
      if (programDataColumn == null) {
        throw new DataException("error.wrong.program.column");
      }
      ProgramDataItem programDataItem = new ProgramDataItem(programDataForm, programDataFormItemDTO.getName(),
          programDataColumn, programDataFormItemDTO.getValue());
      programDataForm.getProgramDataItems().add(programDataItem);
    }
    return programDataForm;
  }
}
