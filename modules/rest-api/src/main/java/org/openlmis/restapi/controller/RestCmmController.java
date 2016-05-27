package org.openlmis.restapi.controller;

import com.wordnik.swagger.annotations.Api;
import lombok.NoArgsConstructor;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestCmmService;
import org.openlmis.stockmanagement.domain.CMMEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
@Api(value = "CMM", description = "Upload historical CMMs")
@NoArgsConstructor
public class RestCmmController extends BaseController {

  @Autowired
  private RestCmmService service;

  @RequestMapping(value = "/rest-api/facilities/{facilityId}/Cmms", method = PUT)
  public ResponseEntity<RestResponse> updateCMMsForFacility(@RequestBody List<CMMEntry> cmmEntries,
                                                            @PathVariable Long facilityId, Principal principal) {

    service.updateCMMsForFacility(cmmEntries, facilityId, loggedInUserId(principal));
    return RestResponse.success("msg.cmm.savesuccess");
  }
}
