package org.openlmis.core.repository;

import org.openlmis.core.domain.Signature;
import org.openlmis.core.domain.moz.ProgramDataForm;
import org.openlmis.core.domain.moz.ProgramDataFormBasicItem;
import org.openlmis.core.domain.moz.ProgramDataItem;
import org.openlmis.core.repository.mapper.ProgramDataFormBasicItemMapper;
import org.openlmis.core.repository.mapper.ProgramDataItemMapper;
import org.openlmis.core.repository.mapper.ProgramDataMapper;
import org.openlmis.core.repository.mapper.SignatureMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProgramDataRepository {

  @Autowired
  ProgramDataMapper programDataMapper;

  @Autowired
  ProgramDataItemMapper programDataItemMapper;

  @Autowired
  ProgramDataFormBasicItemMapper programDataFormBasicItemMapper;

  @Autowired
  SignatureMapper signatureMapper;

  public void createProgramDataForm(ProgramDataForm programDataForm) {
    programDataMapper.insert(programDataForm);
    for (ProgramDataItem programDataItem : programDataForm.getProgramDataItems()) {
      programDataItemMapper.insert(programDataItem);
    }
    if (null != programDataForm.getProgramDataFormBasicItems()) {
      for (ProgramDataFormBasicItem programDataFormBasicItem : programDataForm.getProgramDataFormBasicItems()) {
        programDataFormBasicItemMapper.insert(programDataFormBasicItem);
      }
    }
    for (Signature signature : programDataForm.getProgramDataFormSignatures()) {
      signatureMapper.insertSignature(signature);
      programDataMapper.insertProgramDataFormSignature(programDataForm, signature);
    }
  }

  public List<ProgramDataForm> getProgramDataFormsByFacilityId(Long facilityId) {
    return programDataMapper.getByFacilityId(facilityId);
  }
}
