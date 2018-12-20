package org.openlmis.restapi.service.integration;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.openlmis.restapi.config.IntegrationFCConfig;
import org.springframework.web.client.RestTemplate;
import reactor.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public abstract class IntegrationFromFCService<T> {

    static Logger logger = org.apache.log4j.Logger.getLogger(IntegrationFromFCService.class);

    IntegrationFCConfig integrationFCConfig;

    RestTemplate restTemplate;

    public IntegrationFromFCService(RestTemplate restTemplate, IntegrationFCConfig integrationFCConfig) {
        this.restTemplate = restTemplate;
        this.integrationFCConfig = integrationFCConfig;
    }

    abstract List<T> getDataFromFC(String date);

    abstract void toDb(List<T> data);

    public void sycDataFromFC(String date) {
        String inputDate = StringUtils.isEmpty(date) ? getBeforeDateString() : date;
        List<T> data = getDataFromFC(inputDate);
        if (CollectionUtils.isNotEmpty(data)) {
            toDb(getDataFromFC(inputDate));
        }
    }

    private String getBeforeDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
    }

    String getUrl(String date, String method) {
        return String.format("%s?key=%s&date=%s&output=json", integrationFCConfig.getUrl() + method, integrationFCConfig.getKey(), date);
    }

}
