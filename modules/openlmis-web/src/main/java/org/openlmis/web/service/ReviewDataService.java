package org.openlmis.web.service;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.openlmis.authentication.web.PermissionEvaluator;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.DeliveryZoneService;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.UserService;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.FacilityDistribution;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.dto.DistributionDTO;
import org.openlmis.distribution.service.DistributionService;
import org.openlmis.distribution.service.FacilityDistributionService;
import org.openlmis.web.model.ReviewDataFilter;
import org.openlmis.web.model.ReviewDataFilters;
import org.openlmis.web.model.SynchronizedDistribution;
import org.openlmis.web.util.SynchronizedDistributionComparators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.openlmis.core.domain.RightName.EDIT_SYNCHRONIZED_DATA;
import static org.openlmis.core.domain.RightName.VIEW_SYNCHRONIZED_DATA;

@Service
public class ReviewDataService {

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

  public ReviewDataFilters getFilters() {
    List<Program> programs = programService.getAll();
    List<GeographicZone> geographicZones = facilityService.getAllZones();
    List<DeliveryZone> deliveryZones = deliveryZoneService.getAll();

    List<Distribution> distributions = distributionService.getFullSyncedDistributions();
    List<ProcessingPeriod> periods = new ArrayList<>(distributions.size());

    for (Distribution distribution : distributions) {
      periods.add(distribution.getPeriod());
    }

    return new ReviewDataFilters(programs, geographicZones, deliveryZones, periods);
  }

  public List<SynchronizedDistribution> get(ReviewDataFilter filter, Long userId) {
    List<Distribution> distributions = distributionService.getFullSyncedDistributions(filter.getProgram(), filter.getDeliveryZone(), filter.getPeriod());
    List<SynchronizedDistribution> list = new ArrayList<>();

    for (Distribution distribution : distributions) {
      Map<Long, FacilityDistribution> facilityDistributionMap = facilityDistributionService.getData(distribution);
      Iterator<Map.Entry<Long, FacilityDistribution>> iterator = facilityDistributionMap.entrySet().iterator();

      if (!iterator.hasNext()) {
        continue;
      }

      FacilityDistribution value = iterator.next().getValue();
      String geographicZone = value.getGeographicZone();

      if (!filter.isProvinceSelected() || geographicZone.equalsIgnoreCase(filter.getProvince().getName())) {
        list.add(create(userId, distribution, geographicZone));
      }
    }

    Collections.sort(list, SynchronizedDistributionComparators.get(filter.getOrder()));
    return list;
  }

  public DistributionDTO getDistribution(Distribution arg) {
    Distribution distribution = distributionService.getFullSyncedDistribution(arg);
    Map<Long, FacilityDistribution> facilityDistributionMap = facilityDistributionService.getData(distribution);

    distribution.setFacilityDistributions(facilityDistributionMap);

    return distribution.transform();
  }

  private SynchronizedDistribution create(Long userId, Distribution distribution, String geographicZone) {
    SynchronizedDistribution item = new SynchronizedDistribution();

    item.setProvince(geographicZone);
    item.setDeliveryZone(distribution.getDeliveryZone());
    item.setPeriod(distribution.getPeriod());

    item.setInitiated(distribution.getCreatedDate());
    item.setSync(distribution.getSyncDate());

    item.setView(isViewable(distribution.getSyncDate(), userId));
    item.setEdit(isEditable(distribution.getSyncDate(), userId));

    item.setLastViewed(distribution.getLastViewed());

    // those values need to be read from edit history
    item.setLastEdited(null);
    item.setEditedBy(null);

    return item;
  }

  private boolean isViewable(Date syncDate, Long userId) {
    Months months = Months.monthsBetween(new DateTime(syncDate), DateTime.now());
    boolean hasViewRight = permissionEvaluator.hasPermission(userId, VIEW_SYNCHRONIZED_DATA);
    boolean hasEditRight = permissionEvaluator.hasPermission(userId, EDIT_SYNCHRONIZED_DATA);
    return months.getMonths() <= 12 && ((hasViewRight && !hasEditRight) || (hasEditRight && !isEligibility(syncDate)));
  }

  private boolean isEditable(Date syncDate, Long userId) {
    Months months = Months.monthsBetween(new DateTime(syncDate), DateTime.now());
    boolean hasEditRight = permissionEvaluator.hasPermission(userId, EDIT_SYNCHRONIZED_DATA);
    return months.getMonths() <= 12 && hasEditRight && isEligibility(syncDate);
  }

  private boolean isEligibility(Date syncDate) {
    Days days = Days.daysBetween(new DateTime(syncDate), DateTime.now());
    return days.getDays() <= eligibilityEdit;
  }

}
