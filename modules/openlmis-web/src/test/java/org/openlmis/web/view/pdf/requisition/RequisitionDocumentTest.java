package org.openlmis.web.view.pdf.requisition;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest({RequisitionDocument.class})
public class RequisitionDocumentTest {

  @Test
  public void shouldBuildDocumentUsingRequisitionPdfModel() throws Exception {
    Map model = new HashMap<>();
    RequisitionPdfModel requisitionPdfModel =mock(RequisitionPdfModel.class);

    Document document = mock(Document.class);
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
    whenNew(RequisitionPdfModel.class).withArguments(model).thenReturn(requisitionPdfModel);

    RequisitionDocument requisitionDocument = new RequisitionDocument(document);

    requisitionDocument.buildWith(model);

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
