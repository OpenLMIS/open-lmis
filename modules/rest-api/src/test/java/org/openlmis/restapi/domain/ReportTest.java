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
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Signature;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.builder.ReportBuilder;
import org.openlmis.rnr.builder.PatientQuantificationsBuilder;
import org.openlmis.rnr.builder.RegimenLineItemBuilder;
import org.openlmis.rnr.builder.RnrLineItemBuilder;
import org.openlmis.rnr.domain.PatientQuantificationLineItem;
import org.openlmis.rnr.domain.RegimenLineItem;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;

import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.openlmis.restapi.builder.ReportBuilder.approverName;
import static org.openlmis.restapi.builder.ReportBuilder.products;

@Category(UnitTests.class)
public class ReportTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private String nullString = null;

  @Test
  public void shouldThrowExceptionIfReportDoesNotContainAgentCode() {
    Report report = make(a(ReportBuilder.defaultReport, with(ReportBuilder.agentCode, nullString)));

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    report.validate();
  }

  @Test
  public void shouldNotThrowExceptionIfReportContainsAgentCodeAndProgramCode() {
    Report report = make(a(ReportBuilder.defaultReport, with(ReportBuilder.agentCode, "123")));
    report.setProgramCode("abc");

    //should not throw an exception here
    report.validate();
  }

  @Test
  public void shouldThrowExceptionIfReportContainsBlankAgentCode() {
    Report report = make(a(ReportBuilder.defaultReport, with(ReportBuilder.agentCode, "")));

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    report.validate();
  }

  @Test
  public void shouldThrowExceptionIfReportDoesNotContainProgramCode() {
    Report report = make(a(ReportBuilder.defaultReport, with(ReportBuilder.programCode, nullString)));

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    report.validate();
  }

  @Test
  public void shouldThrowExceptionIfReportContainsBlankProgramCode() {
    Report report = make(a(ReportBuilder.defaultReport, with(ReportBuilder.programCode, "")));

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    report.validate();
  }

  @Test
  public void shouldGetRequisitionFromReport() throws Exception {
    Report report = make(a(ReportBuilder.defaultReport));
    Long requisitionId = 12345L;
    Long userId = 212345L;

    Rnr requisition = report.getRequisition(requisitionId, userId);

    assertThat(requisition.getId(), is(requisitionId));
    assertThat(requisition.getFullSupplyLineItems(), is(report.getProducts()));
    assertThat(requisition.getModifiedBy(), is(userId));
  }

  @Test
  public void shouldThrowExceptionIfProductsAreMissing() throws Exception {
    List<RnrLineItem> rnrLineItems = null;
    Report report = make(a(ReportBuilder.defaultReport, with(products, rnrLineItems)));

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    report.validateForApproval();
  }

  @Test
  public void shouldThrowExceptionIfProductCodeMissing() throws Exception {
    Report report = make(a(ReportBuilder.defaultReport));
    String productCode = null;
    report.setProducts(asList(make(a(RnrLineItemBuilder.defaultRnrLineItem, with(RnrLineItemBuilder.productCode, productCode)))));

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    report.validateForApproval();
  }

  @Test
  public void shouldThrowExceptionIfApproverNameMissing() throws Exception {
    String nullApproverName = null;
    Report report = make(a(ReportBuilder.defaultReport, with(approverName, nullApproverName)));
    report.setProducts(asList(make(a(RnrLineItemBuilder.defaultRnrLineItem))));

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    report.validateForApproval();
  }

  @Test
  public void shouldThrowExceptionIfQuantityApprovedMissing() throws Exception {
    Report report = make(a(ReportBuilder.defaultReport));
    Integer quantityApproved = null;
    report.setProducts(asList(make(a(RnrLineItemBuilder.defaultRnrLineItem, with(RnrLineItemBuilder.quantityApproved, quantityApproved)))));

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    report.validateForApproval();
  }

  @Test
  public void shouldThrowExceptionIfQuantityApprovedIsNegative() throws Exception {
    Report report = make(a(ReportBuilder.defaultReport));
    Integer quantityApproved = -1;
    report.setProducts(asList(make(a(RnrLineItemBuilder.defaultRnrLineItem, with(RnrLineItemBuilder.quantityApproved, quantityApproved)))));

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.restapi.quantity.approved.negative");

    report.validateForApproval();
  }

  @Test
  public void shouldThrowErrorIfApproverNameEmpty() throws Exception {
    Report report = new Report();
    report.setApproverName("");
    report.setProducts(asList(make(a(RnrLineItemBuilder.defaultRnrLineItem))));

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    report.validateForApproval();
  }

  @Test
  public void shouldConvertRnrToReportForRestResponse() {
    Rnr rnr = new Rnr();
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    Program program = make(a(ProgramBuilder.defaultProgram));
    rnr.setFacility(facility);
    rnr.setProgram(program);
    rnr.setClientSubmittedNotes("abc");
    List<RnrLineItem> rnrLineItems = asList(make(a(RnrLineItemBuilder.defaultRnrLineItem, with(RnrLineItemBuilder.productCode, "P1"))),
        make(a(RnrLineItemBuilder.defaultRnrLineItem, with(RnrLineItemBuilder.productCode, "P2"))),
        make(a(RnrLineItemBuilder.defaultRnrLineItem, with(RnrLineItemBuilder.productCode, "P3"))));
    rnr.setFullSupplyLineItems(rnrLineItems);
    List<RegimenLineItem> regimenLineItems = asList(make(a(RegimenLineItemBuilder.defaultRegimenLineItem)),
        make(a(RegimenLineItemBuilder.defaultRegimenLineItem)));
    rnr.setRegimenLineItems(regimenLineItems);
    List<PatientQuantificationLineItem> patientQuantificationLineItems = asList(make(a(PatientQuantificationsBuilder.defaultPatientQuantificationLineItem)),
        make(a(PatientQuantificationsBuilder.defaultPatientQuantificationLineItem)));
    rnr.setPatientQuantifications(patientQuantificationLineItems);
    rnr.setPeriod(new ProcessingPeriod());
    rnr.setRnrSignatures(asList(new Signature(Signature.Type.SUBMITTER, "abc")));

    Report report = Report.prepareForREST(rnr);

    assertThat(report.getAgentCode(), is(rnr.getFacility().getCode()));
    assertThat(report.getProgramCode(), is(rnr.getProgram().getCode()));
    assertThat(report.getClientSubmittedNotes(), is(rnr.getClientSubmittedNotes()));
    assertThat(report.getProducts().size(), is(3));
    assertThat(report.getProducts().get(0).getProductCode(), is("P1"));
    assertThat(report.getRegimens().size(), is(2));
    assertThat(report.getPatientQuantifications().size(), is(2));
    assertNull(report.getClientSubmittedTime());
    assertThat(report.getRnrSignatures().get(0).getText(), is("abc"));
  }

  @Test
  public void shouldConvertClientSubmittedTimeIfExists() {
    Rnr rnr = new Rnr();
    Date date = DateUtil.parseDate("2011-11-11 11:11:11");
    rnr.setFacility(make(a(FacilityBuilder.defaultFacility)));
    rnr.setProgram(make(a(ProgramBuilder.defaultProgram)));
    rnr.setClientSubmittedTime(date);
    rnr.setPeriod(new ProcessingPeriod());

    Report report = Report.prepareForREST(rnr);
    assertThat(report.getClientSubmittedTime().getTime(), is(date.getTime()));
  }

  @Test
  public void shouldRetrievePeriodStartDateForARequisitionIfExists() {
    Rnr rnr = new Rnr();
    Date date = DateUtil.parseDate("2011-11-11 11:11:11");
    rnr.setFacility(make(a(FacilityBuilder.defaultFacility)));
    rnr.setProgram(make(a(ProgramBuilder.defaultProgram)));
    rnr.setClientSubmittedTime(date);
    ProcessingPeriod period = make(a(ProcessingPeriodBuilder.defaultProcessingPeriod));
    rnr.setPeriod(period);

    Report report = Report.prepareForREST(rnr);
    assertThat(report.getPeriodStartDate(), is(period.getStartDate()));
  }
}

