package org.openlmis.rnr.service;

import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.repository.RnrTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
        List<RnrColumn> rnrColumns = rnrRepository.fetchRnrTemplateColumns(programCode);
        return rnrColumns;
    }


    public Map<String, String> saveRnRTemplateForProgram(ProgramRnrTemplate programTemplate) {
        Map<String, String> errors = new HashMap<>();
        errors.putAll(rnrTemplateRuleService.validate(programTemplate));
        errors.putAll(programTemplate.validate());

        if (!(errors == null || errors.isEmpty())) {
            return errors;
        }

        rnrRepository.saveProgramRnrTemplate(programTemplate);
        return null;
    }


    public List<RnrColumn> fetchVisibleRnRColumns(String programCode) {
        return rnrRepository.fetchVisibleProgramRnRColumns(programCode);
    }
}
