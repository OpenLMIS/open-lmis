/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.view.pdf.requisition;

import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import org.junit.Test;
import org.openlmis.core.domain.Column;
import org.openlmis.rnr.domain.RnrLineItem;

import java.util.List;

import static com.itextpdf.text.Element.ALIGN_LEFT;
import static com.itextpdf.text.Element.ALIGN_RIGHT;
import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.rnr.builder.RnrColumnBuilder.columnName;
import static org.openlmis.rnr.builder.RnrColumnBuilder.defaultRnrColumn;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.defaultRnrLineItem;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.*;
import static org.openlmis.web.view.pdf.requisition.RequisitionCellFactory.*;

public class RequisitionCellFactoryTest {

  @Test
  public void textCellShouldSetGivenValue() throws Exception {
    PdfPCell cell = RequisitionCellFactory.textCell("value");
    assertThat(cell.getPhrase().getContent(), is("value"));
  }

  @Test
  public void textCellShouldHaveLeftAlignment() throws Exception {
    PdfPCell cell = RequisitionCellFactory.textCell("value");
    assertThat(cell.getHorizontalAlignment(), is(Element.ALIGN_LEFT));
    assertThat(cell.getPaddingLeft(), is(CELL_PADDING));
  }


  @Test
  public void textCellShouldHaveRequiredPadding() throws Exception {
    PdfPCell cell = RequisitionCellFactory.textCell("value");
    assertThat(cell.getPaddingLeft(), is(CELL_PADDING));
  }


  @Test
  public void numberCellShouldSetGivenValue() throws Exception {
    PdfPCell cell = numberCell("value");
    assertThat(cell.getPhrase().getContent(), is("value"));
  }

  @Test
  public void numberCellShouldHaveRightAlignment() throws Exception {
    PdfPCell cell = numberCell("value");
    assertThat(cell.getHorizontalAlignment(), is(ALIGN_RIGHT));
  }

  @Test
  public void numberCellShouldHaveRequiredPadding() throws Exception {
    PdfPCell cell = numberCell("value");
    assertThat(cell.getPaddingLeft(), is(CELL_PADDING));
  }

  @Test
  public void headingCellShouldHaveRequiredFontSizeAndShouldBeLeftAligned() throws Exception {
    PdfPCell cell = headingCell("value");
    assertThat(cell.getPhrase().getFont(), is(H2_FONT));
    assertThat(cell.getHorizontalAlignment(), is(ALIGN_LEFT));
    assertThat(cell.getPhrase().getContent(), is("value"));
  }

  @Test
  public void headingCellShouldHaveRequiredPadding() throws Exception {
    PdfPCell cell = headingCell("value");
    assertThat(cell.getPaddingLeft(), is(CELL_PADDING));
  }

  @Test
  public void shouldSetBackgroundPaddingAndColumnSpanInCategoryRowCells() throws Exception {
    RnrLineItem lineItem = make(a(defaultRnrLineItem));
    PdfPCell cell = categoryRow(0, lineItem);
    assertThat(cell.getBackgroundColor(), is(HEADER_BACKGROUND));
    assertThat(cell.getPaddingLeft(), is(CELL_PADDING));
    assertThat(cell.getColspan(), is(0));
  }

  @Test
  public void shouldGetLossesAndAdjustmentCell() throws Exception {
    List<? extends Column> rnrColumns = asList(make(a(defaultRnrColumn, with(columnName, LOSSES_AND_ADJUSTMENTS))));
    RnrLineItem lineItem = make(a(defaultRnrLineItem));
    List<PdfPCell> cells = getCells(rnrColumns, lineItem, "$");
    assertThat(cells.get(0).getPhrase().getContent(), is(lineItem.getTotalLossesAndAdjustments().toString()));
    assertThat(cells.get(0).getHorizontalAlignment(), is(ALIGN_RIGHT));
  }


  @Test
  public void shouldGetCostCell() throws Exception {
    List<? extends Column> rnrColumns = asList(make(a(defaultRnrColumn, with(columnName, COST))));
    RnrLineItem lineItem = make(a(defaultRnrLineItem));
    List<PdfPCell> cells = getCells(rnrColumns, lineItem, "$");
    assertThat(cells.get(0).getPhrase().getContent(), is("$" + lineItem.calculateCost().toString()));
    assertThat(cells.get(0).getHorizontalAlignment(), is(ALIGN_RIGHT));
  }

  @Test
  public void shouldGetPriceCell() throws Exception {
    List<? extends Column> rnrColumns = asList(make(a(defaultRnrColumn, with(columnName, PRICE))));
    RnrLineItem lineItem = make(a(defaultRnrLineItem));
    List<PdfPCell> cells = getCells(rnrColumns, lineItem, "$");
    assertThat(cells.get(0).getPhrase().getContent(), is("$" + lineItem.getPrice().toString()));
    assertThat(cells.get(0).getHorizontalAlignment(), is(ALIGN_RIGHT));
  }

  @Test
  public void shouldGetTotalCell() throws Exception {
    List<? extends Column> rnrColumns = asList(make(a(defaultRnrColumn, with(columnName, TOTAL))));
    RnrLineItem lineItem = make(a(defaultRnrLineItem));
    List<PdfPCell> cells = getCells(rnrColumns, lineItem, "$");
    assertThat(cells.get(0).getPhrase().getContent(), is("13"));
    assertThat(cells.get(0).getHorizontalAlignment(), is(ALIGN_RIGHT));
  }

  @Test
  public void shouldGetProductCell() throws Exception {
    List<? extends Column> rnrColumns = asList(make(a(defaultRnrColumn, with(columnName, PRODUCT))));
    RnrLineItem lineItem = make(a(defaultRnrLineItem));
    List<PdfPCell> cells = getCells(rnrColumns, lineItem, "$");
    assertThat(cells.get(0).getPhrase().getContent(), is(""));
    assertThat(cells.get(0).getHorizontalAlignment(), is(ALIGN_LEFT));
  }

  @Test
  public void shouldGetBeginningBalance() throws Exception {
    List<? extends Column> rnrColumns = asList(make(a(defaultRnrColumn, with(columnName, BEGINNING_BALANCE))));
    RnrLineItem lineItem = make(a(defaultRnrLineItem));
    List<PdfPCell> cells = getCells(rnrColumns, lineItem, "$");
    assertThat(cells.get(0).getPhrase().getContent(), is(lineItem.getBeginningBalance().toString()));
    assertThat(cells.get(0).getHorizontalAlignment(), is(ALIGN_RIGHT));
  }

}

