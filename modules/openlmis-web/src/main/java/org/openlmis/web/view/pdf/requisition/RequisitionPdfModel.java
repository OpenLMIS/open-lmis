/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.view.pdf.requisition;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.ArrayUtils;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.Money;
import org.openlmis.rnr.domain.*;
import org.openlmis.web.controller.RequisitionController;
import org.openlmis.web.model.PrintRnrLineItem;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.openlmis.web.view.pdf.requisition.RequisitionCellFactory.*;

@Data
@NoArgsConstructor
public class RequisitionPdfModel {
  private Map<String, Object> model;
  public static final float PARAGRAPH_SPACING = 30.0f;
  public static final BaseColor ROW_GREY_BACKGROUND = new BaseColor(235, 235, 235);
  public static final Font H1_FONT = FontFactory.getFont(FontFactory.TIMES, 30, Font.BOLD, BaseColor.BLACK);
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
  public static final int TABLE_SPACING = 25;

  private List<RnrColumn> rnrColumnList;
  private Rnr requisition;
  private String currency;
  private List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes;

  public RequisitionPdfModel(Map<String, Object> model) {
    this.model = model;
    this.rnrColumnList = (List<RnrColumn>) model.get(RequisitionController.RNR_TEMPLATE);
    this.requisition = (Rnr) model.get(RequisitionController.RNR);
    this.currency = (String) model.get(RequisitionController.CURRENCY);
    this.lossesAndAdjustmentsTypes = (List<LossesAndAdjustmentsType>) model.get(RequisitionController.LOSSES_AND_ADJUSTMENT_TYPES);
  }

  public Paragraph getFullSupplyHeader() {
    return new Paragraph("Full supply products", H2_FONT);
  }

  public Paragraph getNonFullSupplyHeader() {
    return new Paragraph("Non-Full supply products", H2_FONT);
  }

  public PdfPTable getFullSupplyTable() throws DocumentException, NoSuchFieldException, IllegalAccessException {
    return getTableFor(requisition.getFullSupplyLineItems(), true);
  }

  private PdfPTable getTableFor(List<RnrLineItem> lineItems, boolean fullSupply) throws DocumentException, NoSuchFieldException, IllegalAccessException {
    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumnList);
    List<RnrColumn> visibleColumns = template.getPrintableColumns(fullSupply);

    PdfPTable table = prepareRnrLineItemsTable(visibleColumns);

    boolean odd = true;

