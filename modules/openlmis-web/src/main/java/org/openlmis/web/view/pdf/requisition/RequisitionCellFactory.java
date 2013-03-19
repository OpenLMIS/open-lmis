package org.openlmis.web.view.pdf.requisition;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.domain.RnrLineItem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.openlmis.web.view.pdf.requisition.RequisitionDocument.CELL_PADDING;
import static org.openlmis.web.view.pdf.requisition.RequisitionDocument.HEADER_BACKGROUND;

public class RequisitionCellFactory {

  public static PdfPCell numberCell(String value) {
    PdfPCell cell = new PdfPCell(new Phrase(value));
    cell.setPadding(CELL_PADDING);
    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    return cell;
  }

  public static PdfPCell textCell(String value) {
    PdfPCell cell = new PdfPCell(new Phrase(value));
    cell.setPadding(CELL_PADDING);
    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
    return cell;
  }

  public static List<PdfPCell> getCells(List<RnrColumn> visibleColumns, RnrLineItem lineItem, String currency) throws NoSuchFieldException, IllegalAccessException {
    List<PdfPCell> result = new ArrayList<>();
    for (RnrColumn rnrColumn : visibleColumns) {
      if (rnrColumn.getName().equals("lossesAndAdjustments")) {
        result.add(numberCell(lineItem.getTotalLossesAndAdjustments().toString()));
        continue;
      }
      if (rnrColumn.getName().equals("cost")) {
        result.add(numberCell(currency + lineItem.calculateCost()));
        continue;
      }
      if (rnrColumn.getName().equals("price")) {
        result.add(numberCell(currency + lineItem.getPrice()));
        continue;
      }
      Field field = RnrLineItem.class.getDeclaredField(rnrColumn.getName());
      field.setAccessible(true);
      Object fieldValue = field.get(lineItem);
      String cellValue = (fieldValue == null) ? "" : fieldValue.toString();
      if (rnrColumn.getName().equals("product") || rnrColumn.getName().equals("dispensingUnit") || rnrColumn.getName().equals("productCode")) {
        result.add(textCell(cellValue));
      } else {
        result.add(numberCell(cellValue));
      }
    }
    return result;
  }

  public static PdfPCell categoryRow(List<RnrColumn> visibleColumns, RnrLineItem lineItem) {
    Chunk chunk = new Chunk(lineItem.getProductCategory(), FontFactory.getFont(FontFactory.HELVETICA_BOLD));
    PdfPCell cell = new PdfPCell(new Phrase(chunk));
    cell.setColspan(visibleColumns.size());
    cell.setBackgroundColor(HEADER_BACKGROUND);
    cell.setPadding(CELL_PADDING);
    return cell;
  }
}
