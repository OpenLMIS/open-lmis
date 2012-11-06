package org.openlmis.rnr.service;

import org.openlmis.rnr.dao.RnRColumnMapper;
import org.openlmis.rnr.dao.RnrDao;
import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RnRTemplateService {
    private RnRColumnMapper rnrColumnMapper;
    private RnrDao rnrDao;

    @Autowired
    public RnRTemplateService(RnRColumnMapper rnrColumnMapper, RnrDao rnrDao) {
        this.rnrColumnMapper = rnrColumnMapper;
        this.rnrDao = rnrDao;
    }

    public List<RnrColumn> fetchAllMasterColumns() {
        List<RnrColumn> rnRColumns = rnrColumnMapper.fetchAllMasterRnRColumns();
        return rnRColumns==null ? new ArrayList<RnrColumn>(): rnRColumns;
    }

    public void createRnRTemplateForProgram(Integer programId, List<RnrColumn> rnRColumns) {
        rnrDao.insertAllProgramRnRColumns(programId, rnRColumns);
    }
}
