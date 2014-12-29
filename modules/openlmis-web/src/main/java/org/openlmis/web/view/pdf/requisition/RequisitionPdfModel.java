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

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.Money;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.MessageService;
import org.openlmis.rnr.domain.*;
import org.openlmis.web.model.PrintRnrLineItem;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.find;
import static org.openlmis.rnr.domain.RnrStatus.AUTHORIZED;
import static org.openlmis.rnr.domain.RnrStatus.SUBMITTED;
import static org.openlmis.web.controller.RequisitionController.*;
import static org.openlmis.web.view.pdf.requisition.RequisitionCellFactory.*;

/**
 * This entity is used to encapsulate rnr attributes, report formatting attributes to be used in requisition report.
 */

@Data
@NoArgsConstructor
public class RequisitionPdfModel {
  public static final String LABEL_CURRENCY_SYMBOL = "label.currency.symbol";
  public static final float PARAGRAPH_SPACING = 30.0f;
  public static final BaseColor ROW_GREY_BACKGROUND = new BaseColor(235, 235, 235);
  public static final Font H1_FONT = FontFactory.getFont(FontFactory.TIMES, 30, Font.BOLD, BaseColor.BLACK);
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
  public static final int TABLE_SPACING = 25;
  private List<RequisitionStatusChange> statusChanges;
  private List<? extends Column> rnrColumnList;
  private List<? extends Column> regimenColumnList;
  private Integer numberOfMonths;
  private Rnr requisition;
  private List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes;
  private MessageService messageService;

  private ConfigurationSettingService configService;

  public RequisitionPdfModel(Map<String, Object> model, MessageService messageService, ConfigurationSettingService configService) {
    this.statusChanges = (List<RequisitionStatusChange>) model.get(STATUS_CHANGES);
    this.rnrColumnList = (List<RnrColumn>) model.get(RNR_TEMPLATE);
    this.regimenColumnList = (List<RegimenColumn>) model.get(REGIMEN_TEMPLATE);
    this.requisition = (Rnr) model.get(RNR);
    this.lossesAndAdjustmentsTypes = (List<LossesAndAdjustmentsType>) model.get(LOSSES_AND_ADJUSTMENT_TYPES);
    this.numberOfMonths = (Integer) model.get(NUMBER_OF_MONTHS);
    this.messageService = messageService;
    this.configService = configService;
  }

  public Paragraph getFullSupplyHeader() {
    return new Paragraph(messageService.message("label.full.supply.products"), H2_FONT);
  }

  public Paragraph getNonFullSupplyHeader() {
    return new Paragraph(messageService.message("label.non.full.supply.products"), H2_FONT);
  }

  public PdfPTable getFullSupplyTable() throws DocumentException, NoSuchFieldException, IllegalAccessException, IOException {
    return getTableFor(requisition.getFullSupplyLineItems(), true, rnrColumnList);
  }

  public PdfPTable getRegimenTable() throws DocumentException, NoSuchFieldException, IllegalAccessException, IOException {
    List<RegimenLineItem> regimenLineItems = requisition.getRegimenLineItems();
    if (regimenLineItems.size() == 0) return null;

    return getTableFor(regimenLineItems, null, regimenColumnList);
  }

  private PdfPTable getTableFor(List<? extends LineItem> lineItems,
                                Boolean fullSupply,
                                List<? extends Column> columnList) throws DocumentException, NoSuchFieldException, IllegalAccessException, IOException {
    Template template = Template.getInstance(columnList);
    List<? extends Column> visibleColumns = template.getPrintableColumns(fullSupply);

    PdfPTable table = prepareTable(visibleColumns);

    boolean odd = true;

    LineItem previousLineItem = null;
    for (LineItem lineItem : lineItems) {
      if (previousLineItem == null || !lineItem.compareCategory(previousLineItem)) {
        table.addCell(categoryRow(visibleColumns.size(), lineItem));
        previousLineItem = lineItem;
      }

      if (lineItem.isRnrLineItem()) {
        PrintRnrLineItem printRnrLineItem = new PrintRnrLineItem((RnrLineItem) lineItem);
        printRnrLineItem.calculate(rnrColumnList, lossesAndAdjustmentsTypes, numberOfMonths, requisition.getStatus());
      }

      String currencySymbol = messageService.message(LABEL_CURRENCY_SYMBOL);

      if(!configService.getBoolValue("RNR_PRINT_REPEAT_CURRENCY_SYMBOL")){
        currencySymbol = "";
      }

      List<PdfPCell> cells = getCells(visibleColumns, lineItem, currencySymbol);
      odd = !odd;

      for (PdfPCell cell : cells) {
        cell.setBackgroundColor(odd ? BaseColor.WHITE : ROW_GREY_BACKGROUND);
        table.addCell(cell);
      }
    }
    return table;
  }

  private void setTableHeader(PdfPTable table, List<? extends Column> visibleColumns) {
    for (Column column : visibleColumns) {
      table.addCell(column.getLabel());
    }
  }

