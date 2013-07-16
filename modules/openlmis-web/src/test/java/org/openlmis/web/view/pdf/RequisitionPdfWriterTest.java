/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.view.pdf;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPTable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.web.view.pdf.requisition.RequisitionPdfModel;
import org.openlmis.web.view.pdf.requisition.RequisitionPdfWriter;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest(RequisitionPdfWriter.class)
@RunWith(PowerMockRunner.class)
public class RequisitionPdfWriterTest {

  @Mock
  PdfDocument document;

  @Mock
  OutputStream outputStream;

  @InjectMocks
  RequisitionPdfWriter writer;


  @Test
  public void shouldBuildDocumentUsingRequisitionPdfModel() throws Exception {
    Map model = new HashMap<>();
    RequisitionPdfModel requisitionPdfModel = PowerMockito.mock(RequisitionPdfModel.class);

    PdfPTable requisitionHeader = new PdfPTable(1);
    Paragraph fullSupplyHeader = new Paragraph("Full Supply");
    PdfPTable fullSupplyTable = new PdfPTable(2);
    Paragraph nonFullSupplyHeader = new Paragraph("Non Full Supply");
    PdfPTable nonFullSupplyTable = new PdfPTable(3);
    PdfPTable summary = new PdfPTable(4);

    whenNew(RequisitionPdfModel.class).withArguments(model).thenReturn(requisitionPdfModel);

    when(requisitionPdfModel.getRequisitionHeader()).thenReturn(requisitionHeader);
    when(requisitionPdfModel.getFullSupplyHeader()).thenReturn(fullSupplyHeader);
    when(requisitionPdfModel.getFullSupplyTable()).thenReturn(fullSupplyTable);
    when(requisitionPdfModel.getNonFullSupplyTable()).thenReturn(nonFullSupplyTable);
    when(requisitionPdfModel.getNonFullSupplyHeader()).thenReturn(nonFullSupplyHeader);
    when(requisitionPdfModel.getSummary()).thenReturn(summary);

    writer.buildWith(model);

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
