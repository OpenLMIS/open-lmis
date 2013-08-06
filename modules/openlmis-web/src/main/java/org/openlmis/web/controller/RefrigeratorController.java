package org.openlmis.web.controller;

import org.openlmis.core.exception.DataException;
import org.openlmis.distribution.domain.Refrigerator;
import org.openlmis.distribution.service.RefrigeratorReadingService;
import org.openlmis.distribution.service.RefrigeratorService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class RefrigeratorController extends BaseController {

  public static final String REFRIGERATORS = "refrigerators";

  @Autowired
  RefrigeratorService refrigeratorService;

  @Autowired
  RefrigeratorReadingService refrigeratorReadingService;


  @RequestMapping(value = "/deliveryZone/{deliveryZoneId}/program/{programId}/refrigerators", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getRefrigeratorsForADeliveryZoneAndProgram(@PathVariable(value = "deliveryZoneId") Long deliveryZoneId, @PathVariable(value = "programId") Long programId) {
    try {
      ResponseEntity<OpenLmisResponse> response;
      List<Refrigerator> refrigerators = refrigeratorService.getRefrigeratorsForADeliveryZoneAndProgram(deliveryZoneId, programId);
      response = response(REFRIGERATORS, refrigerators);
      return response;
    } catch (DataException dataException) {
      return error(UNEXPECTED_EXCEPTION, HttpStatus.BAD_REQUEST);
    }
  }


}
