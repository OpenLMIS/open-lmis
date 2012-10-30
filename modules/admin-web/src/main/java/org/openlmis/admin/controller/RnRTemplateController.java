package org.openlmis.admin.controller;


import org.openlmis.rnr.domain.RnRColumn;
import org.openlmis.rnr.service.RnRTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

public class RnRTemplateController {

    private RnRTemplateService rnrTemplateService;

    @Autowired
    public RnRTemplateController(RnRTemplateService rnrTemplateService) {
        this.rnrTemplateService = rnrTemplateService;
    }

    @RequestMapping("/rnr/master/columns")
    public List<RnRColumn> fetchMasterColumnList() {
        return rnrTemplateService.fetchAllMasterColumns();
    }
}
