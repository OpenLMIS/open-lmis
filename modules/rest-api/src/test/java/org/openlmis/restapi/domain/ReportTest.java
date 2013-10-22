/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.restapi.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.builder.ReportBuilder;
import org.openlmis.rnr.domain.Rnr;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
public class ReportTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  private final Long nullLong = null;

  @Test
  public void shouldThrowExceptionIfReportDoesNotContainFacilityId() {
    Report report = make(a(ReportBuilder.defaultReport, with(ReportBuilder.facilityId, nullLong)));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.restapi.mandatory.missing");

    report.validate();
  }

  @Test
  public void shouldThrowExceptionIfReportDoesNotContainProgramId() {
    Report report = make(a(ReportBuilder.defaultReport, with(ReportBuilder.programId, nullLong)));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.restapi.mandatory.missing");

    report.validate();
  }

  @Test
  public void shouldThrowExceptionIfReportDoesNotContainUserId() {
    String nullString = null;
    Report report = make(a(ReportBuilder.defaultReport, with(ReportBuilder.userId, nullString)));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.restapi.mandatory.missing");

    report.validate();
  }

  @Test
  public void shouldThrowExceptionIfReportDoesNotContainPeriodId() {
    Report report = make(a(ReportBuilder.defaultReport, with(ReportBuilder.periodId, nullLong)));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error.restapi.mandatory.missing");

    report.validate();
  }

  @Test
  public void shouldGetRequisitionFromReport() throws Exception {
    Report report = make(a(ReportBuilder.defaultReport));
    Rnr requisition = report.getRequisition();
    assertThat(requisition.getId(), is(report.getRequisitionId()));
    assertThat(requisition.getFullSupplyLineItems(), is(report.getProducts()));
  }
}
