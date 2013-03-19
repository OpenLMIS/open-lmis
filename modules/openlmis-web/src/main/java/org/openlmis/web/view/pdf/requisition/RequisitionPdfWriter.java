package org.openlmis.web.view.pdf.requisition;

import com.itextpdf.text.pdf.PdfWriter;
import org.openlmis.web.view.pdf.PdfPageEventHandler;

public class RequisitionPdfWriter {
  private PdfWriter writer;

  public RequisitionPdfWriter(PdfWriter writer) {
    this.writer = writer;
  }

  public void attachPageEvents() {
    writer.setViewerPreferences(getViewerPreferences());
    writer.setPageEvent(new PdfPageEventHandler());
  }

  protected int getViewerPreferences() {
    return PdfWriter.ALLOW_PRINTING | PdfWriter.PageLayoutSinglePage;
  }
}
