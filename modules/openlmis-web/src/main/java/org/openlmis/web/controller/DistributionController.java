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
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.UserService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.FacilityDistribution;
import org.openlmis.distribution.dto.FacilityDistributionDTO;
import org.openlmis.distribution.service.DistributionService;
import org.openlmis.distribution.service.FacilityDistributionService;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Map;

import static org.openlmis.core.web.OpenLmisResponse.SUCCESS;
import static org.openlmis.core.web.OpenLmisResponse.response;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * This controller handles endpoint related to initiate, sync a distribution.
 */

@Controller
@NoArgsConstructor
public class DistributionController extends BaseController {

  @Autowired
  DistributionService distributionService;

  @Autowired
  private FacilityDistributionService facilityDistributionService;

  @Autowired
  UserService userService;

  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

  @RequestMapping(value = "/distributions", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@distributionPermissionService.hasPermission(principal, 'MANAGE_DISTRIBUTION', #distribution)")
  public ResponseEntity<OpenLmisResponse> create(@RequestBody Distribution distribution, HttpServletRequest request) {
    Distribution existingDistribution = distributionService.get(distribution);

    if (existingDistribution != null) {
      return returnInitiatedDistribution(distribution, existingDistribution);
    }

    distribution.setCreatedBy(loggedInUserId(request));
    distribution.setModifiedBy(loggedInUserId(request));
    try {
    Distribution initiatedDistribution = distributionService.create(distribution);

    OpenLmisResponse openLmisResponse = new OpenLmisResponse("distribution", initiatedDistribution);
    openLmisResponse.addData(SUCCESS, messageService.message("message.distribution.created.success",
      distribution.getDeliveryZone().getName(), distribution.getProgram().getName(), distribution.getPeriod().getName()));
    return openLmisResponse.response(CREATED);
    } catch (DataException dataException) {
      return OpenLmisResponse.error(dataException.getLocalizedMessage(), PRECONDITION_FAILED);
    }
  }

  @RequestMapping(value = "/distributions/{id}/facilities/{facilityId}", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@distributionPermissionService.hasPermission(principal, 'MANAGE_DISTRIBUTION', #id)")
  public ResponseEntity<OpenLmisResponse> sync(@RequestBody FacilityDistributionDTO facilityDistributionDTO, @PathVariable Long id,
                                               @PathVariable Long facilityId, HttpServletRequest httpServletRequest) {
    ResponseEntity<OpenLmisResponse> response;
    try {
      facilityDistributionDTO.getFacilityVisit().setFacilityId(facilityId);
      facilityDistributionDTO.setDistributionId(id);
      facilityDistributionDTO.setModifiedBy(loggedInUserId(httpServletRequest));
      distributionService.sync(facilityDistributionDTO.transform());
      response = response("syncStatus", true);
    } catch (DataException e) {
      response = response("syncStatus", false);
    }
    response.getBody().addData("distributionStatus", distributionService.updateDistributionStatus(id, loggedInUserId(httpServletRequest)));
    return response;
  }

  private ResponseEntity<OpenLmisResponse> returnInitiatedDistribution(Distribution distribution, Distribution existingDistribution) {

    existingDistribution.setDeliveryZone(distribution.getDeliveryZone());
    existingDistribution.setPeriod(distribution.getPeriod());
    existingDistribution.setProgram(distribution.getProgram());

    Map<Long, FacilityDistribution> facilityDistributions = facilityDistributionService.get(existingDistribution);
    existingDistribution.setFacilityDistributions(facilityDistributions);

    OpenLmisResponse openLmisResponse = new OpenLmisResponse("distribution", existingDistribution);

    User createdByUser = userService.getById(existingDistribution.getCreatedBy());

    openLmisResponse.addData("message", messageService.message("message.distribution.already.exists",
      createdByUser.getUserName(), DATE_FORMAT.format(existingDistribution.getCreatedDate())));

    openLmisResponse.addData(SUCCESS, messageService.message("message.distribution.created.success",
      distribution.getDeliveryZone().getName(), distribution.getProgram().getName(), distribution.getPeriod().getName()));

    return openLmisResponse.response(OK);
  }
}
