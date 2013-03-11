package org.openlmis.web.view;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class RequisitionPDFViewTest {



  @Test
  public void shouldBuildPdfDocumentForPrinting() throws Exception {
      RequisitionPDFView pdfView = new RequisitionPDFView();
    Document document = mock(Document.class);
    PdfWriter writer = mock(PdfWriter.class);
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    Map<String, Object> model = mock(Map.class);
    pdfView.buildPdfDocument(model, document, writer, request, response);
  }
}
