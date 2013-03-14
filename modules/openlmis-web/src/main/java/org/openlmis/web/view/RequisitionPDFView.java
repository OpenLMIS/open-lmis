package org.openlmis.web.view;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.lang.ArrayUtils;
import org.openlmis.core.domain.Money;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.web.controller.RequisitionController;
import org.openlmis.web.model.PrintRnrLineItem;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequisitionPDFView extends AbstractView {

  private static final BaseColor headerBackground = new BaseColor(210, 210, 210);
  private static final BaseColor rowGreyBackground = new BaseColor(235, 235, 235);
  public static final Font H1_FONT = FontFactory.getFont(FontFactory.TIMES, 30, Font.BOLD, BaseColor.BLACK);
  public static final Font H2_FONT = FontFactory.getFont(FontFactory.TIMES, 20f, Font.BOLD, BaseColor.BLACK);
  public static final Font TEXT_FONT = FontFactory.getFont(FontFactory.TIMES, 20, Font.NORMAL, BaseColor.BLACK);
  private static final Rectangle PAGE_SIZE = new Rectangle(1500, 1059);
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
  private static final int LEFT_MARGIN = 20;
  private static final int RIGHT_MARGIN = 10;
  private static final int TOP_MARGIN = 10;
  private static final int BOTTOM_MARGIN = 30;
  private static final int CELL_PADDING = 10;
  private static final int WIDTH_PERCENTAGE = 100;
  private static final int PARAGRAPH_SPACING = 50;
  private static final int TABLE_SPACING = 25;

  public RequisitionPDFView() {
    setContentType("application/pdf");
  }

  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    // IE workaround: write into byte array first.
    try (ByteArrayOutputStream stream = createTemporaryOutputStream()) {

      Document document = new Document(PAGE_SIZE, LEFT_MARGIN, RIGHT_MARGIN, TOP_MARGIN, BOTTOM_MARGIN);

      PdfWriter writer = PdfWriter.getInstance(document, stream);

      prepareWriter(writer, model);
      document.open();
      buildPdfDocument(model, document, writer);
      document.close();

      writeToResponse(response, stream);
    }
  }

  protected void prepareWriter(PdfWriter writer, Map<String, Object> model)
      throws DocumentException {
    Rnr requisition = (Rnr) model.get(RequisitionController.RNR);

    writer.setPageEvent(new RequisitionPdfHeaderFooter(requisition));
    writer.setViewerPreferences(getViewerPreferences());
  }

  protected int getViewerPreferences() {
    return PdfWriter.ALLOW_PRINTING | PdfWriter.PageLayoutSinglePage;
  }

  protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer) throws Exception {

    Rnr requisition = (Rnr) model.get(RequisitionController.RNR);

    List<RnrColumn> rnrColumnList = (List<RnrColumn>) model.get(RequisitionController.RNR_TEMPLATE);

    rnrColumnList = getVisibleColumns(rnrColumnList, true);
    int[] widths = getColumnWidths(rnrColumnList);

    Paragraph fullSupplyHeader = new Paragraph("Full supply products", H2_FONT);
    fullSupplyHeader.setSpacingBefore(PARAGRAPH_SPACING);
    document.add(fullSupplyHeader);

    addLineItems(document, requisition.getFullSupplyLineItems(), requisition.getPeriod(), rnrColumnList, widths);

    if (requisition.getNonFullSupplyLineItems().size() > 0) {
      document.newPage();
      document.add(new Paragraph("Non-Full supply products", H2_FONT));
      rnrColumnList = getVisibleColumns(rnrColumnList, true);
      widths = getColumnWidths(rnrColumnList);
      addLineItems(document, requisition.getNonFullSupplyLineItems(), requisition.getPeriod(), rnrColumnList, widths);
    }

    document.newPage();

    requisition.setFullSupplyItemsSubmittedCost(requisition.calculateCost(requisition.getFullSupplyLineItems()));
    requisition.setNonFullSupplyItemsSubmittedCost(requisition.calculateCost(requisition.getNonFullSupplyLineItems()));
    addRequisitionSummary(requisition, document);
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
    table.getDefaultCell().setPadding(CELL_PADDING);
    table.setWidthPercentage(WIDTH_PERCENTAGE);
    table.setSpacingBefore(TABLE_SPACING);
    return table;
  }

  private void addLineItems(Document document, List<RnrLineItem> lineItems, ProcessingPeriod period, List<RnrColumn> rnrColumnList, int[] widths) throws NoSuchFieldException, IllegalAccessException, DocumentException {
    PdfPTable table = prepareTable(widths);
    setTableHeader(rnrColumnList, table);
    boolean odd = true;
    RnrLineItem previousLineItem = null;
    for (RnrLineItem lineItem : lineItems) {
      if (previousLineItem == null || !lineItem.getProductCategory().equals(previousLineItem.getProductCategory())) {
        Chunk chunk = new Chunk(lineItem.getProductCategory(), FontFactory.getFont(FontFactory.HELVETICA_BOLD));
        PdfPCell cell = new PdfPCell(new Phrase(chunk));
        cell.setColspan(rnrColumnList.size());
        cell.setBackgroundColor(headerBackground);
        cell.setPadding(CELL_PADDING);
        table.addCell(cell);
        previousLineItem = lineItem;
      }
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
    document.add(table);
  }

  private boolean getStripedTableRow(PdfPTable table, boolean odd) {
    if (odd) {
      table.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
      odd = false;
    } else {
      table.getDefaultCell().setBackgroundColor(rowGreyBackground);
      odd = true;
    }
    return odd;
  }


  private void addRequisitionSummary(Rnr requisition, Document document) throws DocumentException {
    Paragraph paragraph = new Paragraph();
    Chunk chunk = new Chunk("Summary: ", H2_FONT);
    paragraph.add(chunk);
    document.add(paragraph);

    PdfPTable summaryTable = new PdfPTable(2);
    summaryTable.addCell("Total Cost For Full Supply Items");
    summaryTable.addCell(requisition.getFullSupplyItemsSubmittedCost().toString());
    summaryTable.addCell("Total Cost For Non Full Supply Items");
    summaryTable.addCell(requisition.getNonFullSupplyItemsSubmittedCost().toString());
    summaryTable.setSpacingBefore(TABLE_SPACING);
    document.add(summaryTable);

    String submittedDate = requisition.getSubmittedDate() != null ? DATE_FORMAT.format(requisition.getSubmittedDate()) : "";
    summaryTable = new PdfPTable(5);
    summaryTable.addCell("Submitted");
    summaryTable.addCell("By");
    summaryTable.addCell("");
    summaryTable.addCell("Date");
    summaryTable.addCell(submittedDate);
    summaryTable.addCell("Authorized");
    summaryTable.addCell("By");
    summaryTable.addCell("");
    summaryTable.addCell("Date");
    summaryTable.addCell("");
    summaryTable.setSpacingBefore(TABLE_SPACING);
    document.add(summaryTable);

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
