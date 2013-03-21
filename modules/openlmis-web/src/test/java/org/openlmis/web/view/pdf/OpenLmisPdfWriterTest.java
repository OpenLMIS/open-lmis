package org.openlmis.web.view.pdf;

import com.itextpdf.text.pdf.PdfWriter;
import org.junit.Test;

import static com.itextpdf.text.pdf.PdfWriter.ALLOW_PRINTING;
import static com.itextpdf.text.pdf.PdfWriter.PageLayoutSinglePage;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class OpenLmisPdfWriterTest {

  @Test
  public void shouldAttachPageEventsToWriter() throws Exception {
    PdfWriter writer = mock(PdfWriter.class);
    OpenLmisPdfWriter openLmisPdfWriter = new OpenLmisPdfWriter(writer);
    openLmisPdfWriter.attachPageEvents();
    verify(writer).setPageEvent(any(PdfPageEventHandler.class));
    verify(writer).setViewerPreferences(ALLOW_PRINTING | PageLayoutSinglePage);
  }
}
