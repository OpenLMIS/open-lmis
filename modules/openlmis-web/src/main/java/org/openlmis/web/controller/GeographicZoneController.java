package org.openlmis.web.controller;

import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.GeographicZoneService;
import org.openlmis.core.service.GeographicZoneServiceExtension;
import org.openlmis.report.service.ReportLookupService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static org.openlmis.web.response.OpenLmisResponse.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;


@Controller
public class GeographicZoneController extends BaseController {

    @Autowired
    private GeographicZoneService service;

    @Autowired
    private GeographicZoneServiceExtension geographicZoneServiceExt;

    @Autowired
    private ReportLookupService reportLookupService;

    @RequestMapping(value = "/geographicZones/{id}", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_DISTRIBUTION')")
    public ResponseEntity<OpenLmisResponse> get(@PathVariable Long id){
        return OpenLmisResponse.response("geoZone", service.getById(id));
    }



    @RequestMapping(value = "/geographicZone/insert", method = POST, headers = "Accept=application/json")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_GEOGRAPHIC_ZONES')")
    public ResponseEntity<OpenLmisResponse> insert(@RequestBody GeographicZone geographicZone, HttpServletRequest request) {
        ResponseEntity<OpenLmisResponse> successResponse;
        geographicZone.setCreatedBy(loggedInUserId(request));
        geographicZone.setModifiedBy(loggedInUserId(request));
        geographicZone.setModifiedDate(new Date());

        try {
            geographicZoneServiceExt.saveNew(geographicZone);
        } catch (DataException e) {
            return error(e, HttpStatus.BAD_REQUEST);
        }
        successResponse = success(String.format("Geographic zone '%s' has been successfully created",
                geographicZone.getName()), "");
        successResponse.getBody().addData("geographicZone", geographicZone);
        return successResponse;
    }


    @RequestMapping(value = "/geographicZone/setDetails", method = POST, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_GEOGRAPHIC_ZONES')")
    public ResponseEntity<OpenLmisResponse> update(@RequestBody GeographicZone geographicZone,
                                                   HttpServletRequest request) {
        ResponseEntity<OpenLmisResponse> successResponse;
        geographicZone.setModifiedBy(loggedInUserId(request));
        try {
            geographicZoneServiceExt.update(geographicZone);
        } catch (DataException e) {
            return error(e, HttpStatus.BAD_REQUEST);
        }
        successResponse = success("Geographic zone '" + geographicZone.getName() + "' has been successfully updated");
        successResponse.getBody().addData("geographicZone", geographicZone);
        return successResponse;
    }

    @RequestMapping(value = "/geographicZone/getDetails/{id}", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_GEOGRAPHIC_ZONES')")
    public ResponseEntity<OpenLmisResponse> getGeographicZone(@PathVariable(value = "id") int id) {
        return OpenLmisResponse.response("geographicZone",geographicZoneServiceExt.getById(id));
    }

    @RequestMapping(value = "/geographicZones", method = GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_GEOGRAPHIC_ZONES')")
    public List<GeographicZone> searchGeographicZone(@RequestParam(required = true) String param) {
        return geographicZoneServiceExt.searchGeographicZone(param);
    }


    @RequestMapping(value = "/geographicLevels", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getAllGeographicLevels(HttpServletRequest request) {
        return OpenLmisResponse.response("geographicLevels", reportLookupService.getAllGeographicLevels());
    }

    @RequestMapping(value = "/geographicZone/getList", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_GEOGRAPHIC_ZONES')")
    public ResponseEntity<OpenLmisResponse> getGeographicZoneList(HttpServletRequest request) {
        return OpenLmisResponse.response("geographicZones",geographicZoneServiceExt.getAll());
    }


}
