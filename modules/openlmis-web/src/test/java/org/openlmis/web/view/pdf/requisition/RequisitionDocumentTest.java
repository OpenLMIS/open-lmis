package org.openlmis.web.view.pdf.requisition;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class RequisitionDocumentTest {

  @Test
  public void shouldBuildDocumentUsingRequisitionPdfModel() throws Exception {
    Document document = mock(Document.class);
    RequisitionPdfModel requisitionPdfModel = mock(RequisitionPdfModel.class);
    PdfPTable requisitionHeader = new PdfPTable(1);
    Paragraph fullSupplyHeader = new Paragraph("Full Supply");
    PdfPTable fullSupplyTable = new PdfPTable(2);
    Paragraph nonFullSupplyHeader = new Paragraph("Non Full Supply");
    PdfPTable nonFullSupplyTable = new PdfPTable(3);
    PdfPTable summary = new PdfPTable(4);

    when(requisitionPdfModel.getRequisitionHeader()).thenReturn(requisitionHeader);
    when(requisitionPdfModel.getFullSupplyHeader()).thenReturn(fullSupplyHeader);
    when(requisitionPdfModel.getFullSupplyTable()).thenReturn(fullSupplyTable);
    when(requisitionPdfModel.getNonFullSupplyTable()).thenReturn(nonFullSupplyTable);
    when(requisitionPdfModel.getNonFullSupplyHeader()).thenReturn(nonFullSupplyHeader);
    when(requisitionPdfModel.getSummary()).thenReturn(summary);

    new RequisitionDocument(document).build(requisitionPdfModel);

    verify(document).open();
    verify(document).add(requisitionHeader);
    verify(requisitionPdfModel).getRequisitionHeader();

    verify(document).add(fullSupplyHeader);
    verify(requisitionPdfModel).getFullSupplyHeader();

    verify(document).add(fullSupplyTable);
    verify(requisitionPdfModel).getFullSupplyTable();

    verify(document).add(nonFullSupplyHeader);
    verify(requisitionPdfModel).getNonFullSupplyHeader();

    verify(document).add(nonFullSupplyTable);
    verify(requisitionPdfModel).getNonFullSupplyTable();

    verify(document).add(summary);
    verify(requisitionPdfModel).getSummary();

    verify(document).close();
  }
}
