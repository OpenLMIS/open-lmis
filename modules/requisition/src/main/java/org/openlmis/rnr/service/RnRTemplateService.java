package org.openlmis.rnr.service;

import org.openlmis.rnr.dao.RnrColumnMapper;
import org.openlmis.rnr.dao.RnrDao;
import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RnRTemplateService {
    private RnrColumnMapper rnrColumnMapper;
    private RnrDao rnrDao;

    @Autowired
    public RnRTemplateService(RnrColumnMapper rnrColumnMapper, RnrDao rnrDao) {
        this.rnrColumnMapper = rnrColumnMapper;
        this.rnrDao = rnrDao;
    }

    public List<RnrColumn> fetchAllMasterColumns() {
        List<RnrColumn> rnrColumns = rnrColumnMapper.fetchAllMasterRnRColumns();
        return rnrColumns==null ? new ArrayList<RnrColumn>(): rnrColumns;
    }

    public void createRnRTemplateForProgram(Integer programId, List<RnrColumn> rnrColumns) {
        rnrDao.insertAllProgramRnRColumns(programId, rnrColumns);
    }
}
