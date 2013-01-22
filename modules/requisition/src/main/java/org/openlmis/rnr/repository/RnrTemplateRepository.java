package org.openlmis.rnr.repository;

import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.repository.mapper.RnrColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@NoArgsConstructor
public class RnrTemplateRepository {


    private RnrColumnMapper rnrColumnMapper;

    @Autowired
    public RnrTemplateRepository(RnrColumnMapper rnrColumnMapper) {
        this.rnrColumnMapper = rnrColumnMapper;
    }


    @Transactional
    public void saveProgramRnrTemplate(ProgramRnrTemplate programTemplate) {
        if (rnrColumnMapper.isRnrTemplateDefined(programTemplate.getProgramId())) {
            updateAllProgramRnRColumns(programTemplate);
        } else {
            insertAllProgramRnRColumns(programTemplate);
        }
    }

    private void insertAllProgramRnRColumns(ProgramRnrTemplate programRnrTemplate) {
        for (RnrColumn rnrColumn : programRnrTemplate.getRnrColumns()) {
            rnrColumnMapper.insert(programRnrTemplate.getProgramId(), rnrColumn);
        }
    }

    private void updateAllProgramRnRColumns(ProgramRnrTemplate programRnrTemplate) {
        for (RnrColumn rnrColumn : programRnrTemplate.getRnrColumns()) {
            rnrColumnMapper.update(programRnrTemplate.getProgramId(), rnrColumn);
        }
    }

    public List<RnrColumn> fetchVisibleProgramRnRColumns(Integer programId) {
        return rnrColumnMapper.getVisibleProgramRnrColumns(programId);
    }

    public List<RnrColumn> fetchRnrTemplateColumns(Integer programId) {
        if (rnrColumnMapper.isRnrTemplateDefined(programId)) {
            return rnrColumnMapper.fetchDefinedRnrColumnsForProgram(programId);
        } else {
            return rnrColumnMapper.fetchAllMasterRnRColumns();
        }
    }

  public boolean isRnrTemplateDefined(Integer programId) {
    return rnrColumnMapper.isRnrTemplateDefined(programId);
  }

  public boolean isFormulaValidationRequired(Integer programId) {
    return rnrColumnMapper.isFormulaValidationRequired(programId);
  }
}
