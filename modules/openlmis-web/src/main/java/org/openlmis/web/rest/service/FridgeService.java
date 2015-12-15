package org.openlmis.web.rest.service;

import org.openlmis.web.rest.RestClient;
import org.openlmis.web.rest.model.ColdTraceData;
import org.openlmis.web.rest.model.Fridge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class FridgeService {
    private String user;
    private String pwd;
    private String url;
    private RestClient restClient;

    @PostConstruct
    public void init() {
        restClient = new RestClient(user, pwd);
    }

    public ColdTraceData getFridges(String deliveryZoneCode) {
        ColdTraceData coldTraceData;

        try {
            coldTraceData = restClient.getForObject(url, ColdTraceData.class, deliveryZoneCode);
            for (Fridge f : coldTraceData.getFridges()) {
                f.updateURL(user, pwd);
            }
        } catch (RuntimeException e) {
            throw e; // runtime exceptions should not be catched.
        } catch (Exception e) {
            coldTraceData = null;
        }

        return coldTraceData;
    }

    @Value("${nexleaf.api.user}")
    public void setUser(String user) {
        this.user = user;
    }

    @Value("${nexleaf.api.pass}")
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Value("${nexleaf.api.url}")
    public void setUrl(String url) {
        this.url = String.format("%s?delivery_zone={deliveryZoneCode}", url);
    }

}
