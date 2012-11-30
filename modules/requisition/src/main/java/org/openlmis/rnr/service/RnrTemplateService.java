package org.openlmis.rnr.service;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.repository.RnrTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@NoArgsConstructor
public class RnrTemplateService {

    private RnrTemplateRepository rnrRepository;

    private RnrTemplateRuleService rnrTemplateRuleService;

    @Autowired
    public RnrTemplateService(RnrTemplateRepository rnrRepository, RnrTemplateRuleService rnrTemplateRuleService) {
        this.rnrRepository = rnrRepository;
        this.rnrTemplateRuleService = rnrTemplateRuleService;
    }

    public List<RnrColumn> fetchAllRnRColumns(String programCode) {
        List<RnrColumn> rnrColumns;
        if (rnrRepository.isRnRTemPlateDefinedForProgram(programCode)) {
            rnrColumns = rnrRepository.fetchProgramRnrColumns(programCode);
        } else {
            rnrColumns = rnrRepository.fetchAllMasterRnRColumns();
        }
        return rnrColumns == null ? new ArrayList<RnrColumn>() : rnrColumns;
    }


    public Map<String, String> saveRnRTemplateForProgram(String programCode, List<RnrColumn> rnrColumns) {
        Map<String, String> errors = rnrTemplateRuleService.validate(rnrColumns);
        if (!(errors == null || errors.isEmpty())) {
            return errors;
        }

        if (rnrRepository.isRnRTemPlateDefinedForProgram(programCode)) {
            rnrRepository.updateAllProgramRnRColumns(programCode, rnrColumns);
        } else {
            rnrRepository.insertAllProgramRnRColumns(programCode, rnrColumns);
        }
        return null;
    }


    public List<RnrColumn> fetchVisibleRnRColumns(String programCode) {
        return rnrRepository.fetchVisibleProgramRnRColumns(programCode);
    }
}
