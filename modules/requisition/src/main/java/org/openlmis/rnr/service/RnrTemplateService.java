package org.openlmis.rnr.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.repository.RnrTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<RnrColumn> fetchAllRnRColumns(Integer programId) {
        return rnrRepository.fetchRnrTemplateColumns(programId);
    }

    @Transactional
    public Map<String, OpenLmisMessage> saveRnRTemplateForProgram(ProgramRnrTemplate programTemplate) {
        Map<String, OpenLmisMessage> errors =programTemplate.validateToSave();

        if (!(errors.isEmpty())) {
            return errors;
        }

        rnrRepository.saveProgramRnrTemplate(programTemplate);
        return null;
    }


    public List<RnrColumn> fetchColumnsForRequisition(Integer programId) {
        return rnrRepository.fetchColumnsForRequisition(programId);
    }
}
