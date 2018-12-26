package org.openlmis.restapi.service.integration;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Regimen;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.RegimenRepository;
import org.openlmis.core.repository.mapper.RegimenCategoryMapper;
import org.openlmis.restapi.config.IntegrationFCConfig;
import org.openlmis.restapi.domain.integration.RegimenIntegrationDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class RegimenIntegrationFromFCService extends IntegrationFromFCService<RegimenIntegrationDTO> {

    private static final String method = "areaRegime/getAllChangesV1";

    private ProgramRepository programRepository;

    private RegimenCategoryMapper regimenCategoryMapper;

    private RegimenRepository regimenRepository;

    @Autowired
    public RegimenIntegrationFromFCService(RestTemplate restTemplate,
                                           IntegrationFCConfig integrationFCConfig,
                                           ProgramRepository programRepository,
                                           RegimenCategoryMapper regimenCategoryMapper,
                                           RegimenRepository regimenRepository) {
        super(restTemplate, integrationFCConfig);
        this.programRepository = programRepository;
        this.regimenCategoryMapper = regimenCategoryMapper;
        this.regimenRepository = regimenRepository;
    }

    @Override
    List<RegimenIntegrationDTO> getDataFromFC(String date) {
        return getDataTemplate(date, method, RegimenIntegrationDTO[].class);
    }

    @Override
    void toDb(List<RegimenIntegrationDTO> data) {
        List<Regimen> regimenFromFcs = convertPrograms(data);
        List<Regimen> updatedRegimens = new ArrayList<>();
        classificationOperationForProgram(regimenFromFcs, updatedRegimens);
        logger.info(String.format("Get regimen from FC, need to add[%d], need to update[%d]",
                regimenFromFcs.size(), updatedRegimens.size()));
        regimenRepository.toPersistDbByOperationType(regimenFromFcs, updatedRegimens);
    }

    private void classificationOperationForProgram(List<Regimen> regimenFromFcs, List<Regimen> updatedRegimens) {
        List<Regimen> regimenFromDbs = regimenRepository.getAllRegimens();
        for (Iterator<Regimen> regimenFcIt = regimenFromFcs.iterator(); regimenFcIt.hasNext(); ) {
            Regimen regimenFromFc = regimenFcIt.next();
            for (Regimen regimenFromDb : regimenFromDbs) {
                if (isValidClassification(updatedRegimens, regimenFromFc, regimenFromDb)) {
                    regimenFcIt.remove();
                    break;
                }
            }

        }
    }

    private boolean isValidClassification(List<Regimen> updatedRegimens, Regimen regimenFromFc, Regimen regimenFromDb) {
        switch (regimenFromFc.isEqualForFCRegimen(regimenFromDb)) {
            case 0:
                return true;
            case 1:
                updatedRegimens.add(regimenFromFc);
                return true;
            default:
                return false;
        }
    }

    private List<Regimen> convertPrograms(List<RegimenIntegrationDTO> data) {
        List<RegimenCategory> regimenCategories = regimenCategoryMapper.getAll();
        List<Program> programs = programRepository.getAll();

        Integer currentDisplayOrder = getLatestDisplayOrder(regimenCategories);

        List<Regimen> regimens = new ArrayList<>();
        for(RegimenIntegrationDTO source : data) {
            Regimen target = new Regimen();
            RegimenIntegrationDTO reg = (RegimenIntegrationDTO) source;
            BeanUtils.copyProperties(reg, target);
            target.setCategory(getRegimenCategory(reg, regimenCategories));
            target.setProgramId(getProgramId(reg, programs));
            target.setDisplayOrder(++currentDisplayOrder);
            regimens.add(target);
        }
        return regimens;
    }

    private Integer getLatestDisplayOrder(List<RegimenCategory> regimenCategories) {
        Integer currentDisplayOrder = 0;
        if(CollectionUtils.isNotEmpty(regimenCategories)) {
           currentDisplayOrder = regimenCategories.get(regimenCategories.size() - 1).getDisplayOrder();
        }
        return currentDisplayOrder;
    }

    private Long getProgramId(RegimenIntegrationDTO reg, List<Program> programs) {
        if (!StringUtils.isBlank(reg.getAreaCode())) {
            for (Program program : programs) {
                if(reg.getAreaCode().equals(program.getCode())) {
                    return program.getId();
                }
            }
        }
        logger.info(String.format("please supply valid program code [%s] for [%s]", reg.getAreaCode(), reg.getCode()));
        throw new DataException("please supply valid program code");
    }

    private RegimenCategory getRegimenCategory(RegimenIntegrationDTO reg, List<RegimenCategory> regimenCategories) {

        if (!StringUtils.isBlank(reg.getCategoryCode())) {
            for (RegimenCategory regimenCategoryFromDb : regimenCategories) {
                if (regimenCategoryFromDb.getCode().equals(reg.getCategoryCode())) {
                    return regimenCategoryFromDb;
                }
            }
        }
        return regimenCategories.get(0);
    }
}
