package org.openlmis.restapi.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.domain.Vendor;
import org.openlmis.restapi.builder.ReportBuilder;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.domain.Rnr;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.restapi.builder.ReportBuilder.vendor;
import static org.openlmis.restapi.domain.Report.ERROR_MANDATORY_FIELD_MISSING;

public class ReportTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldThrowExceptionIfReportDoesNotContainFacilityId(){
    Integer nullInteger = null;
    Report report = make(a(ReportBuilder.defaultReport, with(ReportBuilder.facilityId, nullInteger)));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage(ERROR_MANDATORY_FIELD_MISSING);

    report.validate();
  }

  @Test
  public void shouldThrowExceptionIfReportDoesNotContainProgramId(){
    Integer nullInteger = null;
    Report report = make(a(ReportBuilder.defaultReport, with(ReportBuilder.programId, nullInteger)));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage(ERROR_MANDATORY_FIELD_MISSING);

    report.validate();
  }

  @Test
  public void shouldThrowExceptionIfReportDoesNotContainUserId(){
    String nullInteger = null;
    Report report = make(a(ReportBuilder.defaultReport, with(ReportBuilder.userId, nullInteger)));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage(ERROR_MANDATORY_FIELD_MISSING);

    report.validate();
  }

  @Test
  public void shouldThrowExceptionIfReportDoesNotContainPeriodId(){
    Integer nullInteger = null;
    Report report = make(a(ReportBuilder.defaultReport, with(ReportBuilder.periodId, nullInteger)));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage(ERROR_MANDATORY_FIELD_MISSING);

    report.validate();
  }

  @Test
  public void shouldThrowExceptionIfReportDoesNotContainVendorId(){
    Vendor nullVendor = null;
    Report report = make(a(ReportBuilder.defaultReport, with(vendor, nullVendor)));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage(ERROR_MANDATORY_FIELD_MISSING);

    report.validate();
  }

  @Test
  public void shouldGetRequisitionFromReport() throws Exception {
    Report report = make(a(ReportBuilder.defaultReport, with(vendor, new Vendor())));
    Rnr requisition = report.getRequisition();
    assertThat(requisition.getId(), is(report.getRequisitionId()));
    assertThat(requisition.getFullSupplyLineItems(), is(report.getProducts()));
  }
}
