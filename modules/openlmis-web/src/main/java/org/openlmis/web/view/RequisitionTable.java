package org.openlmis.web.view;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.ArrayUtils;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.web.controller.RequisitionController;
import org.openlmis.web.model.PrintRnrLineItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.openlmis.web.view.RequisitionCellFactory.categoryRow;
import static org.openlmis.web.view.RequisitionCellFactory.getCells;
import static org.openlmis.web.view.RequisitionDocument.*;

@Data
@NoArgsConstructor
public class RequisitionTable {
  private Map<String, Object> model;
  public static final int PARAGRAPH_SPACING = 30;
  public static final BaseColor ROW_GREY_BACKGROUND = new BaseColor(235, 235, 235);
  private List<RnrColumn> rnrColumnList;
  private Rnr requisition;
  private String currency;

  public RequisitionTable(Map<String, Object> model) {
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
      printRnrLineItem.calculate(requisition.getPeriod(), visibleColumns);

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
    table.setHeaderRows(1);

    setTableHeader(table, visibleColumns);
    return table;
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
}
