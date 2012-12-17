package org.openlmis.rnr.repository;

import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.repository.mapper.RnrColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@NoArgsConstructor
public class RnrTemplateRepository {

    @Autowired
    private RnrColumnMapper rnrColumnMapper;

    @Transactional
    public void insertAllProgramRnRColumns(String programCode, List<RnrColumn> rnrColumns) {
        for (RnrColumn rnrColumn : rnrColumns) {
            rnrColumnMapper.insert(programCode, rnrColumn);
        }
    }

    // TODO : this is not be a repository method
    public boolean isRnRTemPlateDefinedForProgram(String programCode) {
        return rnrColumnMapper.isRnrTemplateDefined(programCode);
    }

    public List<RnrColumn> fetchAllMasterRnRColumns() {
        return rnrColumnMapper.fetchAllMasterRnRColumns();
    }

    @Transactional
    public void updateAllProgramRnRColumns(String programCode, List<RnrColumn> rnrColumns) {
        for (RnrColumn rnrColumn : rnrColumns) {
            rnrColumnMapper.update(programCode, rnrColumn);
        }
    }

    public List<RnrColumn> fetchProgramRnrColumns(String programCode) {
        return rnrColumnMapper.getAllRnrColumnsForProgram(programCode);
    }

    public List<RnrColumn> fetchVisibleProgramRnRColumns(String programCode) {
        return rnrColumnMapper.getVisibleProgramRnrColumns(programCode);
    }

}
