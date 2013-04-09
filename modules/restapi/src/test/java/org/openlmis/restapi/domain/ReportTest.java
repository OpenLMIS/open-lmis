package org.openlmis.restapi.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.restapi.builder.CommtrackReportBuilder;
import org.openlmis.core.exception.DataException;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.openlmis.restapi.domain.Report.ERROR_COMMTRACK_MANDATORY_MISSING;

public class ReportTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldThrowExceptionIfReportDoesNotContainFacilityId(){
    Integer nullInteger = null;
    Report report = make(a(CommtrackReportBuilder.defaultCommtrackReport, with(CommtrackReportBuilder.facilityId, nullInteger)));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage(ERROR_COMMTRACK_MANDATORY_MISSING);

    report.validate();
  }

  @Test
  public void shouldThrowExceptionIfReportDoesNotContainProgramId(){
    Integer nullInteger = null;
    Report report = make(a(CommtrackReportBuilder.defaultCommtrackReport, with(CommtrackReportBuilder.programId, nullInteger)));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage(ERROR_COMMTRACK_MANDATORY_MISSING);

    report.validate();
  }

  @Test
  public void shouldThrowExceptionIfReportDoesNotContainUserId(){
    Integer nullInteger = null;
    Report report = make(a(CommtrackReportBuilder.defaultCommtrackReport, with(CommtrackReportBuilder.userId, nullInteger)));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage(ERROR_COMMTRACK_MANDATORY_MISSING);

    report.validate();
  }

  @Test
  public void shouldThrowExceptionIfReportDoesNotContainPeriodId(){
    Integer nullInteger = null;
    Report report = make(a(CommtrackReportBuilder.defaultCommtrackReport, with(CommtrackReportBuilder.periodId, nullInteger)));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage(ERROR_COMMTRACK_MANDATORY_MISSING);

    report.validate();
  }


}
