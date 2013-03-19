package org.openlmis.web.view.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.*;

import java.util.Date;

import static org.openlmis.web.view.pdf.requisition.RequisitionPdfView.DATE_FORMAT;

public class PdfPageEventHandler extends PdfPageEventHelper {

  public static final int PAGE_TEXT_WIDTH = 100;
  public static final int PAGE_TEXT_HEIGHT = 100;
  public static final float FOOTER_TEXT_SIZE = 10f;

  protected BaseFont baseFont;
  private PdfTemplate pageNumberTemplate;
  private Date currentDate;

  public PdfPageEventHandler() {
    super();
    this.currentDate = new Date();

    try {
      baseFont = BaseFont.createFont();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public void onEndPage(PdfWriter writer, Document document) {
    addPageNumbers(writer, document);
    addDate(writer, document);
  }

  @Override
  public void onOpenDocument(PdfWriter writer, Document document) {
    pageNumberTemplate = writer.getDirectContent().createTemplate(PAGE_TEXT_WIDTH, PAGE_TEXT_HEIGHT);
  }

  private void addDate(PdfWriter writer, Document document) {
    PdfContentByte contentByte = writer.getDirectContent();
    contentByte.saveState();
    String text = DATE_FORMAT.format(currentDate);

    float textBase = document.bottom();

    contentByte.beginText();
    contentByte.setFontAndSize(baseFont, FOOTER_TEXT_SIZE);

    float adjust = baseFont.getWidthPoint("0", FOOTER_TEXT_SIZE);
    contentByte.setTextMatrix(document.left() + adjust, textBase);
    contentByte.showText(text);
    contentByte.endText();

    contentByte.restoreState();
  }

  private void addPageNumbers(PdfWriter writer, Document document) {
    String text = String.format("Page %s of ", writer.getPageNumber());
    PdfContentByte contentByte = writer.getDirectContent();
    contentByte.saveState();

    float textBase = document.bottom();
    float textSize = baseFont.getWidthPoint(text, FOOTER_TEXT_SIZE);

    contentByte.beginText();
    contentByte.setFontAndSize(baseFont, FOOTER_TEXT_SIZE);

    float adjust = baseFont.getWidthPoint("0", FOOTER_TEXT_SIZE);
    contentByte.setTextMatrix(document.right() - textSize - adjust, textBase);
    contentByte.showText(text);
    contentByte.endText();
    contentByte.addTemplate(pageNumberTemplate, document.right() - adjust, textBase);

    contentByte.restoreState();
  }

  @Override
  public void onCloseDocument(PdfWriter writer, Document document) {
    pageNumberTemplate.beginText();
    pageNumberTemplate.setFontAndSize(baseFont, FOOTER_TEXT_SIZE);
    pageNumberTemplate.setTextMatrix(0, 0);
    pageNumberTemplate.showText(String.valueOf(writer.getPageNumber() - 1));
    pageNumberTemplate.endText();
  }
}
