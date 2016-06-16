package org.openlmis.web.controller;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Years;
import org.openlmis.authentication.web.PermissionEvaluator;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.DeliveryZoneService;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.core.service.UserService;
import org.openlmis.core.utils.RightUtil;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.FacilityDistribution;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.service.DistributionService;
import org.openlmis.distribution.service.FacilityDistributionService;
import org.openlmis.web.model.ReviewDataColumnOrder;
import org.openlmis.web.model.ReviewDataFilter;
import org.openlmis.web.model.ReviewDataFilters;
import org.openlmis.web.model.SynchronizedDistribution;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.apache.commons.lang3.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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

  @Autowired
  private UserService userService;

  @Autowired
  private FacilityDistributionService facilityDistributionService;

  @Autowired
  private PermissionEvaluator permissionEvaluator;

  @Value("${eligibility.edit}")
  private Long eligibilityEdit;

  @RequestMapping(value = "review-data/filters", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'VIEW_SYNCHRONIZED_DATA, EDIT_SYNCHRONIZED_DATA')")
  public ResponseEntity<OpenLmisResponse> getFilters() {
    List<Program> programs = programService.getAll();
    List<GeographicZone> geographicZones = facilityService.getAllZones();
    List<DeliveryZone> deliveryZones = deliveryZoneService.getAll();

    List<Distribution> distributions = distributionService.getFullSyncedDistributions();
    List<ProcessingPeriod> periods = new ArrayList<>(distributions.size());

    for (Distribution distribution : distributions) {
      periods.add(distribution.getPeriod());
    }

    return response("filter", new ReviewDataFilters(programs, geographicZones, deliveryZones, periods));
  }

  @RequestMapping(value = "review-data/list", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'VIEW_SYNCHRONIZED_DATA, EDIT_SYNCHRONIZED_DATA')")
  public ResponseEntity<OpenLmisResponse> get(@RequestBody ReviewDataFilter filter, HttpServletRequest request) {
    List<Distribution> distributions = distributionService.getFullSyncedDistributions(filter.getProgram(), filter.getDeliveryZone(), filter.getPeriod());
    List<SynchronizedDistribution> list = new ArrayList<>();
    Long userId = loggedInUserId(request);

    for (Distribution distribution : distributions) {
      Map<Long, FacilityDistribution> facilityDistributionMap = facilityDistributionService.getData(distribution);

      for (Map.Entry<Long, FacilityDistribution> entry : facilityDistributionMap.entrySet()) {
        FacilityDistribution value = entry.getValue();

        if (!filter.isProvinceSelected() || value.getGeographicZone().equalsIgnoreCase(filter.getProvince().getName())) {
          FacilityVisit visit = value.getFacilityVisit();

          SynchronizedDistribution item = new SynchronizedDistribution();
          item.setProvince(value.getGeographicZone());
          item.setDeliveryZone(distribution.getDeliveryZone().getName());
          item.setPeriod(distribution.getPeriod().getName());
          item.setInitiated(distribution.getCreatedDate());
          item.setSync(visit.getSyncDate());
          item.setView(isViewable(visit.getSyncDate(), userId));
          item.setEdit(isEditable(visit.getSyncDate(), userId));

          // those values need to be read from edit history
          item.setLastViewed(null);
          item.setLastEdited(null);
          item.setEditedBy(null);

          list.add(item);
        }
      }
    }

    Collections.sort(list, sortByColumn(filter.getOrder()));

    return response("list", list);
  }

  private Comparator<SynchronizedDistribution> sortByColumn(final ReviewDataColumnOrder order) {
    Comparator<SynchronizedDistribution> comparator = new Comparator<SynchronizedDistribution>() {
      @Override
      public int compare(SynchronizedDistribution a, SynchronizedDistribution b) {
        switch (order.getColumn()) {
          case "province":
            return ObjectUtils.compare(a.getProvince(), b.getProvince());
          case "deliveryZone":
            return ObjectUtils.compare(a.getDeliveryZone(), b.getDeliveryZone());
          case "period":
            return ObjectUtils.compare(a.getPeriod(), b.getPeriod());
          case "initiated":
            return ObjectUtils.compare(a.getInitiated(), b.getInitiated());
          case "synchronized":
            return ObjectUtils.compare(a.getSync(), b.getSync());
          case "lastViewed":
            return ObjectUtils.compare(a.getLastViewed(), b.getLastViewed());
          case "lastEdited":
            return ObjectUtils.compare(a.getLastEdited(), b.getLastEdited());
          case "editedBy":
            return ObjectUtils.compare(a.getEditedBy(), b.getEditedBy());
          default:
            return 0;
        }
      }
    };

    return isTrue(order.getDescending()) ? Collections.reverseOrder(comparator) : comparator;
  }

  private boolean isViewable(Date syncDate, Long userId) {
    Months months = Months.monthsBetween(new DateTime(syncDate), DateTime.now());
    boolean hasViewRight = permissionEvaluator.hasPermission(userId, "VIEW_SYNCHRONIZED_DATA");
    boolean hasEditRight = permissionEvaluator.hasPermission(userId, "EDIT_SYNCHRONIZED_DATA");
    return months.getMonths() <= 12 && ((hasViewRight && !hasEditRight) || (hasEditRight && !isEligibility(syncDate)));
  }

  private boolean isEditable(Date syncDate, Long userId) {
    Months months = Months.monthsBetween(new DateTime(syncDate), DateTime.now());
    boolean hasEditRight = permissionEvaluator.hasPermission(userId, "EDIT_SYNCHRONIZED_DATA");
    return months.getMonths() <= 12 && hasEditRight && isEligibility(syncDate);
  }

  private boolean isEligibility(Date syncDate) {
    Days days = Days.daysBetween(new DateTime(syncDate), DateTime.now());
    return days.getDays() <= eligibilityEdit;
  }

}

