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
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.web.controller.RequisitionController;
import org.openlmis.web.model.PrintRnrLineItem;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.itextpdf.text.Element.ALIGN_LEFT;
import static com.itextpdf.text.Element.ALIGN_RIGHT;
import static org.openlmis.web.view.pdf.requisition.RequisitionCellFactory.categoryRow;
import static org.openlmis.web.view.pdf.requisition.RequisitionCellFactory.getCells;
import static org.openlmis.web.view.pdf.requisition.RequisitionDocument.*;

@Data
@NoArgsConstructor
public class RequisitionPdfModel {
  private Map<String, Object> model;
  public static final int PARAGRAPH_SPACING = 30;
  public static final BaseColor ROW_GREY_BACKGROUND = new BaseColor(235, 235, 235);
  public static final Font H1_FONT = FontFactory.getFont(FontFactory.TIMES, 30, Font.BOLD, BaseColor.BLACK);
  public static final Font H3_FONT = FontFactory.getFont(FontFactory.TIMES, 17f, Font.NORMAL, BaseColor.BLACK);
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
  public static final int TABLE_SPACING = 25;

  private List<RnrColumn> rnrColumnList;
  private Rnr requisition;
  private String currency;

  public RequisitionPdfModel(Map<String, Object> model) {
    this.model = model;
    this.rnrColumnList = (List<RnrColumn>) model.get(RequisitionController.RNR_TEMPLATE);
    this.requisition = (Rnr) model.get(RequisitionController.RNR);
    this.currency = (String) model.get(RequisitionController.CURRENCY);
  }

  public Paragraph getFullSupplyHeader() {
    Paragraph fullSupplyHeader = new Paragraph("Full supply products", H2_FONT);
    fullSupplyHeader.setSpacingBefore(PARAGRAPH_SPACING);
    return fullSupplyHeader;
  }

  public Paragraph getNonFullSupplyHeader() {
    return new Paragraph("Non-Full supply products", H2_FONT);
  }

  public PdfPTable getFullSupplyTable() throws DocumentException, NoSuchFieldException, IllegalAccessException {
    return getTableFor(requisition.getFullSupplyLineItems(), true);
  }

  private PdfPTable getTableFor(List<RnrLineItem> lineItems, boolean fullSupply) throws DocumentException, NoSuchFieldException, IllegalAccessException {
    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumnList);
    List<RnrColumn> visibleColumns = template.getVisibleColumns(fullSupply);

    PdfPTable table = prepareTable(visibleColumns);

    boolean odd = true;

    RnrLineItem previousLineItem = null;
    for (RnrLineItem lineItem : lineItems) {
      if (previousLineItem == null || !lineItem.getProductCategory().equals(previousLineItem.getProductCategory())) {
        table.addCell(categoryRow(visibleColumns, lineItem));
        previousLineItem = lineItem;
      }

      PrintRnrLineItem printRnrLineItem = new PrintRnrLineItem(lineItem);
      printRnrLineItem.calculate(requisition.getPeriod(), rnrColumnList);

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

  private PdfPTable prepareTable(List<RnrColumn> visibleColumns) throws DocumentException {
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
    PdfPTable table = prepareTable();
    addHeading(table);

    Facility facility = requisition.getFacility();
    addFirstLine(facility, table);
    addSecondLine(facility, table);
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

  private PdfPTable prepareTable() throws DocumentException {
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

    Chunk chunk = new Chunk("Summary ", H2_FONT);
    PdfPCell summaryHeaderCell = new PdfPCell(new Phrase(chunk));
    summaryHeaderCell.setColspan(2);
    summaryHeaderCell.setPadding(10);
    summaryHeaderCell.setBorder(0);
    summaryTable.addCell(summaryHeaderCell);

    summaryTable.addCell(cell("Total Cost For Full Supply Items"));
    summaryTable.addCell(cell(currency + requisition.getFullSupplyItemsSubmittedCost(), ALIGN_RIGHT));
    summaryTable.addCell(cell("Total Cost For Non Full Supply Items"));
    summaryTable.addCell(cell(currency + requisition.getNonFullSupplyItemsSubmittedCost(), ALIGN_RIGHT));
    summaryTable.addCell(cell("Total Cost"));
    summaryTable.addCell(cell(currency + this.getTotalCost(requisition).toString(), ALIGN_RIGHT));
    summaryTable.addCell(cell(" "));
    summaryTable.addCell(cell(" "));
    summaryTable.addCell(cell(" "));
    summaryTable.addCell(cell(" "));

    String submittedDate = requisition.getSubmittedDate() != null ? DATE_FORMAT.format(requisition.getSubmittedDate()) : "";

    summaryTable.addCell(cell("Submitted By: "));
    summaryTable.addCell(cell("Date: " + submittedDate));
    summaryTable.addCell(cell("Authorized By: "));
    summaryTable.addCell(cell("Date: "));

    return summaryTable;
  }

  private PdfPCell cell(Object value, int... alignment) {
    Chunk chunk = new Chunk(value.toString());
    chunk.setFont(H3_FONT);
    Phrase phrase = new Phrase(chunk);
    PdfPCell cell = new PdfPCell(phrase);

    cell.setHorizontalAlignment((alignment.length > 0) ? alignment[0] : ALIGN_LEFT);
    cell.setBorder(0);
    cell.setPadding(15);
    return cell;
  }

  public Money getTotalCost(Rnr requisition) {
    return new Money(new BigDecimal(requisition.getFullSupplyItemsSubmittedCost().getValue().floatValue() + requisition.getNonFullSupplyItemsSubmittedCost().getValue().floatValue()));
  }
}
