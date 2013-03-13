package org.openlmis.web.view;

import com.itextpdf.text.*;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RequisitionPDFView extends AbstractView {

  private static final BaseColor headerBackground = new BaseColor(210, 210, 210);
  private static final BaseColor rowGreyBackground = new BaseColor(235, 235, 235);
  private static final Font headerFont = FontFactory.getFont(FontFactory.TIMES, 20f, Font.NORMAL, BaseColor.BLACK);
  private static final Rectangle pageSize = new Rectangle(1500, 1059);
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
  private final int horizontalMargin = 10;
  private final int verticalMargin = 15;
  private final int cellPadding = 5;
  private final int widthPercentage = 100;
  private final int paragraphSpacing = 50;
  private final int tableSpacing = 25;

  public RequisitionPDFView() {
    setContentType("application/pdf");
  }

  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    // IE workaround: write into byte array first.
    try (ByteArrayOutputStream stream = createTemporaryOutputStream()) {

      Document document = new Document(pageSize, horizontalMargin, horizontalMargin, verticalMargin, verticalMargin);

      PdfWriter writer = PdfWriter.getInstance(document, stream);

      document.open();
      buildPdfDocument(model, document, writer);
      document.close();

      writeToResponse(response, stream);
    }
  }

  protected void prepareWriter(PdfWriter writer, Rnr requisition)
      throws DocumentException {
    RequisitionPdfHeaderFooter headerFooter = new RequisitionPdfHeaderFooter(requisition);
    writer.setPageEvent(headerFooter);
    writer.setViewerPreferences(getViewerPreferences());
  }

  protected int getViewerPreferences() {
    return PdfWriter.ALLOW_PRINTING | PdfWriter.PageLayoutSinglePage;
  }

  protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer) throws Exception {

    Rnr requisition = (Rnr) model.get(RequisitionController.RNR);

    prepareWriter(writer, requisition);

    List<RnrColumn> rnrColumnList = (List<RnrColumn>) model.get(RequisitionController.RNR_TEMPLATE);

    rnrColumnList = getVisibleColumns(rnrColumnList, true);
    int[] widths = getColumnWidths(rnrColumnList);


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
    Paragraph paragraph = getRequisitionSummary(requisition);
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
      table.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
      odd = false;
    } else {
      table.getDefaultCell().setBackgroundColor(rowGreyBackground);
      odd = true;
    }
    return odd;
  }


  private Paragraph getRequisitionSummary(Rnr requisition) {
    Paragraph paragraph = new Paragraph();
    Chunk chunk = new Chunk("Summary: ", FontFactory.getFont(FontFactory.TIMES, 30, Font.BOLD, BaseColor.BLACK));
    paragraph.add(chunk);
    String submittedDate = requisition.getSubmittedDate() != null ? dateFormat.format(requisition.getSubmittedDate()) : "";
    chunk = new Chunk("\n\nTotal Cost For Full Supply Items: " + requisition.getFullSupplyItemsSubmittedCost()
        + " \n\n Total Cost For Non Full Supply Items: " + requisition.getNonFullSupplyItemsSubmittedCost()
        + " \n\n Total R&R Cost: " + getTotalCost(requisition)
        + " \n\n Date of Printing: " + dateFormat.format(new Date())
        + " \n\n Date Submitted: " + submittedDate
        + " \n\n Submitted By:  \n\n Authorized By: "
        , FontFactory.getFont(FontFactory.TIMES, 20, Font.NORMAL, BaseColor.BLACK));
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
