package org.openlmis.web.controller;

import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.DeliveryZoneService;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.service.DistributionService;
import org.openlmis.web.model.ReviewDataFilter;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class ReviewDataController extends BaseController {

    @Autowired
    private ProgramService programService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private DeliveryZoneService deliveryZoneService;

    @Autowired
    private DistributionService distributionService;

    @RequestMapping(value = "review-data/filters", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'VIEW_SYNCHRONIZED_DATA, EDIT_SYNCHRONIZED_DATA')")
    public ResponseEntity<OpenLmisResponse> getActiveProgramsForDeliveryZone() {
        List<Program> programs = programService.getAll();
        List<GeographicZone> geographicZones = facilityService.getAllZones();
        List<DeliveryZone> deliveryZones = deliveryZoneService.getAll();

        List<Distribution> distributions = distributionService.getFullSyncedDistributions();
        List<ProcessingPeriod> periods = new ArrayList<>(distributions.size());

        for (Distribution distribution : distributions) {
            periods.add(distribution.getPeriod());
        }

        return response("filter", new ReviewDataFilter(programs, geographicZones, deliveryZones, periods));
    }

}
