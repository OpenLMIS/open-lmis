/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.ProgramService;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.repository.RnrTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
public class RnrTemplateService {

  @Autowired
  private RnrTemplateRepository rnrRepository;
  @Autowired
  private ProgramService programService;

  public List<RnrColumn> fetchAllRnRColumns(Integer programId) {
    return rnrRepository.fetchRnrTemplateColumnsOrMasterColumns(programId);
  }

  @Transactional
  public Map<String, OpenLmisMessage> saveRnRTemplateForProgram(ProgramRnrTemplate programTemplate) {
    Map<String, OpenLmisMessage> errors = programTemplate.validateToSave();

    if (!(errors.isEmpty())) {
      return errors;
    }

    rnrRepository.saveProgramRnrTemplate(programTemplate);
    programService.setTemplateConfigured(programTemplate.getProgramId());
    return null;
  }


  public List<RnrColumn> fetchColumnsForRequisition(Integer programId) {
    return rnrRepository.fetchColumnsForRequisition(programId);
  }
}
