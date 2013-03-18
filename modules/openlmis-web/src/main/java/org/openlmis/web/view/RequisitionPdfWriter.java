package org.openlmis.web.view;

import com.itextpdf.text.pdf.PdfWriter;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.web.controller.RequisitionController;

import java.util.Map;

public class RequisitionPdfWriter {
  private PdfWriter writer;

  public RequisitionPdfWriter(PdfWriter writer) {
    this.writer = writer;
  }

  public void attachPageEvents(Map<String, Object> model) {
    Rnr requisition = (Rnr) model.get(RequisitionController.RNR);

    writer.setViewerPreferences(getViewerPreferences());
    writer.setPageEvent(new RequisitionHeader(requisition));
    writer.setPageEvent(new RequisitionFooter());
  }

  protected int getViewerPreferences() {
    return PdfWriter.ALLOW_PRINTING | PdfWriter.PageLayoutSinglePage;
  }
}
