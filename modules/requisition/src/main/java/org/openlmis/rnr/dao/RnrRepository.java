package org.openlmis.rnr.dao;

import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@NoArgsConstructor
public class RnrRepository {

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

    public boolean isRnRTemPlateDefinedForProgram(String programCode) {
        return programRnrColumnMapper.isRnrTemplateDefined(programCode);
    }

    public List<RnrColumn> fetchAllMasterRnRColumns() {
        return rnrColumnMapper.fetchAllMasterRnRColumns();
    }

    public List<RnrColumn> fetchRnrColumnsDefinedForAProgram(String existingProgramCode) {
        return programRnrColumnMapper.getAllRnrColumnsForProgram(existingProgramCode);
    }

    @Transactional
    public void updateAllProgramRnRColumns(String programCode, List<RnrColumn> rnrColumns) {
        for (RnrColumn rnrColumn : rnrColumns) {
            programRnrColumnMapper.update(programCode, rnrColumn);
        }
    }
}
