package org.openlmis.restapi.controller;

import lombok.NoArgsConstructor;
import org.openlmis.restapi.service.ArchivedProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
public class RestArchivedProductController extends BaseController {
    @Autowired
    ArchivedProductService archivedProductService;

    @RequestMapping(value = "/rest-api/facilities/{facilityId}/archivedProducts", method = POST, headers = ACCEPT_JSON)
    public ResponseEntity updateArchivedProductsList(@PathVariable long facilityId, @RequestBody(required = true) List<String> archivedProductCodes) {
        archivedProductService.updateArchivedProductList(facilityId, archivedProductCodes);
        return new ResponseEntity(HttpStatus.OK);
    }

}
