package org.openlmis.web.rest.service;

import org.openlmis.web.rest.RestClient;
import org.openlmis.web.rest.model.ColdTraceData;
import org.openlmis.web.rest.model.Fridge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class FridgeService {

    @Value("${nexleaf.api.user}")
    private String user;

    @Value("${nexleaf.api.pass}")
    private String pwd;

    @Value("${nexleaf.api.url}")
    private String url;

    private RestClient restClient;

    @PostConstruct
    public void init() {
        restClient = new RestClient(user, pwd);
    }

    public ColdTraceData getFridges() {
        ColdTraceData coldTraceData;
        try {
            coldTraceData = restClient.getForObject(url, ColdTraceData.class);
            for (Fridge f : coldTraceData.getFridges()) {
                f.updateURL(user, pwd);
            }
        } catch (Exception e) {
            coldTraceData = null;
        }
        return coldTraceData;
    }
}
