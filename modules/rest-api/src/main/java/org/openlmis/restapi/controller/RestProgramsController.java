package org.openlmis.restapi.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.restapi.domain.ProgramWithProducts;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestProgramsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.openlmis.restapi.response.RestResponse.error;
import static org.openlmis.restapi.response.RestResponse.response;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@NoArgsConstructor
public class RestProgramsController extends BaseController {

    @Autowired
    private RestProgramsService programService;

    @RequestMapping(value = "/rest-api/programs-with-products", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<RestResponse> getProgramWithProductsByFacility(@RequestParam String facilityCode) {
        try {
            List<ProgramWithProducts> programsWithProducts = programService.getAllProgramsWithProductsByFacilityCode(facilityCode);
            return response("programsWithProducts", programsWithProducts);
        } catch (DataException e) {
            return error(e.getOpenLmisMessage(), BAD_REQUEST);
        }
    }
}
