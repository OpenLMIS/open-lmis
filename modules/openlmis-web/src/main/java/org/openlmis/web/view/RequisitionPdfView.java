package org.openlmis.web.view;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Map;

public class RequisitionPdfView extends AbstractView {

  public static final BaseColor HEADER_BACKGROUND = new BaseColor(210, 210, 210);
  public static final Font H1_FONT = FontFactory.getFont(FontFactory.TIMES, 30, Font.BOLD, BaseColor.BLACK);
  public static final Font H2_FONT = FontFactory.getFont(FontFactory.TIMES, 20f, Font.BOLD, BaseColor.BLACK);
  public static final Font H3_FONT = FontFactory.getFont(FontFactory.TIMES, 17f, Font.NORMAL, BaseColor.BLACK);
  public static final Rectangle PAGE_SIZE = new Rectangle(1500, 1059);
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
  public static final int LEFT_MARGIN = 20;
  public static final int RIGHT_MARGIN = 10;
  public static final int TOP_MARGIN = 10;
  public static final int BOTTOM_MARGIN = 30;
  public static final int WIDTH_PERCENTAGE = 100;
  public static final int TABLE_SPACING = 25;

  public RequisitionPdfView() {
    setContentType("application/pdf");
  }

  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    // IE workaround: write into byte array first.
    try (ByteArrayOutputStream stream = createTemporaryOutputStream()) {

      Document document = new Document(PAGE_SIZE, LEFT_MARGIN, RIGHT_MARGIN, TOP_MARGIN, BOTTOM_MARGIN);

      PdfWriter writer = PdfWriter.getInstance(document, stream);
      RequisitionPdfWriter pDfWriter = new RequisitionPdfWriter(writer);
      pDfWriter.attachPageEvents(model);

      RequisitionDocument requisitionDocument = new RequisitionDocument(document);
      requisitionDocument.build(model);
      writeToResponse(response, stream);
    }
  }



}
