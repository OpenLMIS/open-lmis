package org.openlmis.web.controller;

import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.DistributionEdit;
import org.openlmis.distribution.dto.DistributionDTO;
import org.openlmis.distribution.service.DistributionService;
import org.openlmis.web.model.ReviewDataFilter;
import org.openlmis.web.response.OpenLmisResponse;
import org.openlmis.web.service.ReviewDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static org.openlmis.web.response.OpenLmisResponse.SUCCESS;
import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class ReviewDataController extends BaseController {

  @Autowired
  private ReviewDataService reviewDataService;

  @Autowired
  private DistributionService distributionService;

  @RequestMapping(value = "review-data/filters", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'VIEW_SYNCHRONIZED_DATA, EDIT_SYNCHRONIZED_DATA')")
  public ResponseEntity<OpenLmisResponse> getFilters() {
    return response("filter", reviewDataService.getFilters());
  }

  @RequestMapping(value = "review-data/list", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'VIEW_SYNCHRONIZED_DATA, EDIT_SYNCHRONIZED_DATA')")
  public ResponseEntity<OpenLmisResponse> get(@RequestBody ReviewDataFilter filter, HttpServletRequest request) {
    return response("list", reviewDataService.get(filter, loggedInUserId(request)));
  }

  @RequestMapping(value = "review-data/distribution/check", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'VIEW_SYNCHRONIZED_DATA, EDIT_SYNCHRONIZED_DATA')")
  public ResponseEntity<OpenLmisResponse> check(@RequestBody Distribution distribution, HttpServletRequest request) {
    DistributionEdit distributionEdit = reviewDataService.checkInProgress(distribution, loggedInUserId(request));
    OpenLmisResponse response = new OpenLmisResponse();

    if (null != distributionEdit) {
      response.addData("inProgress", distributionEdit);
    }

    return response.response(OK);
  }

  @RequestMapping(value = "review-data/distribution/get", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'VIEW_SYNCHRONIZED_DATA, EDIT_SYNCHRONIZED_DATA')")
  public ResponseEntity<OpenLmisResponse> getDistribution(@RequestBody Distribution distribution, HttpServletRequest request) {
    OpenLmisResponse openLmisResponse = new OpenLmisResponse("distribution", reviewDataService.getDistribution(distribution, loggedInUserId(request)));
    openLmisResponse.addData(SUCCESS, messageService.message("message.distribution.created.success",
        distribution.getDeliveryZone().getName(), distribution.getProgram().getName(), distribution.getPeriod().getName()));
    return openLmisResponse.response(OK);
  }

  @RequestMapping(value = "review-data/distribution/sync", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'VIEW_SYNCHRONIZED_DATA, EDIT_SYNCHRONIZED_DATA')")
  public ResponseEntity<OpenLmisResponse> sync(@RequestBody DistributionDTO distribution, HttpServletRequest request) {
    reviewDataService.deleteDistributionEdit(distribution.getId(), loggedInUserId(request));

    try {
      OpenLmisResponse response = new OpenLmisResponse();
      response.addData(SUCCESS, "");

      return response.response(OK);
    } catch (Exception e) {
      return OpenLmisResponse.error(e.getMessage(), CONFLICT);
    }
  }

  @RequestMapping(value = "review-data/distribution/lastViewed", method = POST)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'VIEW_SYNCHRONIZED_DATA, EDIT_SYNCHRONIZED_DATA')")
  @ResponseStatus(OK)
  public void updateLastViewed(@RequestBody Long distributionId) {
    distributionService.updateLastViewed(distributionId);
  }

}

