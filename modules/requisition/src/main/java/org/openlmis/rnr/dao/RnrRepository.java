package org.openlmis.rnr.dao;

import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RnrRepository {

    private RnrColumnMapper rnrColumnMapper;
    private ProgramRnrColumnMapper programRnrColumnMapper;

    @Autowired
    public RnrRepository(RnrColumnMapper rnrColumnMapper, ProgramRnrColumnMapper programRnrColumnMapper) {
        this.rnrColumnMapper = rnrColumnMapper;
        this.programRnrColumnMapper = programRnrColumnMapper;
    }

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
}
