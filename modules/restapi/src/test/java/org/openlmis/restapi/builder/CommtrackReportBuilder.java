package org.openlmis.restapi.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.restapi.domain.Report;

import static com.natpryce.makeiteasy.Property.newProperty;

public class CommtrackReportBuilder {

  public static final Property<Report, Integer> facilityId = newProperty();
  public static final Property<Report, Integer> programId = newProperty();
  public static final Property<Report, Integer> periodId = newProperty();
  public static final Property<Report, Integer> userId = newProperty();

  public static final Instantiator<Report> defaultCommtrackReport = new Instantiator<Report>() {
    @Override
    public Report instantiate(PropertyLookup<Report> lookup) {
     Report report = new Report();
      report.setFacilityId(lookup.valueOf(facilityId, 1));
      report.setProgramId(lookup.valueOf(programId, 1));
      report.setPeriodId(lookup.valueOf(periodId, 1));
      report.setUserId(lookup.valueOf(userId, 1));
      return report;
    }
  };
}
