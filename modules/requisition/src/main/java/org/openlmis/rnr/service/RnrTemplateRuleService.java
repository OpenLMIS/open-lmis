package org.openlmis.rnr.service;

import lombok.NoArgsConstructor;
import org.drools.KnowledgeBase;
import org.drools.runtime.StatelessKnowledgeSession;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.repository.RnrTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@NoArgsConstructor
public class RnrTemplateRuleService {

    private KnowledgeBase rnrTemplateRulesKnowledgeBase;

    private RnrTemplateRepository rnrRepository;

    @Autowired
    public RnrTemplateRuleService(@Qualifier("rnrTemplateRulesKnowledgeBase") KnowledgeBase rnrTemplateRulesKnowledgeBase, RnrTemplateRepository rnrRepository) {
        this.rnrRepository = rnrRepository;
        this.rnrTemplateRulesKnowledgeBase = rnrTemplateRulesKnowledgeBase;
    }

    public Map<String, String> validate(ProgramRnrTemplate template) {
        StatelessKnowledgeSession statelessKnowledgeSession = rnrTemplateRulesKnowledgeBase.newStatelessKnowledgeSession();
        RuleValidationErrors validationErrors = new RuleValidationErrors();
        statelessKnowledgeSession.setGlobal("validationErrors", validationErrors);
        statelessKnowledgeSession.execute(template);
        return validationErrors.get();
    }

    public static class RuleValidationErrors {

        Map<String, String> errors = new HashMap<>();

        public Map<String, String> get() {
            return errors;
        }

        public void add(String rnrColumnName, String error) {
            errors.put(rnrColumnName, error);
        }

    }

}
