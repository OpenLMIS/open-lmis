package org.openlmis.web.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.web.view.pdf.PdfPageEventHandler;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import static org.mockito.Mockito.*;
import static org.openlmis.web.view.pdf.PdfPageEventHandler.PAGE_TEXT_HEIGHT;
import static org.openlmis.web.view.pdf.PdfPageEventHandler.PAGE_TEXT_WIDTH;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(MockitoJUnitRunner.class)
@Ignore
@PrepareForTest({PdfPageEventHandler.class, BaseFont.class})
public class RequisitionFooterTest {

  @Mock
  PdfWriter writer;

  @Mock
  Document document;

  @Test
  public void testPrintPageNumberOnEveryPage() throws Exception {
    PdfPageEventHandler requisitionFooter = new PdfPageEventHandler();
    Document document = new Document(PageSize.A4.rotate());
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    PdfWriter writer = PdfWriter.getInstance(document, stream);


    document.open();


    requisitionFooter.onOpenDocument(writer, document);
    requisitionFooter.onEndPage(writer,document);
    document.close();
    System.out.println(stream.toByteArray().toString());
  }

  @Test
  public void shouldPrintPageNumberOnEndOfPage() throws Exception {
    Date date = new Date();
    PdfPageEventHandler requisitionFooter = new PdfPageEventHandler();
    requisitionFooter.onOpenDocument(writer, document);
    PdfTemplate mockedTemplate = mock(PdfTemplate.class);
    PdfContentByte mockContentByte = mock(PdfContentByte.class);
    when(mockContentByte.createTemplate(PAGE_TEXT_WIDTH, PAGE_TEXT_HEIGHT)).thenReturn(mockedTemplate);
    whenNew(Date.class).withNoArguments().thenReturn(date);
    mockStatic(BaseFont.class);
    BaseFont mockBaseFont = mock(BaseFont.class);
    when(BaseFont.createFont()).thenReturn(mockBaseFont);
//    when(mockBaseFont.getWidthPoint());

    when(writer.getPageNumber()).thenReturn(3);
    when(document.bottom()).thenReturn(600f);


    when(writer.getDirectContent()).thenReturn(mockContentByte);
    requisitionFooter.onEndPage(writer, document);


    verify(mockContentByte).saveState();

  }
}