    RnrLineItem previousLineItem = null;
    for (RnrLineItem lineItem : lineItems) {
      if (previousLineItem == null || !lineItem.getProductCategory().equals(previousLineItem.getProductCategory())) {
        table.addCell(categoryRow(visibleColumns, lineItem));
        previousLineItem = lineItem;
      }

      PrintRnrLineItem printRnrLineItem = new PrintRnrLineItem(lineItem);
      printRnrLineItem.calculate(requisition.getPeriod(), rnrColumnList, lossesAndAdjustmentsTypes);

      List<PdfPCell> cells = getCells(visibleColumns, lineItem, currency);
      odd = !odd;

      for (PdfPCell cell : cells) {
        cell.setBackgroundColor(odd ? BaseColor.WHITE : ROW_GREY_BACKGROUND);
        table.addCell(cell);
      }
    }
    return table;
  }

  private void setTableHeader(PdfPTable table, List<RnrColumn> visibleColumns) {
    for (RnrColumn rnrColumn : visibleColumns) {
      table.addCell(rnrColumn.getLabel());
    }
  }

  private PdfPTable prepareRnrLineItemsTable(List<RnrColumn> visibleColumns) throws DocumentException {
    int[] widths = getColumnWidths(visibleColumns);
    PdfPTable table = new PdfPTable(widths.length);

    table.setWidths(widths);
    table.getDefaultCell().setBackgroundColor(HEADER_BACKGROUND);
    table.getDefaultCell().setPadding(CELL_PADDING);
    table.setWidthPercentage(WIDTH_PERCENTAGE);
    table.setSpacingBefore(TABLE_SPACING);
    table.setHeaderRows(2);
    table.setFooterRows(1);
    setTableHeader(table, visibleColumns);
    setBlankFooter(table, visibleColumns);
    return table;
  }

  private void setBlankFooter(PdfPTable table, List<RnrColumn> visibleColumns) {
    PdfPCell cell = new PdfPCell(new Phrase(" "));
    cell.setBorder(0);
    cell.setColspan(visibleColumns.size());
    cell.setBackgroundColor(BaseColor.WHITE);
    table.addCell(cell);
  }

  private int[] getColumnWidths(List<RnrColumn> rnrColumns) {

    java.util.List<Integer> widths = new ArrayList<>();
    for (RnrColumn rnrColumn : rnrColumns) {

      if (rnrColumn.getName().equals("product")) {
        widths.add(125);
        continue;
      }
      if (rnrColumn.getName().equals("remarks")) {
        widths.add(100);
      }
      if (rnrColumn.getName().equals("reasonForRequestedQuantity")) {
        widths.add(100);
      }
      widths.add(40);
    }
    return ArrayUtils.toPrimitive(widths.toArray(new Integer[widths.size()]));
  }

  public PdfPTable getNonFullSupplyTable() throws DocumentException, NoSuchFieldException, IllegalAccessException {
    List<RnrLineItem> nonFullSupplyLineItems = requisition.getNonFullSupplyLineItems();
    if (nonFullSupplyLineItems.size() == 0) return null;
    return getTableFor(nonFullSupplyLineItems, false);
  }

  public PdfPTable getRequisitionHeader() throws DocumentException {
    PdfPTable table = prepareRequisitionHeaderTable();
    addHeading(table);

    Facility facility = requisition.getFacility();
    addFirstLine(facility, table);
    addSecondLine(facility, table);
    table.setSpacingAfter(PARAGRAPH_SPACING);
    return table;
  }


  private void addHeading(PdfPTable table) throws DocumentException {
    Chunk chunk = new Chunk(String.format("Report and Requisition for: %s (%s)",
        this.requisition.getProgram().getName(),
        this.requisition.getFacility().getFacilityType().getName()), H1_FONT);

    PdfPCell cell = new PdfPCell(new Phrase(chunk));
    cell.setColspan(4);
    cell.setPadding(10);
    cell.setBorder(0);
    table.addCell(cell);
  }

  private void addFirstLine(Facility facility, PdfPTable table) {
    String text = String.format("Facility: %s", facility.getName());
    insertCell(table, text, 1);
    text = String.format("Operated By: %s", facility.getOperatedBy().getText());
    insertCell(table, text, 1);
    text = String.format("Maximum Stock level: %s", facility.getFacilityType().getNominalMaxMonth());
    insertCell(table, text, 1);
    text = String.format("Emergency Order Point: %s", facility.getFacilityType().getNominalEop());
    insertCell(table, text, 1);
  }

  private void insertCell(PdfPTable table, String text, int colspan) {
    Chunk chunk;
    chunk = new Chunk(text);
    PdfPCell cell = table.getDefaultCell();
    cell.setPhrase(new Phrase(chunk));
    cell.setColspan(colspan);
    table.addCell(cell);
  }

  private void addSecondLine(Facility facility, PdfPTable table) {
    GeographicZone geographicZone = facility.getGeographicZone();
    GeographicZone parent = geographicZone.getParent();
    StringBuilder builder = new StringBuilder();
    builder.append(geographicZone.getLevel().getName()).append(": ").append(geographicZone.getName());
    insertCell(table, builder.toString(), 1);
    builder = new StringBuilder();
    builder.append(parent.getLevel().getName()).append(": ").append(parent.getName());
    insertCell(table, builder.toString(), 1);
    builder = new StringBuilder();
    builder.append("Reporting Period: ").append(DATE_FORMAT.format(requisition.getPeriod().getStartDate())).append(" - ").
        append(DATE_FORMAT.format(requisition.getPeriod().getEndDate()));
    insertCell(table, builder.toString(), 2);
  }

  private PdfPTable prepareRequisitionHeaderTable() throws DocumentException {
    int[] columnWidths = {200, 200, 200, 200};
    PdfPTable table = new PdfPTable(columnWidths.length);
    table.setWidths(columnWidths);
    table.getDefaultCell().setBackgroundColor(HEADER_BACKGROUND);
    table.getDefaultCell().setPadding(10);
    table.getDefaultCell().setBorder(0);
    table.setWidthPercentage(WIDTH_PERCENTAGE);
    table.setSpacingBefore(TABLE_SPACING);
    table.setHeaderRows(1);
    return table;
  }

  public PdfPTable getSummary() throws DocumentException {
    this.requisition.fillFullSupplyCost();
    this.requisition.fillNonFullSupplyCost();

    PdfPTable summaryTable = new PdfPTable(2);
    summaryTable.setWidths(new int[]{30, 20});
    summaryTable.setSpacingBefore(TABLE_SPACING);
    summaryTable.setWidthPercentage(40);
    summaryTable.setHorizontalAlignment(0);

    PdfPCell summaryHeaderCell = headingCell("Summary");
    summaryHeaderCell.setColspan(2);
    summaryHeaderCell.setPadding(10);
    summaryHeaderCell.setBorder(0);
    summaryTable.addCell(summaryHeaderCell);

    summaryTable.addCell(summaryCell(textCell("Total Cost For Full Supply Items")));
    summaryTable.addCell(summaryCell(numberCell(currency + requisition.getFullSupplyItemsSubmittedCost())));
    summaryTable.addCell(summaryCell(textCell("Total Cost For Non Full Supply Items")));
    summaryTable.addCell(summaryCell(numberCell(currency + requisition.getNonFullSupplyItemsSubmittedCost())));
    summaryTable.addCell(summaryCell(textCell("Total Cost")));
    summaryTable.addCell(summaryCell(numberCell(currency + this.getTotalCost(requisition).toString())));
    summaryTable.addCell(summaryCell(textCell(" ")));
    summaryTable.addCell(summaryCell(textCell(" ")));
    summaryTable.addCell(summaryCell(textCell(" ")));
    summaryTable.addCell(summaryCell(textCell(" ")));

    String submittedDate = requisition.getSubmittedDate() != null ? DATE_FORMAT.format(requisition.getSubmittedDate()) : "";

    summaryTable.addCell(summaryCell(textCell("Submitted By: ")));
    summaryTable.addCell(summaryCell(textCell("Date: " + submittedDate)));
    summaryTable.addCell(summaryCell(textCell("Authorized By: ")));
    summaryTable.addCell(summaryCell(textCell("Date: ")));
    return summaryTable;
  }

  private PdfPCell summaryCell(PdfPCell cell) {
    cell.setPadding(15);
    cell.setBorder(0);
    return cell;
  }

  public Money getTotalCost(Rnr requisition) {
    return new Money(new BigDecimal(requisition.getFullSupplyItemsSubmittedCost().getValue().floatValue() + requisition.getNonFullSupplyItemsSubmittedCost().getValue().floatValue()));
  }
}
