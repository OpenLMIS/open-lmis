package org.openlmis.core.upload;

import org.joda.time.DateTime;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.service.FacilityService;
import org.openlmis.upload.Importable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("programSupportedPersistenceHandler")
public class ProgramSupportedPersistenceHandler extends AbstractModelPersistenceHandler {

  private FacilityService facilityService;

  @Autowired
  public ProgramSupportedPersistenceHandler(FacilityService facilityService) {
    this.facilityService = facilityService;
  }

  @Override
  protected void save(Importable importable, String modifiedBy) {
    ProgramSupported programSupported = (ProgramSupported) importable;
    programSupported.setModifiedBy(modifiedBy);
    programSupported.setStartDate(new DateTime().toDate());
    facilityService.uploadSupportedProgram(programSupported);
  }
}
