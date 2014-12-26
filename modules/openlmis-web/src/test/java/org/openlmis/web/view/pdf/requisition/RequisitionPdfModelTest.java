/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.view.pdf.requisition;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.builder.RnrLineItemBuilder;
import org.openlmis.rnr.domain.*;
import org.openlmis.web.controller.RequisitionController;

import java.math.BigDecimal;
import java.util.*;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.UserBuilder.*;
import static org.openlmis.rnr.builder.RegimenColumnBuilder.*;
import static org.openlmis.rnr.builder.RequisitionBuilder.*;
import static org.openlmis.rnr.builder.RnrTemplateBuilder.defaultRnrTemplate;
import static org.openlmis.rnr.domain.RnrStatus.*;
import static org.openlmis.web.view.pdf.requisition.RequisitionPdfModel.DATE_FORMAT;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RequisitionPdfModelTest {

  @Mock
  MessageService messageService;

  @Mock
  private ConfigurationSettingService configService;

  private Map<String, Object> model;
  private Rnr requisition;
  private RequisitionPdfModel requisitionPdfModel;
  private List<LossesAndAdjustmentsType> lossesAndAdjustmentsList;
  private Date currentDate;
  private Date authorizedDate;

  @Before
  public void setUp() throws Exception {
    Facility f1 = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.name, "F1")));
    requisition = spy(make(a(rnrWithRegimens, with(facility, f1), with(emergency, Boolean.TRUE))));
    model = new HashMap<>();
    model.put(RequisitionController.CURRENCY, "$");
    model.put(RequisitionController.RNR, requisition);
    List<? extends Column> rnrTemplate = make(a(defaultRnrTemplate)).getColumns();
    RegimenColumn regimenColumn1 = make(a(defaultRegimenColumn, with(name, "name"), with(label, "name")));
    RegimenColumn regimenColumn2 = make(a(defaultRegimenColumn, with(name, "code"), with(label, "code")));
    RegimenColumn regimenColumn3 = make(a(defaultRegimenColumn));
    RegimenColumn regimenColumn4 = make(a(defaultRegimenColumn, with(name, "patientsToInitiateTreatment"), with(label, "initiate treatment")));
    RegimenColumn regimenColumn5 = make(a(defaultRegimenColumn, with(name, "patientsStoppedTreatment"), with(label, "stopped treatment")));
    RegimenColumn regimenColumn6 = make(a(defaultRegimenColumn, with(name, "remarks"), with(label, "remarks")));
    List<RegimenColumn> regimenColumnList = Arrays.asList(regimenColumn1, regimenColumn2, regimenColumn3, regimenColumn4, regimenColumn5, regimenColumn6);
    model.put(RequisitionController.RNR_TEMPLATE, rnrTemplate);
    model.put(RequisitionController.REGIMEN_TEMPLATE, regimenColumnList);
    LossesAndAdjustmentsType additive1 = new LossesAndAdjustmentsType("TRANSFER_IN", "TRANSFER IN", true, 1);
    lossesAndAdjustmentsList = asList(additive1);
    model.put(RequisitionController.LOSSES_AND_ADJUSTMENT_TYPES, lossesAndAdjustmentsList);

    currentDate = new Date();
    authorizedDate = new Date();

    model.put("statusChanges", getRequisitionStatusChanges());
    requisitionPdfModel = new RequisitionPdfModel(model, messageService, configService);
  }

  private List<RequisitionStatusChange> getRequisitionStatusChanges() {
    User initiatingUser = make(a(defaultUser, with(firstName, "init-firstName"), with(lastName, "init-lastName")));
    User submittingUser = make(a(defaultUser, with(firstName, "submit-firstName"), with(lastName, "submit-lastName")));
    User authorizingUser = make(a(defaultUser, with(firstName, "auth-firstName"), with(lastName, "auth-lastName")));

    final RequisitionStatusChange initiatedStatusChange = new RequisitionStatusChange(2L, INITIATED, initiatingUser, currentDate);
    final RequisitionStatusChange submittedStatusChange = new RequisitionStatusChange(2L, SUBMITTED, submittingUser, currentDate);
    final RequisitionStatusChange authorizedStatusChange = new RequisitionStatusChange(2L, AUTHORIZED, authorizingUser, authorizedDate);

    return new ArrayList<RequisitionStatusChange>() {{
      add(initiatedStatusChange);
      add(submittedStatusChange);
      add(authorizedStatusChange);
    }};
  }

  @Test
  public void shouldGetHeaderForEmergencyRnr() throws Exception {
    mockMessageServiceCalls();
    when(messageService.message("requisition.type.emergency")).thenReturn("Emergency");

    PdfPTable header = requisitionPdfModel.getRequisitionHeader();
    assertRowValues(header.getRow(0), "Report and Requisition for: Yellow Fever (Warehouse)");
    assertRowValues(header.getRow(1), "Facility: F1", "Operated By: MOH", "Maximum Stock level: 100", "Emergency Order Point: 50.5");
    assertRowValues(header.getRow(2), "Facility Code: F10010", "levelName: Arusha", "parentLevelName: Zambia", "Reporting Period: 01/01/2012 - 01/02/2012", "Requisition Type: Emergency");
    assertThat(header.getSpacingAfter(), is(RequisitionPdfModel.PARAGRAPH_SPACING));
  }

  @Test
  public void shouldGetHeaderForRegularRnr() throws Exception {
    mockMessageServiceCalls();
    when(messageService.message("requisition.type.regular")).thenReturn("Regular");

    requisition.setEmergency(false);

    PdfPTable header = requisitionPdfModel.getRequisitionHeader();
    assertRowValues(header.getRow(0), "Report and Requisition for: Yellow Fever (Warehouse)");
    assertRowValues(header.getRow(1), "Facility: F1", "Operated By: MOH", "Maximum Stock level: 100", "Emergency Order Point: 50.5");
    assertRowValues(header.getRow(2), "Facility Code: F10010", "levelName: Arusha", "parentLevelName: Zambia", "Reporting Period: 01/01/2012 - 01/02/2012", "Requisition Type: Regular");
    assertThat(header.getSpacingAfter(), is(RequisitionPdfModel.PARAGRAPH_SPACING));
  }

  private void mockMessageServiceCalls() {
    when(messageService.message("label.requisition")).thenReturn("Report and Requisition for");
    when(messageService.message("label.facility")).thenReturn("Facility");
    when(messageService.message("create.facility.operatedBy")).thenReturn("Operated By");
    when(messageService.message("label.facility.maximumStock")).thenReturn("Maximum Stock level");
    when(messageService.message("label.emergency.order.point")).thenReturn("Emergency Order Point");
    when(messageService.message("label.facility.reportingPeriod")).thenReturn("Reporting Period");
    when(messageService.message("label.requisition.type")).thenReturn("Requisition Type");
    when(messageService.message("header.facility.code")).thenReturn("Facility Code");
  }

  @Test
  public void shouldGetSummary() throws Exception {
    when(messageService.message("label.summary")).thenReturn("Summary");
    when(messageService.message("label.total.cost.full.supply.items")).thenReturn("Total Cost For Full Supply Items");
    when(messageService.message("label.total.cost.non.full.supply.items")).thenReturn("Total Cost For Non Full Supply Items");
    when(messageService.message("label.total.cost")).thenReturn("Total Cost");
    when(messageService.message("label.submitted.by")).thenReturn("Submitted By");
    when(messageService.message("label.authorized.by")).thenReturn("Authorized By");
    when(messageService.message("label.date")).thenReturn("Date");
    when(messageService.message("label.currency.symbol")).thenReturn("$");

    PdfPTable summary = requisitionPdfModel.getSummary();

    verify(requisition).fillFullSupplyCost();
    verify(requisition).fillNonFullSupplyCost();

    assertRowValues(summary.getRow(0), "Summary");
    assertRowValues(summary.getRow(1), "Total Cost For Full Supply Items", "$8.00");
    assertRowValues(summary.getRow(2), "Total Cost For Non Full Supply Items", "$0.00");
    assertRowValues(summary.getRow(3), "Total Cost", "$8.00");
    assertRowValues(summary.getRow(4), " ", " ");
    assertRowValues(summary.getRow(5), " ", " ");
    assertRowValues(summary.getRow(6), "Submitted By: submit-firstName submit-lastName", "Date: " + DATE_FORMAT.format(currentDate.getTime()));
    assertRowValues(summary.getRow(7), " ", " ");
    assertRowValues(summary.getRow(8), "Authorized By: auth-firstName auth-lastName", "Date: " + DATE_FORMAT.format(authorizedDate.getTime()));
  }

  @Test
  public void shouldGetFullSupplyHeader() throws Exception {
    when(messageService.message("label.full.supply.products")).thenReturn("Full supply product(s)");
    Paragraph fullSupplyHeader = requisitionPdfModel.getFullSupplyHeader();
    assertThat(fullSupplyHeader.getContent(), is("Full supply product(s)"));
  }

  @Test
  public void shouldGetNonFullSupplyHeader() throws Exception {
    when(messageService.message("label.non.full.supply.products")).thenReturn("Non-Full supply product(s)");
    Paragraph nonFullSupplyHeader = requisitionPdfModel.getNonFullSupplyHeader();
    assertThat(nonFullSupplyHeader.getContent(), is("Non-Full supply product(s)"));
  }

  @Test
  public void shouldGetRegimenHeader() throws Exception {
    when(messageService.message("label.regimens")).thenReturn("Regimen(s)");
    Paragraph regimenHeader = requisitionPdfModel.getRegimenHeader();
    assertThat(regimenHeader.getContent(), is("Regimen(s)"));
  }

  @Test
  public void shouldGetFullSupplyLineItems() throws Exception {
    PdfPTable fullSupplyTable = requisitionPdfModel.getFullSupplyTable();
    assertRowValues(fullSupplyTable.getRow(0), "Requested Quantity", "stockOutDays", "stock in hand", "quantity received", "beginning balance", "losses and adjustment");
    assertRowValues(fullSupplyTable.getRow(1), " ");
    assertRowValues(fullSupplyTable.getRow(2), "C1");
    assertRowValues(fullSupplyTable.getRow(3), "6", "3", "4", "3", "10", "1");

    assertThat(fullSupplyTable.getRows().size(), is(requisition.getFullSupplyLineItems().size() + 3));
  }

  @Test
  public void shouldGetNonFullSupplyLineItems() throws Exception {
    RnrLineItem lineItem = make(a(RnrLineItemBuilder.defaultRnrLineItem));
    requisition.add(lineItem, false);
    PdfPTable nonFullSupplyTable = requisitionPdfModel.getNonFullSupplyTable();

    assertRowValues(nonFullSupplyTable.getRow(0), "Requested Quantity");
    assertRowValues(nonFullSupplyTable.getRow(1), " ");
    assertRowValues(nonFullSupplyTable.getRow(2), "C1");
    assertRowValues(nonFullSupplyTable.getRow(3), "6");

    assertThat(nonFullSupplyTable.getRows().size(), is(requisition.getNonFullSupplyLineItems().size() + 3));
  }

  @Test
  public void shouldGetRegimenLineItems() throws Exception {
    PdfPTable regimenTable = requisitionPdfModel.getRegimenTable();
    assertRowValues(regimenTable.getRow(0), "name", "code", "patients on treatment", "initiate treatment", "stopped treatment", "remarks");
    assertRowValues(regimenTable.getRow(1), " ");
    assertRowValues(regimenTable.getRow(2), "Category Name");
    assertRowValues(regimenTable.getRow(3), "Regimen", "R01", "3", "3", "3", "remarks");
  }

  @Test
  public void shouldShowAllocatedBudgetIfBudgetAppliesForRegularRnr() throws DocumentException {
    when(messageService.message("label.summary")).thenReturn("Summary");
    when(messageService.message("label.total.cost.full.supply.items")).thenReturn("Total Cost For Full Supply Items");
    when(messageService.message("label.total.cost.non.full.supply.items")).thenReturn("Total Cost For Non Full Supply Items");
    when(messageService.message("label.total.cost")).thenReturn("Total Cost");
    when(messageService.message("label.submitted.by")).thenReturn("Submitted By");
    when(messageService.message("label.authorized.by")).thenReturn("Authorized By");
    when(messageService.message("label.date")).thenReturn("Date");
    when(messageService.message("label.currency.symbol")).thenReturn("$");
    when(messageService.message("label.allocated.budget")).thenReturn("Allocated Budget");
    when(messageService.message("msg.cost.exceeds.budget")).thenReturn("The total cost exceeds the allocated budget");

    Program requisitionProgram = mock(Program.class);
    when(requisition.getProgram()).thenReturn(requisitionProgram);
    when(requisition.isEmergency()).thenReturn(false);
    when(requisitionProgram.getBudgetingApplies()).thenReturn(true);
    when(requisition.getAllocatedBudget()).thenReturn(new BigDecimal(7));

    PdfPTable summary = requisitionPdfModel.getSummary();

    assertRowValues(summary.getRow(0), "Summary");
    assertRowValues(summary.getRow(1), "Allocated Budget", "$7.00");
    assertRowValues(summary.getRow(2), "Total Cost For Full Supply Items", "$8.00");
    assertRowValues(summary.getRow(3), "Total Cost For Non Full Supply Items", "$0.00");
    assertRowValues(summary.getRow(4), "Total Cost", "$8.00");
    assertRowValues(summary.getRow(5), "The total cost exceeds the allocated budget", " ");
    assertRowValues(summary.getRow(6), " ", " ");
    assertRowValues(summary.getRow(7), " ", " ");
    assertRowValues(summary.getRow(8), "Submitted By: submit-firstName submit-lastName", "Date: " + DATE_FORMAT.format(currentDate.getTime()));
    assertRowValues(summary.getRow(9), " ", " ");
    assertRowValues(summary.getRow(10), "Authorized By: auth-firstName auth-lastName", "Date: " + DATE_FORMAT.format(authorizedDate.getTime()));
  }

  @Test
  public void shouldShowAllocatedBudgetAsNotAllocatedIfBudgetAppliesForRegularRnrAndNotProvided() throws DocumentException {
    when(messageService.message("label.summary")).thenReturn("Summary");
    when(messageService.message("label.total.cost.full.supply.items")).thenReturn("Total Cost For Full Supply Items");
    when(messageService.message("label.total.cost.non.full.supply.items")).thenReturn("Total Cost For Non Full Supply Items");
    when(messageService.message("label.total.cost")).thenReturn("Total Cost");
    when(messageService.message("label.submitted.by")).thenReturn("Submitted By");
    when(messageService.message("label.authorized.by")).thenReturn("Authorized By");
    when(messageService.message("label.date")).thenReturn("Date");
    when(messageService.message("label.currency.symbol")).thenReturn("$");
    when(messageService.message("label.allocated.budget")).thenReturn("Allocated Budget");
    when(messageService.message("msg.cost.exceeds.budget")).thenReturn("The total cost exceeds the allocated budget");
    when(messageService.message("msg.budget.not.allocated")).thenReturn("Not Allocated");

    Program requisitionProgram = mock(Program.class);
    when(requisition.getProgram()).thenReturn(requisitionProgram);
    when(requisition.isEmergency()).thenReturn(false);
    when(requisitionProgram.getBudgetingApplies()).thenReturn(true);
    when(requisition.getAllocatedBudget()).thenReturn(null);

    PdfPTable summary = requisitionPdfModel.getSummary();

    assertRowValues(summary.getRow(0), "Summary");
    assertRowValues(summary.getRow(1), "Allocated Budget", "Not Allocated");
    assertRowValues(summary.getRow(2), "Total Cost For Full Supply Items", "$8.00");
    assertRowValues(summary.getRow(3), "Total Cost For Non Full Supply Items", "$0.00");
    assertRowValues(summary.getRow(4), "Total Cost", "$8.00");
    assertRowValues(summary.getRow(5), " ", " ");
    assertRowValues(summary.getRow(6), " ", " ");
    assertRowValues(summary.getRow(7), "Submitted By: submit-firstName submit-lastName", "Date: " + DATE_FORMAT.format(currentDate.getTime()));
    assertRowValues(summary.getRow(8), " ", " ");
    assertRowValues(summary.getRow(9), "Authorized By: auth-firstName auth-lastName", "Date: " + DATE_FORMAT.format(authorizedDate.getTime()));
  }

  private void assertRowValues(PdfPRow row, String... cellTexts) {
    PdfPCell[] rowCells = row.getCells();
    int index = 0;
    for (String text : cellTexts) {
      assertThat(rowCells[index++].getPhrase().getContent(), is(text));
    }
  }

}


