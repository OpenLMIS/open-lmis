/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.repository;

import org.openlmis.core.domain.Program;
import org.openlmis.rnr.domain.Column;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.repository.mapper.ProgramRnrColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository class for Rnr Template related database operations.
 */

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
    for (Column rnrColumn : programRnrTemplate.getColumns()) {
      rnrColumn.setCreatedBy(programRnrTemplate.getModifiedBy());
      rnrColumn.setModifiedBy(programRnrTemplate.getModifiedBy());
      programRnrColumnMapper.insert(programRnrTemplate.getProgramId(), (RnrColumn) rnrColumn);
    }
  }

  private void updateAllProgramRnRColumns(ProgramRnrTemplate programRnrTemplate) {
    for (Column rnrColumn : programRnrTemplate.getColumns()) {
      rnrColumn.setModifiedBy(programRnrTemplate.getModifiedBy());
      programRnrColumnMapper.update(programRnrTemplate.getProgramId(), (RnrColumn) rnrColumn);
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
