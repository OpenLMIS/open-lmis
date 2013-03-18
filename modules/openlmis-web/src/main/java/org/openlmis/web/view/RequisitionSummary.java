package org.openlmis.web.view;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.openlmis.core.domain.Money;
import org.openlmis.rnr.domain.Rnr;

import java.math.BigDecimal;

import static com.itextpdf.text.Element.ALIGN_LEFT;
import static com.itextpdf.text.Element.ALIGN_RIGHT;
import static org.openlmis.web.view.RequisitionPdfView.H3_FONT;

public class RequisitionSummary {

  private Rnr requisition;

  public RequisitionSummary(Rnr requisition) {
    this.requisition = requisition;
    this.requisition.fillFullSupplyCost();
    this.requisition.fillNonFullSupplyCost();
  }

  PdfPTable get() throws DocumentException {
    PdfPTable summaryTable = new PdfPTable(2);
    summaryTable.setWidths(new int[]{30,20});
    summaryTable.setSpacingBefore(RequisitionPdfView.TABLE_SPACING);
    summaryTable.setWidthPercentage(40);
    summaryTable.setHorizontalAlignment(0);

//    PdfPCell defaultCell = summaryTable.getDefaultCell();
//    defaultCell.setPadding(15);
//    defaultCell.setBorder(0);

    Chunk chunk = new Chunk("Summary ", RequisitionPdfView.H2_FONT);
    PdfPCell summaryHeaderCell = new PdfPCell(new Phrase(chunk));
    summaryHeaderCell.setColspan(2);
    summaryHeaderCell.setPadding(10);
    summaryHeaderCell.setBorder(0);
    summaryTable.addCell(summaryHeaderCell);

    summaryTable.addCell(cell("Total Cost For Full Supply Items"));
    summaryTable.addCell(cell(requisition.getFullSupplyItemsSubmittedCost(), ALIGN_RIGHT));
    summaryTable.addCell(cell("Total Cost For Non Full Supply Items"));
    summaryTable.addCell(cell(requisition.getNonFullSupplyItemsSubmittedCost(), ALIGN_RIGHT));
    summaryTable.addCell(cell("Total Cost"));
    summaryTable.addCell(cell(this.getTotalCost(requisition).toString(), ALIGN_RIGHT));
    summaryTable.addCell(cell(" "));
    summaryTable.addCell(cell(" "));
    summaryTable.addCell(cell(" "));
    summaryTable.addCell(cell(" "));

    String submittedDate = requisition.getSubmittedDate() != null ? RequisitionPdfView.DATE_FORMAT.format(requisition.getSubmittedDate()) : "";

    summaryTable.addCell(cell("Submitted By: "));
    summaryTable.addCell(cell("Date: " + submittedDate));
    summaryTable.addCell(cell("Authorized By: "));
    summaryTable.addCell(cell("Date: "));

    return summaryTable;
  }

  private PdfPCell cell(Object value, int... alignment) {
    Chunk chunk = new Chunk(value.toString());
    chunk.setFont(H3_FONT);
    Phrase phrase = new Phrase(chunk);
    PdfPCell cell = new PdfPCell(phrase);

    cell.setHorizontalAlignment((alignment.length > 0) ? alignment[0] : ALIGN_LEFT);
    cell.setBorder(0);
    cell.setPadding(15);
    return cell;
  }

  public Money getTotalCost(Rnr requisition) {
    return new Money(new BigDecimal(requisition.getFullSupplyItemsSubmittedCost().getValue().floatValue() + requisition.getNonFullSupplyItemsSubmittedCost().getValue().floatValue()));
  }
}
