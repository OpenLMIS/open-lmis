package org.openlmis.restapi.service.integration;

import org.apache.commons.collections.Transformer;
import org.openlmis.core.domain.Program;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.restapi.config.IntegrationFCConfig;
import org.openlmis.restapi.domain.integration.ProgramIntegrationDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.apache.commons.collections.CollectionUtils.collect;

@Service
public class ProgramIntegrationFromFCService extends IntegrationFromFCService<ProgramIntegrationDTO> {

    private static final String method = "area/getAllChangesV1";

    private ProgramRepository programRepository;

    @Autowired
    public ProgramIntegrationFromFCService(RestTemplate restTemplate,
                                           IntegrationFCConfig integrationFCConfig,
                                           ProgramRepository programRepository) {
        super(restTemplate, integrationFCConfig);
        this.programRepository = programRepository;
    }

    @Override
    List<ProgramIntegrationDTO> getDataFromFC(String date) {
        return getDataTemplate(date, method, ProgramIntegrationDTO[].class);
    }


    @Override
    void toDb(List<ProgramIntegrationDTO> data) {

        List<Program> programFromFcs = convertPrograms(data);
        List<Program> updatedPrograms = new ArrayList<>();
        Map<Long, Boolean> updatedStatus = new HashMap<>();
        classificationOperationForProgram(programFromFcs, updatedPrograms, updatedStatus);
        logger.info(String.format("Get program from FC, need to add[%d], need to update[%d], need to active/inactive [%d] ",
                programFromFcs.size(), updatedPrograms.size(), updatedStatus.size()));
        programRepository.toPersistDbByOperationType(programFromFcs, updatedPrograms, updatedStatus);

    }

    private List<Program> convertPrograms(List<ProgramIntegrationDTO> data) {
        return (List<Program>) collect(data, new Transformer() {
            @Override
            public Object transform(Object source) {
                Program target = new Program();
                BeanUtils.copyProperties((ProgramIntegrationDTO) source, target);
                return target;
            }
        });
    }

    private void classificationOperationForProgram(List<Program> programFromFcs, List<Program> updatedPrograms, Map<Long, Boolean> updatedStatus) {
        List<Program> programFromDbs = programRepository.getAll();
        for(Iterator<Program> programFromFcIt = programFromFcs.iterator(); programFromFcIt.hasNext();){
            Program programFromFc = programFromFcIt.next();
            for (Program programFromDb : programFromDbs) {
                if(isValidClassification(updatedPrograms, updatedStatus, programFromFc, programFromDb)){
                    programFromFcIt.remove();
                    break;
                }
            }

        }
    }

    private boolean isValidClassification(List<Program> updatedPrograms, Map<Long, Boolean> updatedStatus, Program programFromFc, Program programFromDb) {
        switch (programFromFc.isEqualForFCProgram(programFromDb)) {
            case 0:
                return true;
            case 1:
                updatedPrograms.add(programFromFc);
                return true;
            case 2:
                updatedPrograms.add(programFromFc);
                updatedStatus.put(programFromDb.getId(), programFromFc.getActive());
                return true;
            default:
                return false;
        }
    }
}
