package org.openlmis.restapi.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.domain.Vendor;
import org.openlmis.restapi.builder.ReportBuilder;
import org.openlmis.core.exception.DataException;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
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
    Report report = make(a(ReportBuilder.defaultReport, with(ReportBuilder.vendor, nullVendor)));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage(ERROR_MANDATORY_FIELD_MISSING);

    report.validate();
  }

}
