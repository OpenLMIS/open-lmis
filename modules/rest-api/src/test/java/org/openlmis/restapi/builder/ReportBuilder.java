package org.openlmis.restapi.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.Vendor;
import org.openlmis.restapi.domain.Report;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ReportBuilder {

  public static final Property<Report, Integer> facilityId = newProperty();
  public static final Property<Report, Integer> programId = newProperty();
  public static final Property<Report, Integer> periodId = newProperty();
  public static final Property<Report, String> userId = newProperty();
  public static final Property<Report, Vendor> vendor = newProperty();

  public static final Instantiator<Report> defaultReport = new Instantiator<Report>() {
    @Override
    public Report instantiate(PropertyLookup<Report> lookup) {
     Report report = new Report();
      report.setRequisitionId(1);
      report.setFacilityId(lookup.valueOf(facilityId, 1));
      report.setProgramId(lookup.valueOf(programId, 1));
      report.setPeriodId(lookup.valueOf(periodId, 1));
      report.setUserId(lookup.valueOf(userId, "1"));
      Vendor defaultVendor = new Vendor();
      defaultVendor.setId(1);
      defaultVendor.setName("vendor");
      defaultVendor.setAuthToken("authToken");
      report.setVendor(lookup.valueOf(vendor, defaultVendor));
      return report;
    }
  };
}
