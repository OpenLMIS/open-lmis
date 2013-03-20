package org.openlmis.web.view.pdf.requisition;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;

public class RequisitionDocument {
  private Document document;
  public static final BaseColor HEADER_BACKGROUND = new BaseColor(210, 210, 210);
  public static final Font H2_FONT = FontFactory.getFont(FontFactory.TIMES, 20f, Font.BOLD, BaseColor.BLACK);
  public static final int CELL_PADDING = 5;
  public static final int WIDTH_PERCENTAGE = 100;

  public RequisitionDocument(Document document) {
    this.document = document;
  }

  public void build(RequisitionPdfModel requisitionPdfModel) throws DocumentException, NoSuchFieldException, IllegalAccessException {
    document.open();
    document.add(requisitionPdfModel.getRequisitionHeader());

    document.add(requisitionPdfModel.getFullSupplyHeader());
    document.add(requisitionPdfModel.getFullSupplyTable());
    PdfPTable nonFullSupplyTable = requisitionPdfModel.getNonFullSupplyTable();

    if (nonFullSupplyTable != null) {
      document.newPage();
      document.add(requisitionPdfModel.getNonFullSupplyHeader());
      document.add(nonFullSupplyTable);
    }

    document.newPage();
    document.add(requisitionPdfModel.getSummary());
    document.close();
  }

}
