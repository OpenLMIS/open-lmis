/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.view.pdf.requisition;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.rnr.builder.RnrLineItemBuilder;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.web.controller.RequisitionController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;
import static org.openlmis.rnr.builder.RequisitionBuilder.facility;
import static org.openlmis.rnr.builder.RnrTemplateBuilder.defaultRnrTemplate;

@RunWith(MockitoJUnitRunner.class)
public class RequisitionPdfModelTest {

  private Map<String, Object> model;
  private Rnr requisition;
  private RequisitionPdfModel requisitionPdfModel;

  @Before
  public void setUp() throws Exception {
    Facility f1 = make(a(FacilityBuilder.defaultFacility, with(FacilityBuilder.name, "F1")));
    requisition = spy(make(a(defaultRnr, with(facility, f1))));
    model = new HashMap<>();
    model.put(RequisitionController.CURRENCY, "$");
    model.put(RequisitionController.RNR, requisition);
    List<RnrColumn> rnrTemplate = make(a(defaultRnrTemplate)).getRnrColumns();
    model.put(RequisitionController.RNR_TEMPLATE, rnrTemplate);
    requisitionPdfModel = new RequisitionPdfModel(model);
  }

  @Test
  public void shouldGetHeader() throws Exception {
    PdfPTable header = requisitionPdfModel.getRequisitionHeader();
    assertRowValues(header.getRow(0), "Report and Requisition for: Yellow Fever (Central Warehouse)");
    assertRowValues(header.getRow(1), "Facility: F1", "Operated By: MOH", "Maximum Stock level: 100", "Emergency Order Point: 50.5");
    assertRowValues(header.getRow(2), "levelName: Lusaka", "parentLevelName: Zambia", "Reporting Period: 01/01/2012 - 01/02/2012");
    assertThat(header.getSpacingAfter(), is(RequisitionPdfModel.PARAGRAPH_SPACING));

  }

  @Test
  public void shouldGetSummary() throws Exception {
    PdfPTable summary = requisitionPdfModel.getSummary();

    verify(requisition).fillFullSupplyCost();
    verify(requisition).fillNonFullSupplyCost();

    assertRowValues(summary.getRow(0), "Summary");
    assertRowValues(summary.getRow(1), "Total Cost For Full Supply Items", "$8.00");
    assertRowValues(summary.getRow(2), "Total Cost For Non Full Supply Items", "$0.00");
    assertRowValues(summary.getRow(3), "Total Cost", "$8.00");
    assertRowValues(summary.getRow(4), " ", " ");
    assertRowValues(summary.getRow(5), " ", " ");
    assertRowValues(summary.getRow(6), "Submitted By: ", "Date: 19/03/2013");
    assertRowValues(summary.getRow(7), "Authorized By: ", "Date: ");

  }

  @Test
  public void shouldGetFullSupplyHeader() throws Exception {
    Paragraph fullSupplyHeader = requisitionPdfModel.getFullSupplyHeader();
    assertThat(fullSupplyHeader.getContent(), is("Full supply products"));
  }

  @Test
  public void shouldGetNonFullSupplyHeader() throws Exception {
    Paragraph nonFullSupplyHeader = requisitionPdfModel.getNonFullSupplyHeader();
    assertThat(nonFullSupplyHeader.getContent(), is("Non-Full supply products"));
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

  private void assertRowValues(PdfPRow row, String... cellTexts) {
    PdfPCell[] rowCells = row.getCells();
    int index = 0;
    for (String text : cellTexts) {
      assertThat(rowCells[index++].getPhrase().getContent(), is(text));
    }
  }

}


