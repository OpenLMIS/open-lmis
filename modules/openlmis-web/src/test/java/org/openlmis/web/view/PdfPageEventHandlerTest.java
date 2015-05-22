/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.view.pdf.PdfPageEventHandler;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.openlmis.web.view.pdf.PdfPageEventHandler.PAGE_TEXT_HEIGHT;
import static org.openlmis.web.view.pdf.PdfPageEventHandler.PAGE_TEXT_WIDTH;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({PdfPageEventHandler.class, BaseFont.class})
public class PdfPageEventHandlerTest {

  PdfWriter writer;

  Document document;

  @Mock
  MessageService messageService;

  @Test
  public void shouldPrintPageFooterInformation() throws Exception {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    document = spy(new Document(PageSize.A4.rotate()));
    document.open();
    writer = spy(PdfWriter.getInstance(document, stream));

    PdfPageEventHandler pdfPageEventHandler = new PdfPageEventHandler(messageService);

    PdfContentByte mockContentByte = mock(PdfContentByte.class);
    DateTime dateTime = new DateTime().withDate(2013, 3, 20);
    whenNew(Date.class).withNoArguments().thenReturn(dateTime.toDate());
    Mockito.when(messageService.message("label.page.of", 3 )).thenReturn("Page 3 of");

    doReturn(3).when(writer).getPageNumber();
    doReturn(600f).when(document).bottom();
    doReturn(600f).when(document).right();
    doReturn(mockContentByte).when(writer).getDirectContent();

    pdfPageEventHandler.onEndPage(writer, document);

    verify(mockContentByte).saveState();
    verify(mockContentByte).beginText();
    verify(mockContentByte).showText("20/03/2013");
    verify(mockContentByte).showText("Page 3 of ");
    verify(mockContentByte).addTemplate(any(PdfTemplate.class), anyFloat(), anyFloat());
    verify(mockContentByte).endText();
    verify(mockContentByte).restoreState();
  }

  @Test
  public void shouldCreatePageNumberTemplateOnOpenDocument() throws Exception {
    document = mock(Document.class);
    writer = mock(PdfWriter.class);
    PdfContentByte mockContentByte = mock(PdfContentByte.class);

    when(writer.getDirectContent()).thenReturn(mockContentByte);

    PdfPageEventHandler pdfPageEventHandler = new PdfPageEventHandler(messageService);
    pdfPageEventHandler.onOpenDocument(writer, document);
    verify(writer).getDirectContent();
    verify(mockContentByte).createTemplate(PAGE_TEXT_WIDTH, PAGE_TEXT_HEIGHT);
  }

  @Test
  public void shouldSetPageNumberTemplateOnCloseDocument() throws Exception {
    document = mock(Document.class);
    writer = mock(PdfWriter.class);
    PdfTemplate template = mock(PdfTemplate.class);
    PdfContentByte mockContentByte = mock(PdfContentByte.class);

    when(writer.getDirectContent()).thenReturn(mockContentByte);
    when(writer.getPageNumber()).thenReturn(5);
    when(mockContentByte.createTemplate(PAGE_TEXT_WIDTH, PAGE_TEXT_HEIGHT)).thenReturn(template);

    PdfPageEventHandler pdfPageEventHandler = new PdfPageEventHandler(messageService);
    pdfPageEventHandler.onOpenDocument(writer, document);
    pdfPageEventHandler.onCloseDocument(writer, document);

    verify(template).beginText();
    verify(template).setFontAndSize(any(BaseFont.class), anyFloat());
    verify(template).setTextMatrix(0, 0);
    verify(template).showText("4");
    verify(template).endText();
  }
}
