package org.openlmis.rnr.service;

import org.openlmis.rnr.dao.RnRColumnMapper;
import org.openlmis.rnr.dao.RnRDao;
import org.openlmis.rnr.domain.RnRColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RnRTemplateService {
    private RnRColumnMapper rnrColumnMapper;
    private RnRDao rnRDao;

    @Autowired
    public RnRTemplateService(RnRColumnMapper rnrColumnMapper, RnRDao rnRDao) {
        this.rnrColumnMapper = rnrColumnMapper;
        this.rnRDao = rnRDao;
    }

    public List<RnRColumn> fetchAllMasterColumns() {
        List<RnRColumn> rnRColumns = rnrColumnMapper.fetchAllMasterRnRColumns();
        return rnRColumns==null ? new ArrayList<RnRColumn>(): rnRColumns;
    }

    public void createRnRTemplateForProgram(String programId, List<RnRColumn> rnRColumns) {
        rnRDao.insertAllRnRColumns(Integer.parseInt(programId),rnRColumns);
    }
}
