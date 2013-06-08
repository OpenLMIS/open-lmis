package org.openlmis.restapi.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.Vendor;
import org.openlmis.restapi.domain.Report;

import static com.natpryce.makeiteasy.Property.newProperty;

public class ReportBuilder {

  public static final Property<Report, Long> facilityId = newProperty();
  public static final Property<Report, Long> programId = newProperty();
  public static final Property<Report, Long> periodId = newProperty();
  public static final Property<Report, String> userId = newProperty();
  public static final Property<Report, Vendor> vendor = newProperty();

  public static final Instantiator<Report> defaultReport = new Instantiator<Report>() {
    @Override
    public Report instantiate(PropertyLookup<Report> lookup) {
     Report report = new Report();
      report.setRequisitionId(1L);
      report.setFacilityId(lookup.valueOf(facilityId, 1L));
      report.setProgramId(lookup.valueOf(programId, 1L));
      report.setPeriodId(lookup.valueOf(periodId, 1L));
      report.setUserId(lookup.valueOf(userId, "1"));
      Vendor defaultVendor = new Vendor();
      defaultVendor.setId(1L);
      defaultVendor.setName("vendor");
      defaultVendor.setAuthToken("authToken");
      report.setVendor(lookup.valueOf(vendor, defaultVendor));
      return report;
    }
  };
}
