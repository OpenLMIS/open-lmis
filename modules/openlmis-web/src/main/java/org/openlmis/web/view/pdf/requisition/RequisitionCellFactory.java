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
import org.openlmis.rnr.domain.Column;
import org.openlmis.rnr.domain.ColumnType;
import org.openlmis.rnr.domain.LineItem;

import java.util.ArrayList;
import java.util.List;


public class RequisitionCellFactory {
  public static final float CELL_PADDING = 5f;
  public static final BaseColor HEADER_BACKGROUND = new BaseColor(210, 210, 210);
  public static final Font H2_FONT = FontFactory.getFont(FontFactory.TIMES, 20f, Font.BOLD, BaseColor.BLACK);
  public static final int WIDTH_PERCENTAGE = 100;

  public static PdfPCell numberCell(String value) {
    PdfPCell cell = getPdfPCell(value);
    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    return cell;
  }

  private static PdfPCell getPdfPCell(String value) {
    PdfPCell cell = new PdfPCell(new Phrase(value));
    cell.setPadding(CELL_PADDING);
    return cell;
  }

  public static PdfPCell textCell(String value) {
    PdfPCell cell = getPdfPCell(value);
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

  public static List<PdfPCell> getCells(List<? extends Column> visibleColumns, LineItem lineItem, String currency, String skippedText) throws NoSuchFieldException, IllegalAccessException {
    List<PdfPCell> result = new ArrayList<>();
    for (Column column : visibleColumns) {
      ColumnType columnType = column.getColumnType();
      String value = lineItem.getValue(column.getName());
      createCell(result, columnType, value, currency, skippedText);
    }
    return result;
  }

  private static void createCell(List<PdfPCell> result, ColumnType columnType, String value, String currency, String skippedText) {
    if (columnType.equals(ColumnType.TEXT)) {
      result.add(textCell(value));
    }
    if (columnType.equals(ColumnType.NUMERIC)) {
      result.add(numberCell(value));
    }
    if (columnType.equals(ColumnType.CURRENCY)) {
      result.add(numberCell(currency + value));
    }

    if (columnType.equals(ColumnType.BOOLEAN)) {
      value = value.equalsIgnoreCase("true") ? skippedText : "";
      result.add(textCell(value));
    }
  }

  public static PdfPCell categoryRow(Integer visibleColumnsSize, LineItem lineItem) {
    Chunk chunk = new Chunk(lineItem.getCategoryName(), FontFactory.getFont(FontFactory.HELVETICA_BOLD));
    PdfPCell cell = new PdfPCell(new Phrase(chunk));
    cell.setColspan(visibleColumnsSize);
    cell.setBackgroundColor(HEADER_BACKGROUND);
    cell.setPadding(CELL_PADDING);
    return cell;
  }

}