/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.web.controller.equipment;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.equipment.domain.EquipmentInventory;
import org.openlmis.equipment.service.EquipmentInventoryService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.lang.Integer.parseInt;
import static org.openlmis.core.domain.RightName.MANAGE_EQUIPMENT_INVENTORY;

@Controller
@RequestMapping(value="/equipment/inventory/")
public class EquipmentInventoryController extends BaseController {

  public static final String PROGRAMS = "programs";
  public static final String INVENTORY = "inventory";
  public static final String FACILITIES = "facilities";
  public static final String PAGINATION = "pagination";

  @Autowired
  private EquipmentInventoryService service;

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private ProgramService programService;

  @RequestMapping(value="list", method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getInventory(@RequestParam("typeId") Long typeId,
                                                       @RequestParam("programId") Long programId,
                                                       @RequestParam("equipmentTypeId") Long equipmentTypeId,
                                                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                       @Value("${search.page.size}") String limit,
                                                       HttpServletRequest request){
    Long userId = loggedInUserId(request);
    Pagination pagination = new Pagination(page, parseInt(limit));
    pagination.setTotalRecords(service.getInventoryCount(userId, typeId, programId, equipmentTypeId));
    List<EquipmentInventory> inventory = service.getInventory(userId, typeId, programId, equipmentTypeId, pagination);
    ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response(INVENTORY, inventory);
    response.getBody().addData(PAGINATION, pagination);
    return response;
  }

  @RequestMapping(value= PROGRAMS, method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getPrograms(HttpServletRequest request){
    Long userId = loggedInUserId(request);
    return OpenLmisResponse.response(PROGRAMS,programService.getProgramForSupervisedFacilities(userId, MANAGE_EQUIPMENT_INVENTORY));
  }

  @RequestMapping(value="facility/programs", method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getProgramsForFacility(@RequestParam("facilityId") Long facilityId, HttpServletRequest request){
    Long userId = loggedInUserId(request);
    return OpenLmisResponse.response(PROGRAMS,programService.getProgramsForUserByFacilityAndRights(facilityId, userId, MANAGE_EQUIPMENT_INVENTORY));
  }

  @RequestMapping(value="supervised/facilities", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_INVENTORY')")
  public ResponseEntity<ModelMap> getFacilities(@RequestParam("programId") Long programId,  HttpServletRequest request){
    ModelMap modelMap = new ModelMap();
    Long userId = loggedInUserId(request);
    List<Facility> facilities = facilityService.getUserSupervisedFacilities(userId, programId, MANAGE_EQUIPMENT_INVENTORY);
    modelMap.put(FACILITIES, facilities);
    return new ResponseEntity<>(modelMap, HttpStatus.OK);
  }

  @RequestMapping(value="by-id", method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_INVENTORY')")
  public ResponseEntity<OpenLmisResponse> getInventory(@RequestParam("id") Long id){
    return OpenLmisResponse.response(INVENTORY, service.getInventoryById(id));
  }

  @RequestMapping(value="save", method = RequestMethod.POST)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_INVENTORY')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody EquipmentInventory inventory, HttpServletRequest request){
    ResponseEntity<OpenLmisResponse> response;
    Long userId = loggedInUserId(request);
    inventory.setCreatedBy(userId);
    inventory.setModifiedBy(userId);
    service.save(inventory);
    service.updateNonFunctionalEquipments();
    response = OpenLmisResponse.success(messageService.message("message.equipment.inventory.saved"));
    response.getBody().addData(INVENTORY, inventory);
    return response;
  }

  @RequestMapping(value="status/update", method = RequestMethod.POST)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_INVENTORY')")
  public ResponseEntity<OpenLmisResponse> updateStatus(@RequestBody EquipmentInventory inventory, HttpServletRequest request){
    ResponseEntity<OpenLmisResponse> response;
    Long userId = loggedInUserId(request);
    inventory.setModifiedBy(userId);
    service.updateStatus(inventory);
    service.updateNonFunctionalEquipments();
    response = OpenLmisResponse.success(messageService.message("message.equipment.inventory.saved"));
    response.getBody().addData(INVENTORY, inventory);
    return response;
  }

}
