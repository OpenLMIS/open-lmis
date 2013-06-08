/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.repository;

import org.openlmis.core.domain.Program;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.repository.mapper.ProgramRnrColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RnrTemplateRepository {

  @Autowired
  private ProgramRnrColumnMapper programRnrColumnMapper;

  public void saveProgramRnrTemplate(ProgramRnrTemplate programTemplate) {
    if (programRnrColumnMapper.isRnrTemplateDefined(programTemplate.getProgramId())) {
      updateAllProgramRnRColumns(programTemplate);
    } else {
      insertAllProgramRnRColumns(programTemplate);
    }
  }

  private void insertAllProgramRnRColumns(ProgramRnrTemplate programRnrTemplate) {
    for (RnrColumn rnrColumn : programRnrTemplate.getRnrColumns()) {
      rnrColumn.setCreatedBy(programRnrTemplate.getModifiedBy());
      rnrColumn.setModifiedBy(programRnrTemplate.getModifiedBy());
      programRnrColumnMapper.insert(programRnrTemplate.getProgramId(), rnrColumn);
    }
  }

  private void updateAllProgramRnRColumns(ProgramRnrTemplate programRnrTemplate) {
    for (RnrColumn rnrColumn : programRnrTemplate.getRnrColumns()) {
      rnrColumn.setModifiedBy(programRnrTemplate.getModifiedBy());
      programRnrColumnMapper.update(programRnrTemplate.getProgramId(), rnrColumn);
    }
  }

  public List<RnrColumn> fetchColumnsForRequisition(Long programId) {
    return programRnrColumnMapper.fetchDefinedRnrColumnsForProgram(programId);
  }

  public List<RnrColumn> fetchRnrTemplateColumnsOrMasterColumns(Long programId) {
    if (programRnrColumnMapper.isRnrTemplateDefined(programId)) {
      return programRnrColumnMapper.fetchDefinedRnrColumnsForProgram(programId);
    } else {
      return programRnrColumnMapper.fetchAllMasterRnRColumns();
    }
  }

  public boolean isFormulaValidationRequired(Program program) {
    return programRnrColumnMapper.isFormulaValidationRequired(program);
  }
}
