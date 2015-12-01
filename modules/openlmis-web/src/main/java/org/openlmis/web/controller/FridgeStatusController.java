package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.web.response.OpenLmisResponse;
import org.openlmis.web.rest.RestClient;
import org.openlmis.web.rest.model.ColdTraceData;
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

    @Value("${nexleaf.api.url}")
    private String url;

    @RequestMapping(value = "/fridges", method = GET)
    public ResponseEntity<OpenLmisResponse> getFridges(HttpServletRequest request) {
        RestClient restClient = new RestClient(user, pwd);
        ColdTraceData coldTraceData;
        try {
            coldTraceData = restClient.getForObject(url, ColdTraceData.class);
            for (Fridge f : coldTraceData.getFridges()) {
                f.updateURL(user, pwd);
            }
        } catch (Exception e) {
            coldTraceData = null;
        }
        return response("coldTraceData", coldTraceData);
    }

}
