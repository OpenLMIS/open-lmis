package org.openlmis.web.view;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.lang.ArrayUtils;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Money;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.web.controller.RequisitionController;
import org.openlmis.web.model.PrintRnrLineItem;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RequisitionPDFView extends AbstractPdfView {

  private static final Color headerBackground = new Color(210, 210, 210);
  private static final Color rowGreyBackground = new Color(235, 235, 235);
  private static final Font headerFont = FontFactory.getFont(FontFactory.TIMES, 20, Font.NORMAL, Color.BLACK);
  private static final Rectangle pageSize = new Rectangle(1500, 1059);
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
  private final int horizontalMargin = 10;
  private final int verticalMargin = 15;
  private final int cellPadding = 5;
  private final int widthPercentage = 100;
  private final int paragraphSpacing = 50;
  private final int tableSpacing = 25;

  @Override
  protected void buildPdfDocument(Map<String, Object> model, Document document,
                                  PdfWriter writer, HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {

    Rnr requisition = (Rnr) model.get(RequisitionController.RNR);
    List<RnrColumn> rnrColumnList = (List<RnrColumn>) model.get(RequisitionController.RNR_TEMPLATE);


    rnrColumnList = getVisibleColumns(rnrColumnList, true);
    int[] widths = getColumnWidths(rnrColumnList);


    Paragraph paragraph = getRequisitionHeader(requisition);
    document.add(paragraph);

    Paragraph fullSupplyHeader = new Paragraph("Full supply products", headerFont);
    fullSupplyHeader.setSpacingBefore(paragraphSpacing);
    document.add(fullSupplyHeader);

    PdfPTable table = prepareTable(widths);

    setTableHeader(rnrColumnList, table);

    addLineItems(requisition.getFullSupplyLineItems(), requisition.getPeriod(), rnrColumnList, table);

    document.add(table);

    if (requisition.getNonFullSupplyLineItems().size() > 0) {
      document.newPage();
      document.add(new Paragraph("Non-Full supply products", headerFont));
      rnrColumnList = getVisibleColumns(rnrColumnList, true);
      widths = getColumnWidths(rnrColumnList);

      table = prepareTable(widths);

      setTableHeader(rnrColumnList, table);

      addLineItems(requisition.getNonFullSupplyLineItems(), requisition.getPeriod(), rnrColumnList, table);

      document.add(table);
    }

    document.newPage();

    requisition.setFullSupplyItemsSubmittedCost(requisition.calculateCost(requisition.getFullSupplyLineItems()));
    requisition.setNonFullSupplyItemsSubmittedCost(requisition.calculateCost(requisition.getNonFullSupplyLineItems()));
    paragraph = getRequisitionSummary(requisition);
    document.add(paragraph);

  }

  private void setTableHeader(List<RnrColumn> rnrColumnList, PdfPTable table) {
    for (RnrColumn rnrColumn : rnrColumnList) {
      table.addCell(rnrColumn.getLabel());
    }
    table.setHeaderRows(1);
  }

  private PdfPTable prepareTable(int[] widths) throws DocumentException {
    PdfPTable table = new PdfPTable(widths.length);
    table.setWidths(widths);
    table.getDefaultCell().setBackgroundColor(headerBackground);
    table.getDefaultCell().setPadding(cellPadding);
    table.setWidthPercentage(widthPercentage);
    table.setSpacingBefore(tableSpacing);
    return table;
  }

  private void addLineItems(List<RnrLineItem> lineItems, ProcessingPeriod period, List<RnrColumn> rnrColumnList, PdfPTable table) throws NoSuchFieldException, IllegalAccessException {
    boolean odd = true;
    for (RnrLineItem lineItem : lineItems) {
      PrintRnrLineItem printRnrLineItem = new PrintRnrLineItem(lineItem);
      printRnrLineItem.calculate(period, rnrColumnList);
      odd = getStripedTableRow(table, odd);
      for (RnrColumn rnrColumn : rnrColumnList) {
        if (rnrColumn.getName().equals("lossesAndAdjustments")) {
          table.addCell(lineItem.getTotalLossesAndAdjustments().toString());
          continue;
        }
        if (rnrColumn.getName().equals("cost")) {
          table.addCell(lineItem.calculateCost().toString());
          continue;
        }
        Field field = RnrLineItem.class.getDeclaredField(rnrColumn.getName());
        field.setAccessible(true);
        Object fieldValue = field.get(lineItem);
        String cellValue = (fieldValue == null) ? "" : fieldValue.toString();
        table.addCell(cellValue);
      }
    }
  }

  private boolean getStripedTableRow(PdfPTable table, boolean odd) {
    if (odd) {
      table.getDefaultCell().setBackgroundColor(Color.WHITE);
      odd = false;
    } else {
      table.getDefaultCell().setBackgroundColor(rowGreyBackground);
      odd = true;
    }
    return odd;
  }


  @Override
  protected Document newDocument() {
    Document document = new Document(pageSize, horizontalMargin, horizontalMargin, verticalMargin, verticalMargin);
    return document;
  }


  @Override
  protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {
    HeaderFooter footer = new HeaderFooter(new Phrase("Page "), new Phrase("/" + "" + ".  Date - " + dateFormat.format(new Date())));
    footer.setAlignment(Element.ALIGN_RIGHT);
    document.setFooter(footer);
  }

  private Paragraph getRequisitionHeader(Rnr requisition) {
    Paragraph paragraph = new Paragraph();
    Chunk chunk = new Chunk("Report and Requisition for: ", FontFactory.getFont(FontFactory.TIMES, 30, Font.BOLD, Color.BLACK));
    paragraph.add(chunk);
    Facility facility = requisition.getFacility();
    chunk = new Chunk("\n\nFacility: " + facility.getName() + " \t\t Operated By: " + facility.getOperatedBy().getText()
      + " \t\t Maximum Stock level: " + facility.getFacilityType().getNominalMaxMonth() + " \t\t Emergency Order Point: " + facility.getFacilityType().getNominalEop()
      + " \n\n " + facility.getGeographicZone().getLevel().getName() + ": " + facility.getGeographicZone().getName()
      + " \t\t " + facility.getGeographicZone().getParent().getLevel().getName() + ": " + facility.getGeographicZone().getParent().getName()
      + " \t\t Reporting Period: " + requisition.getPeriod().getStartDate() + " - " + requisition.getPeriod().getEndDate(), FontFactory.getFont(FontFactory.TIMES, 20, Font.NORMAL, Color.BLACK));
    paragraph.add(chunk);
    return paragraph;
  }

  private Paragraph getRequisitionSummary(Rnr requisition) {
    Paragraph paragraph = new Paragraph();
    Chunk chunk = new Chunk("Summary: ", FontFactory.getFont(FontFactory.TIMES, 30, Font.BOLD, Color.BLACK));
    paragraph.add(chunk);
    String submittedDate = requisition.getSubmittedDate() != null ? dateFormat.format(requisition.getSubmittedDate()) : "";
    chunk = new Chunk("\n\nTotal Cost For Full Supply Items: " + requisition.getFullSupplyItemsSubmittedCost()
      + " \n\n Total Cost For Non Full Supply Items: " + requisition.getNonFullSupplyItemsSubmittedCost()
      + " \n\n Total R&R Cost: " + getTotalCost(requisition)
      + " \n\n Date of Printing: " + dateFormat.format(new Date())
      + " \n\n Date Submitted: " + submittedDate
      + " \n\n Submitted By:  \n\n Authorized By: "
      , FontFactory.getFont(FontFactory.TIMES, 20, Font.NORMAL, Color.BLACK));
    paragraph.add(chunk);
    return paragraph;
  }

  private Money getTotalCost(Rnr requisition) {
    return new Money(new BigDecimal(requisition.getFullSupplyItemsSubmittedCost().getValue().floatValue() + requisition.getNonFullSupplyItemsSubmittedCost().getValue().floatValue()));
  }

  private int[] getColumnWidths(List<RnrColumn> rnrColumnList) {

    List<Integer> widths = new ArrayList<>();
    for (RnrColumn rnrColumn : rnrColumnList) {

      if (rnrColumn.getName().equals("product")) {
        widths.add(125);
        continue;
      }
      if (rnrColumn.getName().equals("remarks")) {
        widths.add(100);
      }
      if (rnrColumn.getName().equals("quantityRequestedExplanation")) {
        widths.add(100);
      }
      widths.add(40);
    }
    return ArrayUtils.toPrimitive(widths.toArray(new Integer[widths.size()]));
  }


  private List<RnrColumn> getVisibleColumns(List<RnrColumn> rnrColumnList, boolean fullSupply) {
    List<RnrColumn> visibleRnrColumns = new ArrayList<>();
    if (fullSupply) {
      for (RnrColumn rnrColumn : rnrColumnList) {
        if (rnrColumn.getName().equals("remarks") || rnrColumn.getName().equals("quantityRequestedExplanation"))
          continue;
        if (rnrColumn.isVisible()) {
          visibleRnrColumns.add(rnrColumn);
        }
      }
    } else {
      for (RnrColumn rnrColumn : rnrColumnList) {
        if (rnrColumn.getName().equals("product") || rnrColumn.getName().equals("productCode") ||
          rnrColumn.getName().equals("dispensingUnit") || rnrColumn.getName().equals("quantityRequested") ||
          rnrColumn.getName().equals("packsToShip") || rnrColumn.getName().equals("price") ||
          rnrColumn.getName().equals("cost")) {
          if (rnrColumn.isVisible()) {
            visibleRnrColumns.add(rnrColumn);
          }
        }
      }
    }
    return visibleRnrColumns;
  }
}
