package org.openlmis.restapi.service.integration;

import org.openlmis.restapi.config.IntegrationFCConfig;
import org.openlmis.restapi.domain.integration.RegimenIntegrationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class RegimenIntegrationFromFCService extends IntegrationFromFCService<RegimenIntegrationDTO> {

    private static final String method = "areaRegime/getAllChangesV1";

    @Autowired
    public RegimenIntegrationFromFCService(RestTemplate restTemplate, IntegrationFCConfig integrationFCConfig) {
        super(restTemplate, integrationFCConfig);
    }

    @Override
    public List<RegimenIntegrationDTO> getDataFromFC(String date) {
        String url = getUrl(date, method);
        logger.info(String.format("Get regimen from FC {%s}, the beginning time is %s", url, date));
        ResponseEntity<RegimenIntegrationDTO[]> regimenEntities = restTemplate.getForEntity(url, RegimenIntegrationDTO[].class);
        RegimenIntegrationDTO[] regimens = regimenEntities.getBody();
        logger.info(String.format("Get regimen from FC the size is %d", regimens.length));
        if (regimens != null && regimens.length > 0) {
            return Arrays.asList(regimens);
        }
        return null;
    }

    @Override
    void toDb(List<RegimenIntegrationDTO> data) {

    }
}
