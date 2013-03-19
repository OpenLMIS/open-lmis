package org.openlmis.web.view.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import org.openlmis.web.view.pdf.requisition.RequisitionDocument;
import org.openlmis.web.view.pdf.requisition.RequisitionPdfModel;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Map;

public class OpenLmisPdfView extends AbstractView {

  public static final Rectangle PAGE_SIZE = new Rectangle(1500, 1059);
  public static final int LEFT_MARGIN = 20;
  public static final int RIGHT_MARGIN = 10;
  public static final int TOP_MARGIN = 10;
  public static final int BOTTOM_MARGIN = 30;

  public OpenLmisPdfView() {
    setContentType("application/pdf");
  }

  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    try (ByteArrayOutputStream stream = createTemporaryOutputStream()) {

      Document document = new Document(PAGE_SIZE, LEFT_MARGIN, RIGHT_MARGIN, TOP_MARGIN, BOTTOM_MARGIN);
      RequisitionPdfModel requisitionPdfModel = new RequisitionPdfModel(model);

      PdfWriter writer = PdfWriter.getInstance(document, stream);

      OpenLmisPdfWriter pDfWriter = new OpenLmisPdfWriter(writer);
      pDfWriter.attachPageEvents();

      RequisitionDocument requisitionDocument = new RequisitionDocument(document);
      requisitionDocument.build(requisitionPdfModel);

      writeToResponse(response, stream);
    }
  }
}
