package org.openlmis.rnr.dao;

import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@NoArgsConstructor
// TODO : this should be RnrTemplateRepository
public class RnrTemplateRepository {

    @Autowired
    private RnrColumnMapper rnrColumnMapper;
    @Autowired
    private ProgramRnrColumnMapper programRnrColumnMapper;

    @Transactional
    public void insertAllProgramRnRColumns(String programCode, List<RnrColumn> rnrColumns) {
        for (RnrColumn rnrColumn : rnrColumns) {
            programRnrColumnMapper.insert(programCode, rnrColumn);
        }
    }

    // TODO : this is not be a repository method
    public boolean isRnRTemPlateDefinedForProgram(String programCode) {
        return programRnrColumnMapper.isRnrTemplateDefined(programCode);
    }

    public List<RnrColumn> fetchAllMasterRnRColumns() {
        return rnrColumnMapper.fetchAllMasterRnRColumns();
    }

    @Transactional
    public void updateAllProgramRnRColumns(String programCode, List<RnrColumn> rnrColumns) {
        for (RnrColumn rnrColumn : rnrColumns) {
            programRnrColumnMapper.update(programCode, rnrColumn);
        }
    }

    public List<RnrColumn> fetchProgramRnrColumns(String programCode) {
        return programRnrColumnMapper.getAllRnrColumnsForProgram(programCode);
    }

    public List<RnrColumn> fetchVisibleProgramRnRColumns(String programCode) {
        return programRnrColumnMapper.getVisibleProgramRnrColumns(programCode);
    }

}
