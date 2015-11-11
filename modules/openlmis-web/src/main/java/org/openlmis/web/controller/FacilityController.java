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

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.RequisitionGroupService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.web.model.FacilityReferenceData;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static org.openlmis.core.domain.Facility.createFacilityToBeDeleted;
import static org.openlmis.core.domain.Facility.createFacilityToBeRestored;
import static org.openlmis.core.domain.RightName.*;
import static org.openlmis.core.web.OpenLmisResponse.response;
import static org.openlmis.core.web.OpenLmisResponse.success;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * This controller handles endpoint related to create, get, update, disable facility, also has endpoints to return home facility,
 * supervised facility for a user.
 */

@Controller
@NoArgsConstructor
public class FacilityController extends BaseController {

  public static final String FACILITIES = "facilities";
  @Autowired
  private FacilityService facilityService;

  @Autowired
  private ProgramService programService;

  @Autowired
  RequisitionGroupService requisitionGroupService;

  @RequestMapping(value = "/facilities", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY, MANAGE_USER')")
  public ResponseEntity<OpenLmisResponse> get(@RequestParam(value = "searchParam", required = false) String searchParam,
                                              @RequestParam(value = "columnName") String columnName,
                                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                                              @Value("${search.page.size}") String limit) {
    Pagination pagination = new Pagination(page, parseInt(limit));
    pagination.setTotalRecords(facilityService.getTotalSearchResultCountByColumnName(searchParam, columnName));
    List<Facility> facilities = facilityService.searchBy(searchParam, columnName, pagination);
    ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response(FACILITIES, facilities);
    response.getBody().addData("pagination", pagination);
    return response;
  }

  @RequestMapping(value = "/filter-facilities", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY, MANAGE_SUPERVISORY_NODE, MANAGE_REQUISITION_GROUP, MANAGE_SUPPLY_LINE, MANAGE_USER')")
  public ResponseEntity<OpenLmisResponse> getFilteredFacilities(@RequestParam(value = "searchParam", required = false) String searchParam,
                                                                @RequestParam(value = "facilityTypeId", required = false) Long facilityTypeId,
                                                                @RequestParam(value = "geoZoneId", required = false) Long geoZoneId,
                                                                @RequestParam(value = "virtualFacility", required = false) Boolean virtualFacility,
                                                                @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                                @Value("${search.results.limit}") String facilitySearchLimit) {
    Integer count = facilityService.getFacilitiesCountBy(searchParam, facilityTypeId, geoZoneId, virtualFacility, enabled);
    if (count <= Integer.parseInt(facilitySearchLimit)) {
      List<Facility> facilities = facilityService.searchFacilitiesBy(searchParam, facilityTypeId, geoZoneId, virtualFacility, enabled);
      return OpenLmisResponse.response("facilityList", facilities);
    } else {
      return OpenLmisResponse.response("message", "too.many.results.found");
    }
  }

  @RequestMapping(value = "/user/facilities", method = GET)
  public List<Facility> getHomeFacility(HttpServletRequest httpServletRequest) {
    return Arrays.asList(facilityService.getHomeFacility(loggedInUserId(httpServletRequest)));
  }

  @RequestMapping(value = "/facilities/reference-data", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public Map getReferenceData() {
    FacilityReferenceData facilityReferenceData = new FacilityReferenceData();
    return facilityReferenceData.addFacilityTypes(facilityService.getAllTypes()).
      addFacilityOperators(facilityService.getAllOperators()).
      addGeographicZones(facilityService.getAllZones()).
      addPrograms(programService.getAll()).get();
  }

  @RequestMapping(value = "/facilities/{id}", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'MANAGE_FACILITY, MANAGE_USER')")
  public ResponseEntity<OpenLmisResponse> getFacility(@PathVariable(value = "id") Long id) {
    return response("facility", facilityService.getById(id));
  }

  @RequestMapping(value = "/create/requisition/supervised/{programId}/facilities.json", method = GET)
  public ResponseEntity<ModelMap> getUserSupervisedFacilitiesSupportingProgram(@PathVariable(value = "programId") Long programId,
                                                                               HttpServletRequest request) {
    ModelMap modelMap = new ModelMap();
    Long userId = loggedInUserId(request);
    List<Facility> facilities = facilityService.getUserSupervisedFacilities(userId, programId, CREATE_REQUISITION,
      AUTHORIZE_REQUISITION);
    modelMap.put("facilities", facilities);
    return new ResponseEntity<>(modelMap, HttpStatus.OK);
  }
  @RequestMapping(value = "/users/{userId}/supervised/{programId}/facilities.json", method = GET)
  public ResponseEntity<ModelMap> getUserSupervisedFacilitiesSupportingProgram(@PathVariable(
        value = "programId") Long programId, @PathVariable("userId") Long userId) {
    ModelMap modelMap = new ModelMap();
    List<Facility> facilities = facilityService.getUserSupervisedFacilities(userId, programId, CREATE_REQUISITION,
            AUTHORIZE_REQUISITION);
    modelMap.put("facilities", facilities);
    return new ResponseEntity<>(modelMap, HttpStatus.OK);
  }

  @RequestMapping(value = "/facilities", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public ResponseEntity insert(@RequestBody Facility facility, HttpServletRequest request) {
    facility.setCreatedBy(loggedInUserId(request));
    facility.setModifiedBy(loggedInUserId(request));
    ResponseEntity<OpenLmisResponse> response;
    try {
      facilityService.update(facility);
    } catch (DataException exception) {
      return createErrorResponse(facility, exception);
    }
    response = success(messageService.message("message.facility.created.success", facility.getName()));
    response.getBody().addData("facility", facility);
    return response;
  }

  @RequestMapping(value = "/facilities/{id}", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public ResponseEntity<OpenLmisResponse> update(@PathVariable("id") Long id,
                                                 @RequestBody Facility facility,
                                                 HttpServletRequest request) {
    facility.setId(id);
    facility.setModifiedBy(loggedInUserId(request));

    try {
      facilityService.update(facility);
    } catch (DataException exception) {
      return createErrorResponse(facility, exception);
    }

    String successMessage = messageService.message("message.facility.updated.success", facility.getName());
    OpenLmisResponse openLmisResponse = new OpenLmisResponse("facility", facility);
    return openLmisResponse.successEntity(successMessage);
  }

  @RequestMapping(value = "/user/facilities/view", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> listForViewing(HttpServletRequest request) {
    return response("facilities",
      facilityService.getForUserAndRights(loggedInUserId(request), VIEW_REQUISITION));
  }

  @RequestMapping(value = "/facilities/{facilityId}", method = DELETE, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public ResponseEntity<OpenLmisResponse> softDelete(HttpServletRequest httpServletRequest,
                                                     @PathVariable Long facilityId) {
    Facility facilityToBeDeleted = createFacilityToBeDeleted(facilityId, loggedInUserId(httpServletRequest));
    facilityService.updateEnabledAndActiveFor(facilityToBeDeleted);
    Facility deletedFacility = facilityService.getById(facilityId);

    String successMessage = messageService.message("disable.facility.success", deletedFacility.getName(), deletedFacility.getCode());
    OpenLmisResponse response = new OpenLmisResponse("facility", deletedFacility);
    return response.successEntity(successMessage);
  }

  @RequestMapping(value = "/facilities/{id}/restore", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public ResponseEntity<OpenLmisResponse> restore(HttpServletRequest request,
                                                  @PathVariable("id") Long facilityId) {
    Facility facilityToBeDeleted = createFacilityToBeRestored(facilityId, loggedInUserId(request));

    facilityService.updateEnabledAndActiveFor(facilityToBeDeleted);

    Facility restoredFacility = facilityService.getById(facilityId);

    String successMessage = messageService.message("enable.facility.success", restoredFacility.getName(),
      restoredFacility.getCode());

    OpenLmisResponse response = new OpenLmisResponse("facility", restoredFacility);
    return response.successEntity(successMessage);
  }

  @RequestMapping(value = "/deliveryZones/{deliveryZoneId}/programs/{programId}/facilities", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_DISTRIBUTION')")
  public ResponseEntity<OpenLmisResponse> getFacilitiesForDeliveryZoneAndProgram(@PathVariable("deliveryZoneId") Long deliveryZoneId,
                                                                                 @PathVariable("programId") Long programId) {
    List<Facility> facilities = facilityService.getAllForDeliveryZoneAndProgram(deliveryZoneId, programId);
    return response("facilities", Facility.filterForActiveProducts(facilities));
  }

  @RequestMapping(value = "/enabledWarehouses", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USER')")
  public ResponseEntity<OpenLmisResponse> getEnabledWarehouses() {
    List<Facility> enabledWarehouses = facilityService.getEnabledWarehouses();
    return response("enabledWarehouses", enabledWarehouses);
  }

  @RequestMapping(value = "/facility-types", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY, MANAGE_SUPERVISORY_NODE, MANAGE_REQUISITION_GROUP, MANAGE_SUPPLY_LINE, MANAGE_FACILITY_APPROVED_PRODUCT, MANAGE_USER')")
  public List<FacilityType> getFacilityTypes() {
    return facilityService.getAllTypes();
  }

  private ResponseEntity<OpenLmisResponse> createErrorResponse(Facility facility, DataException exception) {
    OpenLmisResponse openLmisResponse = new OpenLmisResponse("facility", facility);
    return openLmisResponse.errorEntity(exception, BAD_REQUEST);
  }

  @RequestMapping(value = "/facility-contacts", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getContactsForFacility(@RequestParam("type") String type, @RequestParam("facilityId") Long facilityId) {
    return OpenLmisResponse.response("contacts",facilityService.getContactList(facilityId, type));
  }

    @RequestMapping(value = "/facility-supervisors", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacilitySupervisors(@RequestParam("facilityId") Long facilityId) {
        return OpenLmisResponse.response("supervisors",facilityService.getFacilitySupervisors(facilityId));
    }

  @RequestMapping(value = "/facility-images", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getImagesForFacility(@RequestParam("facilityId") Long facilityId) {
    return OpenLmisResponse.response("images",facilityService.getFacilityImages(facilityId));
  }

  @RequestMapping(value = "/facilityType/{facilityTypeId}/requisitionGroup/{requisitionGroupId}/facilities", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getFacilityByTypeAndRequisitionGroupId(@PathVariable("facilityTypeId") Long facilityTypeId, @PathVariable("requisitionGroupId") Long requisitionGroupId) {
    return OpenLmisResponse.response("facilities",facilityService.getFacilityByTypeAndRequisitionGroupId(facilityTypeId, requisitionGroupId));
  }

    @RequestMapping(value = "/geoFacilityTree", method = GET)
    public ResponseEntity<OpenLmisResponse> getGeoTreeFacility(HttpServletRequest httpServletRequest) {

        ResponseEntity<OpenLmisResponse> response;
        response = OpenLmisResponse.success("");
        response.getBody().addData("regionFacilityTree", facilityService.getGeoRegionFacilityTree(loggedInUserId(httpServletRequest)));
        response.getBody().addData("districtFacility", facilityService.getGeoDistrictFacility(loggedInUserId(httpServletRequest)));
        response.getBody().addData("flatFacility", facilityService.getGeoFlatFacilityTree(loggedInUserId(httpServletRequest)));
        return response;
    }

  @RequestMapping(value = "/user/facilities/view-order-requisition", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> listForViewingOrderRequisition(HttpServletRequest request) {
    return response("facilities",
            facilityService.getForUserAndRights(loggedInUserId(request), VIEW_VACCINE_ORDER_REQUISITION));
  }

}
