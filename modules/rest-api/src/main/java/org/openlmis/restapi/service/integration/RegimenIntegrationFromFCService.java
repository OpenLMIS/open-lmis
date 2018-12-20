package org.openlmis.restapi.service.integration;

import org.apache.commons.collections.Transformer;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Regimen;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.mapper.RegimenCategoryMapper;
import org.openlmis.restapi.config.IntegrationFCConfig;
import org.openlmis.restapi.domain.integration.ProgramIntegrationDTO;
import org.openlmis.restapi.domain.integration.RegimenIntegrationDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.collect;

@Service
public class RegimenIntegrationFromFCService extends IntegrationFromFCService<RegimenIntegrationDTO> {

    private static final String method = "areaRegime/getAllChangesV1";

    private ProgramRepository programRepository;

    private RegimenCategoryMapper regimenCategoryMapper;

    @Autowired
    public RegimenIntegrationFromFCService(RestTemplate restTemplate,
                                           IntegrationFCConfig integrationFCConfig,
                                           ProgramRepository programRepository,
                                           RegimenCategoryMapper regimenCategoryMapper) {
        super(restTemplate, integrationFCConfig);
        this.programRepository = programRepository;
        this.regimenCategoryMapper = regimenCategoryMapper;
    }

    @Override
    public List<RegimenIntegrationDTO> getDataFromFC(String date) {
        return getDataTemplate(date, method, RegimenIntegrationDTO[].class);
    }

    @Override
    void toDb(List<RegimenIntegrationDTO> data) {

        List<Regimen> regimenFromFcs = convertPrograms(data);
        List<Program> updatedPrograms = new ArrayList<>();

    }



    private List<Regimen> convertPrograms(List<RegimenIntegrationDTO> data) {
        return (List<Regimen>) collect(data, new Transformer() {
            @Override
            public Object transform(Object source) {
                Regimen target = new Regimen();
                BeanUtils.copyProperties((RegimenIntegrationDTO) source, target);
                return target;
            }
        });
    }
}
