package org.openlmis.web.view.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PdfPageEventHandler extends PdfPageEventHelper {

  public static final int PAGE_TEXT_WIDTH = 100;
  public static final int PAGE_TEXT_HEIGHT = 100;
  public static final float FOOTER_TEXT_SIZE = 10f;

  protected BaseFont baseFont;
  private PdfTemplate pageNumberTemplate;
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
  private float textAdjustment;

  public PdfPageEventHandler() {
    super();
    try {
      baseFont = BaseFont.createFont();
      textAdjustment = baseFont.getWidthPoint("0", FOOTER_TEXT_SIZE);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onEndPage(PdfWriter writer, Document document) {
    addPageFooterInfo(writer, document);
  }

  @Override
  public void onOpenDocument(PdfWriter writer, Document document) {
    pageNumberTemplate = writer.getDirectContent().createTemplate(PAGE_TEXT_WIDTH, PAGE_TEXT_HEIGHT);
  }

  @Override
  public void onCloseDocument(PdfWriter writer, Document document) {
    pageNumberTemplate.beginText();
    pageNumberTemplate.setFontAndSize(baseFont, FOOTER_TEXT_SIZE);
    pageNumberTemplate.setTextMatrix(0, 0);
    pageNumberTemplate.showText(String.valueOf(writer.getPageNumber() - 1));
    pageNumberTemplate.endText();
  }

  private void addPageFooterInfo(PdfWriter writer, Document document) {
    PdfContentByte contentByte = writer.getDirectContent();
    contentByte.saveState();

    contentByte.setFontAndSize(baseFont, FOOTER_TEXT_SIZE);

    contentByte.beginText();
    writeCurrentDate(document, contentByte);
    writePageNumber(writer, document, contentByte);
    contentByte.endText();

    contentByte.restoreState();
  }

  private void writeCurrentDate(Document document, PdfContentByte contentByte) {
    contentByte.setTextMatrix(document.left() + textAdjustment, document.bottom());
    String dateText = DATE_FORMAT.format(new Date());
    contentByte.showText(dateText);
  }

  private void writePageNumber(PdfWriter writer, Document document, PdfContentByte contentByte) {
    String pageNumberText = String.format("Page %s of ", writer.getPageNumber());
    float pageNumberTextSize = baseFont.getWidthPoint(pageNumberText, FOOTER_TEXT_SIZE);
    contentByte.setTextMatrix(document.right() - pageNumberTextSize - textAdjustment, document.bottom());
    contentByte.showText(pageNumberText);
    contentByte.addTemplate(pageNumberTemplate, document.right() - textAdjustment, document.bottom());
  }
}