package org.openlmis.web.view;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.rnr.domain.Rnr;

import java.util.Date;

import static org.openlmis.web.view.RequisitionPDFView.H1_FONT;
import static org.openlmis.web.view.RequisitionPDFView.TEXT_FONT;

public class RequisitionPdfHeaderFooter extends PdfPageEventHelper {

  public static final String NEWLINE = "\n\n";
  public static final String TAB = "  ";
  public static final int PAGE_TEXT_WIDTH = 100;
  public static final int PAGE_TEXT_HEIGHT = 100;
  protected BaseFont baseFont;
  private Rnr requisition;
  private PdfTemplate totalPages;
  private float footerTextSize = 10f;
  private Date currentDate;

  public RequisitionPdfHeaderFooter(Rnr rnr) {
    super();
    this.requisition = rnr;
    this.currentDate = new Date();
    try {
      baseFont = BaseFont.createFont();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onOpenDocument(PdfWriter writer, Document document) {
    try {
      document.add(getRequisitionHeader());
      totalPages = writer.getDirectContent().createTemplate(PAGE_TEXT_WIDTH, PAGE_TEXT_HEIGHT);
    } catch (DocumentException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onEndPage(PdfWriter writer, Document document) {
    addPageNumbers(writer, document);
    addDate(writer, document);
  }

  private void addDate(PdfWriter writer, Document document) {
    PdfContentByte contentByte = writer.getDirectContent();
    contentByte.saveState();
    String text = currentDate.toString();

    float textBase = document.bottom();
    float textSize = baseFont.getWidthPoint(text, footerTextSize);

    contentByte.beginText();
    contentByte.setFontAndSize(baseFont, footerTextSize);

    float adjust = baseFont.getWidthPoint("0", footerTextSize);
    contentByte.setTextMatrix(document.left() + textSize + adjust, textBase);
    contentByte.showText(text);
    contentByte.endText();

    contentByte.restoreState();
  }

  private void addPageNumbers(PdfWriter writer, Document document) {
    PdfContentByte contentByte = writer.getDirectContent();
    contentByte.saveState();
    String text = String.format("Page %s of ", writer.getPageNumber());

    float textBase = document.bottom();
    float textSize = baseFont.getWidthPoint(text, footerTextSize);

    contentByte.beginText();
    contentByte.setFontAndSize(baseFont, footerTextSize);

    float adjust = baseFont.getWidthPoint("0", footerTextSize);
    contentByte.setTextMatrix(document.right() - textSize - adjust, textBase);
    contentByte.showText(text);
    contentByte.endText();
    contentByte.addTemplate(totalPages, document.right() - adjust, textBase);

    contentByte.restoreState();
  }

  @Override
  public void onCloseDocument(PdfWriter writer, Document document) {
    totalPages.beginText();
    totalPages.setFontAndSize(baseFont, footerTextSize);
    totalPages.setTextMatrix(0, 0);
    totalPages.showText(String.valueOf(writer.getPageNumber() - 1));
    totalPages.endText();
  }

  private Paragraph getRequisitionHeader() {
    Paragraph paragraph = new Paragraph();
    addHeading(paragraph);
    Facility facility = requisition.getFacility();
    Chunk headerLines = new Chunk("", TEXT_FONT);
    addFirstLine(facility, headerLines);
    addSecondLine(facility, headerLines);
    paragraph.add(headerLines);
    return paragraph;
  }

  private void addHeading(Paragraph paragraph) {
    Chunk chunk = new Chunk(String.format("Report and Requisition for: %s (%s)",
        this.requisition.getProgram().getName(),
        this.requisition.getFacility().getFacilityType().getName()), H1_FONT);
    paragraph.add(chunk);
  }

  private void addFirstLine(Facility facility, Chunk headerLines) {
    StringBuilder builder = new StringBuilder();
    builder.append(NEWLINE).append("Facility: ").append(facility.getName());
    builder.append(TAB).append("Operated By: ").append(facility.getOperatedBy().getText());
    builder.append(TAB).append("Maximum Stock level: ").append(facility.getFacilityType().getNominalMaxMonth());
    builder.append(TAB).append("Emergency Order Point: ").append(facility.getFacilityType().getNominalEop());

    headerLines.append(builder.toString());
  }

  private void addSecondLine(Facility facility, Chunk headerLines) {
    GeographicZone geographicZone = facility.getGeographicZone();
    GeographicZone parent = geographicZone.getParent();
    StringBuilder builder = new StringBuilder();
    builder.append(NEWLINE).append(geographicZone.getLevel().getName()).append(": ").append(geographicZone.getName());
    builder.append(TAB).append(parent.getLevel().getName()).append(": ").append(parent.getName());
    builder.append(TAB).append("Reporting Period: ").append(requisition.getPeriod().getStartDate()).append(" - ").append(requisition.getPeriod().getEndDate());

    headerLines.append(builder.toString());
  }

}
