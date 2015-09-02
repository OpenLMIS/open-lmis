package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.web.response.OpenLmisResponse;
import org.openlmis.web.rest.RestClient;
import org.openlmis.web.rest.model.ColdTradeData;
import org.openlmis.web.rest.model.Fridge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller handles endpoint to get fridge status.
 */

@Controller
@NoArgsConstructor
public class FridgeStatusController extends BaseController {

    @Value("${nexleaf.api.user}")
    private String user;
    @Value("${nexleaf.api.pass}")
    private String pwd;

    @RequestMapping(value = "/fridges", method = GET)
    public ResponseEntity<OpenLmisResponse> getFridges(HttpServletRequest request) {
        RestClient restClient = new RestClient(user, pwd);
        ColdTradeData coldTradeData;
        try {
            coldTradeData = restClient.getForObject("http://uar.coldtrace.org/api/v1/fridges/", ColdTradeData.class);
            for (Fridge f : coldTradeData.getFridges()) {
                f.updateURL(user, pwd);
            }
        } catch (Exception e) {
            coldTradeData = null;
        }
        return response("coldTradeData", coldTradeData);
    }

}