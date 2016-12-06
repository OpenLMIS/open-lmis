package org.openlmis.core.repository;

import org.openlmis.core.domain.moz.ProgramDataForm;
import org.openlmis.core.domain.moz.ProgramDataItem;
import org.openlmis.core.repository.mapper.ProgramDataItemMapper;
import org.openlmis.core.repository.mapper.ProgramDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProgramDataRepository {

  @Autowired
  ProgramDataMapper programDataMapper;

  @Autowired
  ProgramDataItemMapper programDataItemMapper;

  public void createProgramDataForm(ProgramDataForm programDataForm) {
    programDataMapper.insert(programDataForm);
    for (ProgramDataItem programDataItem : programDataForm.getProgramDataItems()) {
      programDataItemMapper.insert(programDataItem);
    }
  }

  public List<ProgramDataForm> getProgramDataFormsByFacilityId(Long facilityId) {
    return programDataMapper.getByFacilityId(facilityId);
  }
}
