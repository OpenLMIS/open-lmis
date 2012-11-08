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

    @Autowired @SuppressWarnings("unused")
    private RnrColumnMapper rnrColumnMapper;
    @Autowired @SuppressWarnings("unused")
    private ProgramRnrColumnMapper programRnrColumnMapper;

    @Transactional
    public void insertAllProgramRnRColumns(int programId, List<RnrColumn> rnrColumns) {
        for (RnrColumn rnrColumn : rnrColumns) {
            programRnrColumnMapper.insert(programId, rnrColumn);
        }
    }


    public boolean isRnRTemPlateDefinedForProgram(int programId) {
        return programRnrColumnMapper.isRnrTemplateDefined(programId);
    }

    public List<RnrColumn> fetchAllMasterRnRColumns() {
        return rnrColumnMapper.fetchAllMasterRnRColumns();
    }

    public List<RnrColumn> fetchRnrColumnsDefinedForAProgram(int existingProgramId) {
        return programRnrColumnMapper.getAllRnrColumnsForProgram(existingProgramId);
    }

    @Transactional
    public void updateAllProgramRnRColumns(int programId, List<RnrColumn> rnrColumns) {
        for (RnrColumn rnrColumn : rnrColumns) {
            programRnrColumnMapper.update(programId, rnrColumn);
        }
    }
}
