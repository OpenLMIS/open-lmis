package org.openlmis.web.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.rnr.domain.Rnr;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.builder.FacilityBuilder.*;
import static org.openlmis.rnr.builder.RequisitionBuilder.*;

@RunWith(MockitoJUnitRunner.class)
public class RequisitionPdfHeaderFooterTest {

  @Mock
  private PdfWriter mockedWriter;

  @Mock
  private Document mockedDocument;

  @Test
  public void itShouldAddHeaderParagraphOnOpenDocument() throws Exception {
    Facility f1 = make(a(defaultFacility, with(name, "F1")));
    Rnr requisition = make(a(defaultRnr, with(facility, f1)));
    RequisitionPdfHeaderFooter headerFooter = new RequisitionPdfHeaderFooter(requisition);
    PdfContentByte pdfContentByte = mock(PdfContentByte.class);
    when(mockedWriter.getDirectContent()).thenReturn(pdfContentByte);

    headerFooter.onOpenDocument(mockedWriter, mockedDocument);

    verify(mockedDocument).add(argThat(paragraphMatcher()));
  }

  private Matcher<Paragraph> paragraphMatcher() {
    return new ArgumentMatcher<Paragraph>() {
      @Override
      public boolean matches(Object argument) {
        Paragraph paragraph = (Paragraph) argument;
        return (paragraph.getChunks().get(0).toString().equals("Report and Requisition for:") &&
            paragraph.getChunks().get(1).toString().equals("\n\nFacility: F1  Operated By: MOH  Maximum Stock level: 100  Emergency Order Point: 50.5" +
                "\n\nlevelName: Lusaka  parentLevelName: Zambia  Reporting Period: Sun Jan 01 00:01:00 IST 2012 - Wed Feb 01 00:01:00 IST 2012"));
      }
    };
  }
}


