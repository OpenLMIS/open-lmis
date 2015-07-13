/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.view.pdf;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPTable;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.view.pdf.requisition.RequisitionPdfModel;
import org.openlmis.web.view.pdf.requisition.RequisitionPdfWriter;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RequisitionPdfWriter.class)
public class RequisitionPdfWriterTest {

  @Mock
  PdfDocument document;

  @Mock
  MessageService messageService;

  @Mock
  ConfigurationSettingService configService;

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
    Paragraph regimenHeader = new Paragraph("Regimen");
    PdfPTable regimenTable = new PdfPTable(3);
    PdfPTable summary = new PdfPTable(4);

    whenNew(RequisitionPdfModel.class).withArguments(model, messageService, configService).thenReturn(requisitionPdfModel);

    when(requisitionPdfModel.getRequisitionHeader()).thenReturn(requisitionHeader);
    when(requisitionPdfModel.getFullSupplyHeader()).thenReturn(fullSupplyHeader);
    when(requisitionPdfModel.getFullSupplyTable()).thenReturn(fullSupplyTable);
    when(requisitionPdfModel.getNonFullSupplyTable()).thenReturn(nonFullSupplyTable);
    when(requisitionPdfModel.getNonFullSupplyHeader()).thenReturn(nonFullSupplyHeader);
    when(requisitionPdfModel.getRegimenHeader()).thenReturn(regimenHeader);
    when(requisitionPdfModel.getRegimenTable()).thenReturn(regimenTable);
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

    verify(document).add(regimenHeader);
    verify(requisitionPdfModel).getRegimenHeader();

    verify(document).add(regimenTable);
    verify(requisitionPdfModel).getRegimenTable();

    verify(document).add(summary);
    verify(requisitionPdfModel).getSummary();

    verify(document).close();
  }
}
