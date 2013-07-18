/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.view.pdf.requisition;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import org.openlmis.core.domain.Column;
import org.openlmis.rnr.domain.RegimenLineItem;
import org.openlmis.rnr.domain.RnrLineItem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class RequisitionCellFactory {
  public static final float CELL_PADDING = 5f;
  public static final BaseColor HEADER_BACKGROUND = new BaseColor(210, 210, 210);
  public static final Font H2_FONT = FontFactory.getFont(FontFactory.TIMES, 20f, Font.BOLD, BaseColor.BLACK);
  public static final int WIDTH_PERCENTAGE = 100;

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

  public static PdfPCell headingCell(String value) {
    Chunk chunk = new Chunk(value, H2_FONT);
    PdfPCell cell = new PdfPCell(new Phrase(chunk));
    cell.setPadding(CELL_PADDING);
    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
    return cell;
  }

  public static List<PdfPCell> getCells(List<? extends Column> visibleColumns, RnrLineItem lineItem, String currency) throws NoSuchFieldException, IllegalAccessException {
    List<PdfPCell> result = new ArrayList<>();
    for (Column rnrColumn : visibleColumns) {
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

      if (rnrColumn.getName().equals("total") && lineItem.getQuantityReceived() != null && lineItem.getBeginningBalance() != null) {
        Integer total = lineItem.getBeginningBalance() + lineItem.getQuantityReceived();
        result.add(numberCell(total.toString()));
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

  public static List<PdfPCell> getCellsForRegimen(List<? extends Column> visibleColumns, RegimenLineItem lineItem) throws NoSuchFieldException, IllegalAccessException {
    List<PdfPCell> result = new ArrayList<>();
    for (Column regimenColumn : visibleColumns) {

      Field field = RegimenLineItem.class.getDeclaredField(regimenColumn.getName());
      field.setAccessible(true);
      Object fieldValue = field.get(lineItem);
      String cellValue = (fieldValue == null) ? "" : fieldValue.toString();
      if (regimenColumn.getName().equals("code") || regimenColumn.getName().equals("name") || regimenColumn.getName().equals("remarks")) {
        result.add(textCell(cellValue));
      } else {
        result.add(numberCell(cellValue));
      }
    }
    return result;
  }

  public static PdfPCell categoryRow(Integer visibleColumnsSize, RnrLineItem lineItem) {
    Chunk chunk = new Chunk(lineItem.getProductCategory(), FontFactory.getFont(FontFactory.HELVETICA_BOLD));
    PdfPCell cell = new PdfPCell(new Phrase(chunk));
    cell.setColspan(visibleColumnsSize);
    cell.setBackgroundColor(HEADER_BACKGROUND);
    cell.setPadding(CELL_PADDING);
    return cell;
  }

  public static PdfPCell categoryRowForRegimen(Integer visibleColumnsSize, RegimenLineItem lineItem) {
    Chunk chunk = new Chunk(lineItem.getCategory().getName(), FontFactory.getFont(FontFactory.HELVETICA_BOLD));
    PdfPCell cell = new PdfPCell(new Phrase(chunk));
    cell.setColspan(visibleColumnsSize);
    cell.setBackgroundColor(HEADER_BACKGROUND);
    cell.setPadding(CELL_PADDING);
    return cell;
  }
}
