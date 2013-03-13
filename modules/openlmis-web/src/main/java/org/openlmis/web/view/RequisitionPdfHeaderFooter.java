package org.openlmis.web.view;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.rnr.domain.Rnr;

public class RequisitionPdfHeaderFooter extends PdfPageEventHelper {
  public static final Font HEADING_FONT = FontFactory.getFont(FontFactory.TIMES, 30, Font.BOLD, BaseColor.BLACK);
  public static final Font TEXT_FONT = FontFactory.getFont(FontFactory.TIMES, 20, Font.NORMAL, BaseColor.BLACK);
  public static final String NEWLINE = "\n\n";
  public static final String TAB = "  ";
  private Rnr requisition;
  private PdfTemplate total;

  public RequisitionPdfHeaderFooter(Rnr rnr) {
    this.requisition = rnr;
  }

  @Override
  public void onOpenDocument(PdfWriter writer, Document document) {
    try {
      document.add(getRequisitionHeader());
      total = writer.getDirectContent().createTemplate(30, 16);
    } catch (DocumentException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onStartPage(PdfWriter writer, Document document) {
  }

  @Override
  public void onEndPage(PdfWriter writer, Document document) {
    PdfPTable table = new PdfPTable(3);
    try {
      table.setWidths(new int[]{24, 24, 2});
      table.setTotalWidth(527);
      table.setLockedWidth(true);
      table.getDefaultCell().setFixedHeight(20);
      table.getDefaultCell().setBorder(Rectangle.BOTTOM);
      table.addCell("HEADER");
      table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
      PdfPCell cell = new PdfPCell(Image.getInstance(total));
      cell.setBorder(Rectangle.BOTTOM);
      table.addCell(cell);
      table.writeSelectedRows(0, -1, 34, 803, writer.getDirectContent());
    } catch (DocumentException de) {
      throw new ExceptionConverter(de);
    }
  }

  @Override
  public void onCloseDocument(PdfWriter writer, Document document) {
    ColumnText.showTextAligned(total, Element.ALIGN_LEFT, new Phrase(String.valueOf(writer.getPageNumber()-1)), 2, 2, 0);
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
    Chunk chunk = new Chunk("Report and Requisition for:", HEADING_FONT);
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
