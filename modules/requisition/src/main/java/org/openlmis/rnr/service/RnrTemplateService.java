/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.service;

import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.repository.RnrTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Exposes the services for handling RnrColumn and ProgramRnrTemplate entity.
 */

@Service
public class RnrTemplateService {

  @Autowired
  private RnrTemplateRepository rnrRepository;

  @Autowired
  private ProgramService programService;

  @Autowired
  private ConfigurationSettingService configService;



  public List<RnrColumn> fetchAllRnRColumns(Long programId) {
    return rnrRepository.fetchRnrTemplateColumnsOrMasterColumns(programId);
  }

  @Transactional
  public Map<String, OpenLmisMessage> saveRnRTemplateForProgram(ProgramRnrTemplate programTemplate) {
    Map<String, OpenLmisMessage> errors = programTemplate.validateToSave();

    if (errors.isEmpty()) {
      rnrRepository.saveProgramRnrTemplate(programTemplate);
      programService.setTemplateConfigured(programTemplate.getProgramId());
    }

    return errors;
  }

  public List<RnrColumn> fetchColumnsForRequisition(Long programId) {
    return rnrRepository.fetchColumnsForRequisition(programId);
  }

  public ProgramRnrTemplate fetchProgramTemplate(Long programId) {
    return new ProgramRnrTemplate(programId, fetchAllRnRColumns(programId));
  }

  public ProgramRnrTemplate fetchProgramTemplateForRequisition(Long programId) {
    ProgramRnrTemplate template =  new ProgramRnrTemplate(programId ,fetchColumnsForRequisition(programId));
    // read if system should populate 0 or not
    template.setApplyDefaultZero(Boolean.parseBoolean(configService.getConfigurationStringValue("DEFAULT_ZERO") ));
    return template;
  }
}
