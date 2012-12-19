package org.openlmis.rnr.service;

import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.repository.RnrTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@NoArgsConstructor
public class RnrTemplateService {

    private RnrTemplateRepository rnrRepository;


    @Autowired
    public RnrTemplateService(RnrTemplateRepository rnrRepository) {
        this.rnrRepository = rnrRepository;
    }

    public List<RnrColumn> fetchAllRnRColumns(String programCode) {
        List<RnrColumn> rnrColumns = rnrRepository.fetchRnrTemplateColumns(programCode);
        return rnrColumns;
    }


    public Map<String, String> saveRnRTemplateForProgram(ProgramRnrTemplate programTemplate) {
        Map<String, String> errors =(programTemplate.validate());

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
