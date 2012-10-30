package org.openlmis.rnr.service;

import org.openlmis.rnr.dao.RnRColumnMapper;
import org.openlmis.rnr.domain.RnRColumn;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RnRTemplateService {
    private RnRColumnMapper rnrColumnMapper;

    @Autowired
    public RnRTemplateService(RnRColumnMapper rnrColumnMapper) {
        this.rnrColumnMapper = rnrColumnMapper;
    }

    public List<RnRColumn> fetchAllMasterColumns() {
        return rnrColumnMapper.fetchAllMasterRnRColumns();
    }
}
