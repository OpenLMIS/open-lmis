package org.openlmis.restapi.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;
import org.openlmis.core.exception.DataException;
import org.openlmis.restapi.domain.ProgramWithProducts;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestProgramsWithProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

import static org.openlmis.restapi.response.RestResponse.error;
import static org.openlmis.restapi.response.RestResponse.response;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
public class RestProgramsWithProductsController extends BaseController {

    @Autowired
    private RestProgramsWithProductsService programService;

    //DEPRECATE: facility code can change in openlmis
    @Deprecated
    @RequestMapping(value = "/rest-api/programs-with-products", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<RestResponse> getProgramWithProductsByFacility(@RequestParam String facilityCode) {
        try {
            List<ProgramWithProducts> programsWithProducts = programService.getAllProgramsWithProductsByFacilityCode(facilityCode);
            return response("programsWithProducts", programsWithProducts);
        } catch (DataException e) {
            return error(e.getOpenLmisMessage(), BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/rest-api/latest-programs-with-products", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<RestResponse> getLatestProgramWithProductsByFacility(@RequestParam Long facilityId,
                                                                               @RequestParam(required = false) Long afterUpdatedTime) {
        try {
            Date afterUpdatedTimeInDate = (afterUpdatedTime == null ? null : new Date(afterUpdatedTime));
            List<ProgramWithProducts> programsWithProducts = programService.getLatestProgramsWithProductsByFacilityId(facilityId, afterUpdatedTimeInDate);
            RestResponse restResponse = new RestResponse("programsWithProducts", programsWithProducts);
            restResponse.addData("latestUpdatedTime", new Date());
            return new ResponseEntity<>(restResponse, HttpStatus.OK);
        } catch (DataException e) {
            return error(e.getOpenLmisMessage(), BAD_REQUEST);
        }
    }

}
