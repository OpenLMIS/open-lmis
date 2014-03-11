/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.dto.GeographicZoneGeometry;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.GeographicZoneService;
import org.openlmis.core.service.GeographicZoneServiceExtension;
import org.openlmis.core.service.SMSService;
import org.openlmis.report.service.lookup.ReportLookupService;
import org.openlmis.web.model.GeoZoneInfo;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
  public ResponseEntity<OpenLmisResponse> get(@PathVariable Long id) {
    return OpenLmisResponse.response("geoZone", service.getById(id));
  }


  @RequestMapping(value = "/geographicZone/insert.json", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_GEOGRAPHIC_ZONES')")
  public ResponseEntity<OpenLmisResponse> insert(@RequestBody GeographicZone geographicZone, HttpServletRequest request) throws IOException{
    ResponseEntity<OpenLmisResponse> successResponse;
    geographicZone.setCreatedBy(loggedInUserId(request));
    geographicZone.setModifiedBy(loggedInUserId(request));
    geographicZone.setModifiedDate(new Date());

    try {
      geographicZoneServiceExt.saveNew(geographicZone);

    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
      catch (IOException e){
          return error(e.getMessage(),HttpStatus.BAD_REQUEST);
      }
    successResponse = success("Geographic zone " + geographicZone.getName() + " has been successfully created");
    successResponse.getBody().addData("geographicZone", geographicZone);
    successResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    return successResponse;
  }


  @RequestMapping(value = "/geographicZone/setDetails", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_GEOGRAPHIC_ZONES')")
  public ResponseEntity<OpenLmisResponse> update(@RequestBody GeographicZone geographicZone,
                                                 HttpServletRequest request) throws IOException{
    ResponseEntity<OpenLmisResponse> successResponse;
    geographicZone.setModifiedBy(loggedInUserId(request));
    try {
      if (geographicZone.getId() == null) {
        geographicZoneServiceExt.saveNew(geographicZone);
      } else {
        geographicZoneServiceExt.update(geographicZone);
      }
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
    catch (IOException e){
        return error(e.getMessage(),HttpStatus.BAD_REQUEST);
    }
    successResponse = success("Geographic zone '" + geographicZone.getName() + "' has been successfully saved");
    successResponse.getBody().addData("geographicZone", geographicZone);
    return successResponse;
  }

  @RequestMapping(value = "/geographicZone/getDetails/{id}", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_GEOGRAPHIC_ZONES')")
  public ResponseEntity<OpenLmisResponse> getGeographicZone(@PathVariable(value = "id") int id) {
    return OpenLmisResponse.response("geographicZone", geographicZoneServiceExt.getById(id));
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
    return OpenLmisResponse.response("geographicZones", geographicZoneServiceExt.getAll());
  }


  @RequestMapping(value = "/geographic-zone/save-gis", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_GEOGRAPHIC_ZONES')")
  public ResponseEntity<OpenLmisResponse> saveGeographicZoneGIS(@RequestBody GeoZoneInfo geoZoneGeometries, HttpServletRequest request) {
    geographicZoneServiceExt.saveGisInfo(geoZoneGeometries.getFeatures(), loggedInUserId(request));
    return OpenLmisResponse.response("status", true);
  }

}
