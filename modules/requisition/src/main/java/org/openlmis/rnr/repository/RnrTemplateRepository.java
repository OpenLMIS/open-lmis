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
        if (rnrColumnMapper.isRnrTemplateDefined(programTemplate.getProgramCode())) {
            updateAllProgramRnRColumns(programTemplate);
        } else {
            insertAllProgramRnRColumns(programTemplate);
        }
    }

    private void insertAllProgramRnRColumns(ProgramRnrTemplate programRnrTemplate) {
        for (RnrColumn rnrColumn : programRnrTemplate.getRnrColumns()) {
            rnrColumnMapper.insert(programRnrTemplate.getProgramCode(), rnrColumn);
        }
    }

    private void updateAllProgramRnRColumns(ProgramRnrTemplate programRnrTemplate) {
        for (RnrColumn rnrColumn : programRnrTemplate.getRnrColumns()) {
            rnrColumnMapper.update(programRnrTemplate.getProgramCode(), rnrColumn);
        }
    }

    public List<RnrColumn> fetchVisibleProgramRnRColumns(String programCode) {
        return rnrColumnMapper.getVisibleProgramRnrColumns(programCode);
    }

    public List<RnrColumn> fetchRnrTemplateColumns(String programCode) {
        if (rnrColumnMapper.isRnrTemplateDefined(programCode)) {
            return rnrColumnMapper.fetchDefinedRnrColumnsForProgram(programCode);
        } else {
            return rnrColumnMapper.fetchAllMasterRnRColumns();
        }
    }
}
