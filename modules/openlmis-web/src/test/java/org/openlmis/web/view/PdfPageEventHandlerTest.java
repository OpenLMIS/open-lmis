/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.view.pdf.PdfPageEventHandler;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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

@RunWith(PowerMockRunner.class)
@Category(UnitTests.class)
@PrepareForTest({PdfPageEventHandler.class, BaseFont.class})
public class PdfPageEventHandlerTest {

  PdfWriter writer;

  Document document;

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
