package org.openlmis.web.view.pdf.requisition;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;

public class RequisitionDocument {
  private Document document;

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