  private PdfPTable prepareTable(List<? extends Column> visibleColumns) throws DocumentException {
    java.util.List<Integer> widths = new ArrayList<>();
    for (Column column : visibleColumns) {
      widths.add(column.getColumnWidth());
    }
    PdfPTable table = new PdfPTable(widths.size());

    table.setWidths(ArrayUtils.toPrimitive(widths.toArray(new Integer[widths.size()])));
    table.getDefaultCell().setBackgroundColor(HEADER_BACKGROUND);
    table.getDefaultCell().setPadding(CELL_PADDING);
    table.setWidthPercentage(WIDTH_PERCENTAGE);
    table.setSpacingBefore(TABLE_SPACING);
    table.setHeaderRows(2);
    table.setFooterRows(1);
    setTableHeader(table, visibleColumns);
    setBlankFooter(table, visibleColumns.size());
    return table;
  }

  private void setBlankFooter(PdfPTable table, Integer visibleColumnsSize) {
    PdfPCell cell = new PdfPCell(new Phrase(" "));
    cell.setBorder(0);
    cell.setColspan(visibleColumnsSize);
    cell.setBackgroundColor(BaseColor.WHITE);
    table.addCell(cell);
  }

  public PdfPTable getNonFullSupplyTable() throws DocumentException, NoSuchFieldException, IllegalAccessException, IOException {
    List<RnrLineItem> nonFullSupplyLineItems = requisition.getNonFullSupplyLineItems();
    Collections.sort(nonFullSupplyLineItems, new LineItemComparator());
    if (nonFullSupplyLineItems.size() == 0) return null;

    return getTableFor(nonFullSupplyLineItems, false, rnrColumnList);
  }

  public PdfPTable getRequisitionHeader() throws DocumentException {
    PdfPTable table = prepareRequisitionHeaderTable();
    addHeading(table);

    Facility facility = requisition.getFacility();
    addFirstLine(facility, table);
    addSecondLine(facility, table, requisition.isEmergency());
    table.setSpacingAfter(PARAGRAPH_SPACING);
    return table;
  }

  private void addHeading(PdfPTable table) throws DocumentException {
    Chunk chunk = new Chunk(String.format(messageService.message("label.requisition") + ": %s (%s)",
      this.requisition.getProgram().getName(),
      this.requisition.getFacility().getFacilityType().getName()), H1_FONT);

    PdfPCell cell = new PdfPCell(new Phrase(chunk));
    cell.setColspan(5);
    cell.setPadding(10);
    cell.setBorder(0);
    table.addCell(cell);
  }

  private void addFirstLine(Facility facility, PdfPTable table) {
    String text = String.format(messageService.message("label.facility") + ": %s", facility.getName());
    insertCell(table, text, 1);
    text = String.format(messageService.message("create.facility.operatedBy") + ": %s",
      facility.getOperatedBy().getText());
    insertCell(table, text, 1);
    text = String.format(messageService.message("label.facility.maximumStock") + ": %s",
      facility.getFacilityType().getNominalMaxMonth());
    insertCell(table, text, 1);
    text = String.format(messageService.message("label.emergency.order.point") + ": %s",
      facility.getFacilityType().getNominalEop());
    insertCell(table, text, 1);
    insertCell(table, "", 1);
  }

  private void insertCell(PdfPTable table, String text, int colSpan) {
    Chunk chunk;
    chunk = new Chunk(text);
    PdfPCell cell = table.getDefaultCell();
    cell.setPhrase(new Phrase(chunk));
    cell.setColspan(colSpan);
    table.addCell(cell);
  }

  private void addSecondLine(Facility facility, PdfPTable table, Boolean emergency) {
    GeographicZone geographicZone = facility.getGeographicZone();
    GeographicZone parent = geographicZone.getParent();
    String text = String.format(messageService.message("header.facility.code") + ": %s", facility.getCode());
    insertCell(table,text, 1);

    StringBuilder builder = new StringBuilder();
    builder.append(geographicZone.getLevel().getName()).append(": ").append(geographicZone.getName());
    insertCell(table, builder.toString(), 1);
    builder = new StringBuilder();
    builder.append(parent.getLevel().getName()).append(": ").append(parent.getName());
    insertCell(table, builder.toString(), 1);
    builder = new StringBuilder();
    builder.append(messageService.message("label.facility.reportingPeriod")).append(": ").append(DATE_FORMAT.format(
      requisition.getPeriod().getStartDate())).append(" - ").append(DATE_FORMAT.format(requisition.getPeriod().getEndDate()));

    insertCell(table, builder.toString(), 1);

    String label = emergency ? "requisition.type.emergency" : "requisition.type.regular";
    builder = new StringBuilder();
    builder.append(messageService.message("label.requisition.type")).append(": ").append(messageService.message(label));
    insertCell(table, builder.toString(), 1);
  }

