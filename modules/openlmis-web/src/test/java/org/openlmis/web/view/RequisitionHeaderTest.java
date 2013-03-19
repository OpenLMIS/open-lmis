package org.openlmis.web.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class RequisitionHeaderTest {

  @Mock
  @SuppressWarnings("unused")
  private PdfWriter mockedWriter;

  @Mock
  @SuppressWarnings("unused")
  private Document mockedDocument;


  private Matcher<Paragraph> paragraphMatcher() {
    return new ArgumentMatcher<Paragraph>() {
      @Override
      public boolean matches(Object argument) {
        Paragraph paragraph = (Paragraph) argument;
        return (paragraph.getChunks().get(0).toString().equals("Report and Requisition for: Yellow Fever (Central Warehouse)") &&
            paragraph.getChunks().get(1).toString().equals("\n\nFacility: F1  Operated By: MOH  Maximum Stock level: 100  Emergency Order Point: 50.5" +
                "\n\nlevelName: Lusaka  parentLevelName: Zambia  Reporting Period: Sun Jan 01 00:01:00 IST 2012 - Wed Feb 01 00:01:00 IST 2012"));
      }
    };
  }
}


