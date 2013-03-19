package org.openlmis.web.view.pdf;

import com.itextpdf.text.pdf.PdfWriter;

public class OpenLmisPdfWriter {
  private PdfWriter writer;

  public OpenLmisPdfWriter(PdfWriter writer) {
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
