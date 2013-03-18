package org.openlmis.web.view;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.rnr.domain.Rnr;

import static org.openlmis.web.view.RequisitionPdfView.*;

public class RequisitionHeader extends PdfPageEventHelper {

  private Rnr requisition;

  public RequisitionHeader(Rnr requisition) {
    this.requisition = requisition;
  }

  @Override
  public void onOpenDocument(PdfWriter writer, Document document) {
    try {
      document.add(getRequisitionHeader());

    } catch (DocumentException e) {
      throw new RuntimeException(e);
    }
  }

  private PdfPTable getRequisitionHeader() throws DocumentException {
    PdfPTable table = prepareTable();
    addHeading(table);

    Facility facility = requisition.getFacility();
    addFirstLine(facility, table);
    addSecondLine(facility, table);
    return table;
  }


  private void addHeading(PdfPTable table) throws DocumentException {
    Chunk chunk = new Chunk(String.format("Report and Requisition for: %s (%s)",
        this.requisition.getProgram().getName(),
        this.requisition.getFacility().getFacilityType().getName()), H1_FONT);

    PdfPCell cell = new PdfPCell(new Phrase(chunk));
    cell.setColspan(4);
    cell.setPadding(10);
    cell.setBorder(0);
    table.addCell(cell);
  }

  private void addFirstLine(Facility facility, PdfPTable table) {
    String text = String.format("Facility: %s", facility.getName());
    insertCell(table, text, 1);
    text = String.format("Operated By: %s", facility.getOperatedBy().getText());
    insertCell(table, text, 1);
    text = String.format("Maximum Stock level: %s", facility.getFacilityType().getNominalMaxMonth());
    insertCell(table, text, 1);
    text = String.format("Emergency Order Point: %s", facility.getFacilityType().getNominalEop());
    insertCell(table, text, 1);
  }

  private void insertCell(PdfPTable table, String text, int colspan) {
    Chunk chunk;
    chunk = new Chunk(text);
    PdfPCell cell = table.getDefaultCell();
    cell.setPhrase(new Phrase(chunk));
    cell.setColspan(colspan);
    table.addCell(cell);
  }

  private void addSecondLine(Facility facility, PdfPTable table) {
    GeographicZone geographicZone = facility.getGeographicZone();
    GeographicZone parent = geographicZone.getParent();
    StringBuilder builder = new StringBuilder();
    builder.append(geographicZone.getLevel().getName()).append(": ").append(geographicZone.getName());
    insertCell(table, builder.toString(), 1);
    builder = new StringBuilder();
    builder.append(parent.getLevel().getName()).append(": ").append(parent.getName());
    insertCell(table, builder.toString(), 1);
    builder = new StringBuilder();
    builder.append("Reporting Period: ").append(DATE_FORMAT.format(requisition.getPeriod().getStartDate())).append(" - ").
        append(DATE_FORMAT.format(requisition.getPeriod().getEndDate()));
    insertCell(table, builder.toString(), 2);
  }

  private PdfPTable prepareTable() throws DocumentException {
    int[] columnWidths = {200, 200, 200, 200};
    PdfPTable table = new PdfPTable(columnWidths.length);
    table.setWidths(columnWidths);
    table.getDefaultCell().setBackgroundColor(HEADER_BACKGROUND);
    table.getDefaultCell().setPadding(10);
    table.getDefaultCell().setBorder(0);
    table.setWidthPercentage(WIDTH_PERCENTAGE);
    table.setSpacingBefore(TABLE_SPACING);
    table.setHeaderRows(1);
    return table;
  }

}