  private PdfPTable prepareRequisitionHeaderTable() throws DocumentException {
    int[] columnWidths = {160, 160, 160, 160, 160};
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

    DecimalFormat formatter = new DecimalFormat("#,##0.00");

    PdfPTable summaryTable = new PdfPTable(2);
    summaryTable.setWidths(new int[]{30, 20});
    summaryTable.setSpacingBefore(TABLE_SPACING);
    summaryTable.setWidthPercentage(40);
    summaryTable.setHorizontalAlignment(0);

    PdfPCell summaryHeaderCell = headingCell(messageService.message("label.summary"));
    summaryHeaderCell.setColspan(2);
    summaryHeaderCell.setPadding(10);
    summaryHeaderCell.setBorder(0);
    summaryTable.addCell(summaryHeaderCell);

    boolean showBudget = !requisition.isEmergency() && requisition.getProgram().getBudgetingApplies();
    if (showBudget) {
      summaryTable.addCell(summaryCell(textCell(messageService.message("label.allocated.budget"))));
      PdfPCell allocatedBudgetCell = requisition.getAllocatedBudget() != null ? numberCell(messageService.message(LABEL_CURRENCY_SYMBOL) + formatter.format( new Money(requisition.getAllocatedBudget()).toDecimal())) :
        numberCell(messageService.message("msg.budget.not.allocated"));
      summaryTable.addCell(summaryCell(allocatedBudgetCell));
    }
    summaryTable.addCell(summaryCell(textCell(messageService.message("label.total.cost.full.supply.items"))));
    summaryTable.addCell(summaryCell(numberCell(messageService.message(LABEL_CURRENCY_SYMBOL) + formatter.format(requisition.getFullSupplyItemsSubmittedCost().toDecimal()))) );
    summaryTable.addCell(summaryCell(textCell(messageService.message("label.total.cost.non.full.supply.items"))));
    summaryTable.addCell(summaryCell(numberCell(messageService.message(LABEL_CURRENCY_SYMBOL) + formatter.format(requisition.getNonFullSupplyItemsSubmittedCost().toDecimal()))));
    summaryTable.addCell(summaryCell(textCell(messageService.message("label.total.cost"))));
    summaryTable.addCell(summaryCell(numberCell(messageService.message(LABEL_CURRENCY_SYMBOL) + formatter.format(this.getTotalCost(
      requisition).toDecimal()).toString())));
    if (showBudget && requisition.getAllocatedBudget() != null && (requisition.getAllocatedBudget().compareTo(this.getTotalCost(requisition).getValue()) == -1)) {
      summaryTable.addCell(summaryCell(textCell(messageService.message("msg.cost.exceeds.budget"))));
      summaryTable.addCell(summaryCell(textCell(" ")));
    }

    summaryTable.addCell(summaryCell(textCell(" ")));
    summaryTable.addCell(summaryCell(textCell(" ")));
    summaryTable.addCell(summaryCell(textCell(" ")));
    summaryTable.addCell(summaryCell(textCell(" ")));

    fillAuditFields(summaryTable);
    return summaryTable;
  }

  private void fillAuditFields(PdfPTable summaryTable) {
    RequisitionStatusChange submittedStatusChange = getStatusChangeFor(SUBMITTED);
    RequisitionStatusChange authorizedStatusChange = getStatusChangeFor(AUTHORIZED);

    String submittedDate = submittedStatusChange != null ? DATE_FORMAT.format(submittedStatusChange.getCreatedDate()) : "";
    String submittedBy = submittedStatusChange != null ? submittedStatusChange.getCreatedBy().getFirstName() + " " + submittedStatusChange.getCreatedBy().getLastName() : "";

    String authorizedDate = authorizedStatusChange != null ? DATE_FORMAT.format(authorizedStatusChange.getCreatedDate()) : "";
    String authorizedBy = authorizedStatusChange != null ? authorizedStatusChange.getCreatedBy().getFirstName() + " " + authorizedStatusChange.getCreatedBy().getLastName() : "";

    summaryTable.addCell(summaryCell(textCell(messageService.message("label.submitted.by") + ": " + submittedBy)));
    summaryTable.addCell(summaryCell(textCell(messageService.message("label.date") + ": " + submittedDate)));
    summaryTable.addCell(summaryCell(textCell(" ")));
    summaryTable.addCell(summaryCell(textCell(" ")));
    summaryTable.addCell(summaryCell(textCell(messageService.message("label.authorized.by") + ": " + authorizedBy)));
    summaryTable.addCell(summaryCell(textCell(messageService.message("label.date") + ": " + authorizedDate)));
  }

  private RequisitionStatusChange getStatusChangeFor(final RnrStatus status) {
    return (RequisitionStatusChange) find(statusChanges, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        return ((RequisitionStatusChange) o).getStatus().equals(status);
      }
    });
  }

  private PdfPCell summaryCell(PdfPCell cell) {
    cell.setPadding(15);
    cell.setBorder(0);
    return cell;
  }

  public Money getTotalCost(Rnr requisition) {
    return new Money(requisition.getFullSupplyItemsSubmittedCost().getValue().add(requisition.getNonFullSupplyItemsSubmittedCost().getValue()));
  }

  public Paragraph getRegimenHeader() {
    return new Paragraph(messageService.message("label.regimens"), H2_FONT);
  }
}
