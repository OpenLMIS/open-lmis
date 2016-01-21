/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.view.pdf.requisition;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.MessageService;
import org.openlmis.web.view.pdf.PdfPageEventHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * This is the pdf writer implementation used in printing requisition report.
 */

public class RequisitionPdfWriter extends PdfWriter {

  public static final Rectangle PAGE_SIZE = new Rectangle(1500, 1059);
  public static final float LEFT_MARGIN = 20;
  public static final float RIGHT_MARGIN = 10;
  public static final float TOP_MARGIN = 10;
  public static final float BOTTOM_MARGIN = 30;
  private MessageService messageService;
  private ConfigurationSettingService configService;

  public RequisitionPdfWriter(PdfDocument document,
                              OutputStream stream,
                              MessageService messageService,
                              ConfigurationSettingService configService
                             ) throws DocumentException {
    super(document, stream);
    document.addWriter(this);
    setDocumentAttributes(document);
    this.setViewerPreferences(getViewerPreferences());
    this.messageService = messageService;
    this.configService = configService;
    this.setPageEvent(new PdfPageEventHandler(messageService));
  }

  private void setDocumentAttributes(PdfDocument document) {
    document.setPageSize(PAGE_SIZE);
    document.setMargins(LEFT_MARGIN, RIGHT_MARGIN, TOP_MARGIN, BOTTOM_MARGIN);
  }

  public void buildWith(Map<String, Object> model) throws DocumentException, NoSuchFieldException, IllegalAccessException, IOException {
    RequisitionPdfModel requisitionPdfModel = new RequisitionPdfModel(model, messageService,configService);
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

    PdfPTable regimenTable = requisitionPdfModel.getRegimenTable();
    if (regimenTable != null) {
      document.newPage();
      document.add(requisitionPdfModel.getRegimenHeader());
      document.add(regimenTable);
    }

    document.newPage();
    document.add(requisitionPdfModel.getSummary());
    document.close();
  }

  protected int getViewerPreferences() {
    return PdfWriter.ALLOW_PRINTING | PdfWriter.PageLayoutSinglePage;
  }
}
