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
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.GeographicZoneService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * This controller handles endpoint related to get geographicZones details for given id.
 */

@Controller
public class GeographicZoneController extends BaseController {

  @Autowired
  private GeographicZoneService service;

  @RequestMapping(value = "/geographicZones/{id}", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_DISTRIBUTION')")
  public ResponseEntity<OpenLmisResponse> get(@PathVariable Long id) {
    return OpenLmisResponse.response("geoZone", service.getById(id));
  }

  @RequestMapping(value = "/geographicZones", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_GEO_ZONE')")
  public ResponseEntity<OpenLmisResponse> insert(@RequestBody GeographicZone geographicZone, HttpServletRequest request) {
    Long userId = loggedInUserId(request);
    geographicZone.setCreatedBy(userId);
    geographicZone.setModifiedBy(userId);
    try {
      service.save(geographicZone);
    } catch (DataException e) {
      return OpenLmisResponse.error(e, BAD_REQUEST);
    }
    ResponseEntity<OpenLmisResponse> success = success(messageService.message("message.geo.zone.created.success", geographicZone.getName()));
    success.getBody().addData("geoZone", geographicZone);
    return success;
  }

  @RequestMapping(value = "/geographicZones/{id}", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_GEO_ZONE')")
  public ResponseEntity<OpenLmisResponse> update(@RequestBody GeographicZone geographicZone, @PathVariable("id") Long id,
                                                 HttpServletRequest request) {
    Long userId = loggedInUserId(request);
    geographicZone.setId(id);
    geographicZone.setModifiedBy(userId);
    try {
      service.save(geographicZone);
    } catch (DataException e) {
      return OpenLmisResponse.error(e, BAD_REQUEST);
    }
    ResponseEntity<OpenLmisResponse> success = success(messageService.message("message.geo.zone.updated.success", geographicZone.getName()));
    success.getBody().addData("geoZone", geographicZone);
    return success;
  }

  @RequestMapping(value = "/geographicZones", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_GEO_ZONE')")
  public List<GeographicZone> search(@RequestParam(value = "searchParam") String searchParam, @RequestParam(value = "columnName") String columnName) {
    return service.searchBy(searchParam, columnName);
  }
}
