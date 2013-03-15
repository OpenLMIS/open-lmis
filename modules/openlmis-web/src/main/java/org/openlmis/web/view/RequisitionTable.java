package org.openlmis.web.view;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.lang.ArrayUtils;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequisitionTable extends AbstractView {

  public static final BaseColor HEADER_BACKGROUND = new BaseColor(210, 210, 210);
  public static final BaseColor ROW_GREY_BACKGROUND = new BaseColor(235, 235, 235);
  public static final Font H1_FONT = FontFactory.getFont(FontFactory.TIMES, 30, Font.BOLD, BaseColor.BLACK);
  public static final Font H2_FONT = FontFactory.getFont(FontFactory.TIMES, 20f, Font.BOLD, BaseColor.BLACK);
  public static final Font H3_FONT = FontFactory.getFont(FontFactory.TIMES, 17f, Font.NORMAL, BaseColor.BLACK);
  public static final Rectangle PAGE_SIZE = new Rectangle(1500, 1059);
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
  public static final int LEFT_MARGIN = 20;
  public static final int RIGHT_MARGIN = 10;
  public static final int TOP_MARGIN = 10;
  public static final int BOTTOM_MARGIN = 30;
  public static final int CELL_PADDING = 5;
  public static final int WIDTH_PERCENTAGE = 100;
  public static final int PARAGRAPH_SPACING = 30;
  public static final int TABLE_SPACING = 25;

  public RequisitionTable() {
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
      buildPdfDocument(model, document);
      document.close();

      writeToResponse(response, stream);
    }
  }

  protected void prepareWriter(PdfWriter writer, Map<String, Object> model)
      throws DocumentException {
    Rnr requisition = (Rnr) model.get(RequisitionController.RNR);

    writer.setViewerPreferences(getViewerPreferences());
    writer.setPageEvent(new RequisitionHeader(requisition));
    writer.setPageEvent(new RequisitionFooter());
  }

  protected int getViewerPreferences() {
    return PdfWriter.ALLOW_PRINTING | PdfWriter.PageLayoutSinglePage;
  }

  protected void buildPdfDocument(Map<String, Object> model, Document document) throws Exception {

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
    RequisitionSummary requisitionSummary = new RequisitionSummary();
    requisitionSummary.addRequisitionSummary(requisition, document);
  }

  private void setTableHeader(List<RnrColumn> rnrColumnList, PdfPTable table) {
    for (RnrColumn rnrColumn : rnrColumnList) {
      table.addCell(rnrColumn.getLabel());
    }
  }

  private PdfPTable prepareTable(int[] widths) throws DocumentException {
    PdfPTable table = new PdfPTable(widths.length);
    table.setWidths(widths);
    table.getDefaultCell().setBackgroundColor(HEADER_BACKGROUND);
    table.getDefaultCell().setPadding(CELL_PADDING);
    table.setWidthPercentage(WIDTH_PERCENTAGE);
    table.setSpacingBefore(TABLE_SPACING);
    table.setHeaderRows(1);
    return table;
  }

  private void addLineItems(Document document, List<RnrLineItem> lineItems, ProcessingPeriod period, List<RnrColumn> rnrColumnList, int[] widths)
      throws NoSuchFieldException, IllegalAccessException, DocumentException {
    PdfPTable table = prepareTable(widths);
    setTableHeader(rnrColumnList, table);
    boolean odd = true;
    RnrLineItem previousLineItem = null;
    for (RnrLineItem lineItem : lineItems) {
      if (previousLineItem == null || !lineItem.getProductCategory().equals(previousLineItem.getProductCategory())) {
        Chunk chunk = new Chunk(lineItem.getProductCategory(), FontFactory.getFont(FontFactory.HELVETICA_BOLD));
        PdfPCell cell = new PdfPCell(new Phrase(chunk));
        cell.setColspan(rnrColumnList.size());
        cell.setBackgroundColor(HEADER_BACKGROUND);
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
      table.getDefaultCell().setBackgroundColor(ROW_GREY_BACKGROUND);
      odd = true;
    }
    return odd;
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
      if (rnrColumn.getName().equals("reasonForRequestedQuantity")) {
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
        if (rnrColumn.getName().equals("remarks") || rnrColumn.getName().equals("reasonForRequestedQuantity"))
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
