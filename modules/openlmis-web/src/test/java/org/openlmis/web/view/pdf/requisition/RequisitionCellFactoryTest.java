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

import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.openlmis.rnr.domain.Column;
import org.openlmis.rnr.domain.RnrLineItem;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.List;

import static com.itextpdf.text.Element.ALIGN_LEFT;
import static com.itextpdf.text.Element.ALIGN_RIGHT;
import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openlmis.rnr.builder.RnrColumnBuilder.columnName;
import static org.openlmis.rnr.builder.RnrColumnBuilder.defaultRnrColumn;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.defaultRnrLineItem;
import static org.openlmis.rnr.builder.RnrLineItemBuilder.skipped;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.*;
import static org.openlmis.web.view.pdf.requisition.RequisitionCellFactory.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({Image.class, RequisitionCellFactory.class})
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
  public void imageCellShouldHaveRequiredPadding() throws Exception {
    PdfPCell cell = imageCell();
    assertThat(cell.getPaddingLeft(), is(CELL_PADDING));
  }

  @Test
  public void imageCellShouldHaveCenterAlignment() throws Exception {
    PdfPCell cell = imageCell();
    assertThat(cell.getHorizontalAlignment(), is(Element.ALIGN_CENTER));
    assertThat(cell.getVerticalAlignment(), is(Element.ALIGN_MIDDLE));
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


  @Test
  public void shouldGetSkippedAsImageIfLineItemIsSkippedAndImageIsFound() throws Exception {
    mockStatic(Image.class);
    List<? extends Column> rnrColumns = asList(make(a(defaultRnrColumn, with(columnName, SKIPPED))));
    RnrLineItem lineItem = make(a(defaultRnrLineItem, with(skipped, true)));

    Image image = mock(Image.class);
    PdfPCell pdfCell = new PdfPCell();
    whenNew(PdfPCell.class).withArguments(image).thenReturn(pdfCell);
    when(Image.getInstance(anyString())).thenReturn(image);

    List<PdfPCell> cells = getCells(rnrColumns, lineItem, "$");

    assertThat(cells.get(0), is(pdfCell));

  }

  @Test
  public void shouldGetSkippedAsBlankIfLineItemIsNotSkipped() throws Exception {
    List<? extends Column> rnrColumns = asList(make(a(defaultRnrColumn, with(columnName, SKIPPED))));
    RnrLineItem lineItem = make(a(defaultRnrLineItem, with(skipped, false)));
    List<PdfPCell> cells = getCells(rnrColumns, lineItem, "$");
    assertThat(cells.get(0).getPhrase().getContent(), is(""));
    assertThat(cells.get(0).getHorizontalAlignment(), is(ALIGN_LEFT));
  }


}

