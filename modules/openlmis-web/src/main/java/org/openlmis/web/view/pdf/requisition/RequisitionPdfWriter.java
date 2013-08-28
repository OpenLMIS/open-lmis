/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.view.pdf.requisition;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.openlmis.core.service.MessageService;
import org.openlmis.web.view.pdf.PdfPageEventHandler;

import java.io.OutputStream;
import java.util.Map;

public class RequisitionPdfWriter extends PdfWriter {

  public static final Rectangle PAGE_SIZE = new Rectangle(1500, 1059);
  public static final float LEFT_MARGIN = 20;
  public static final float RIGHT_MARGIN = 10;
  public static final float TOP_MARGIN = 10;
  public static final float BOTTOM_MARGIN = 30;
  private MessageService messageService;

  public RequisitionPdfWriter(PdfDocument document, OutputStream stream, MessageService messageService) throws DocumentException {
    super(document, stream);
    document.addWriter(this);
    setDocumentAttributes(document);
    this.setViewerPreferences(getViewerPreferences());
    this.messageService = messageService;
    this.setPageEvent(new PdfPageEventHandler(messageService));
  }

  private void setDocumentAttributes(PdfDocument document) {
    document.setPageSize(PAGE_SIZE);
    document.setMargins(LEFT_MARGIN, RIGHT_MARGIN, TOP_MARGIN, BOTTOM_MARGIN);
  }

  public void buildWith(Map<String, Object> model)
    throws DocumentException, NoSuchFieldException, IllegalAccessException {
    RequisitionPdfModel requisitionPdfModel = new RequisitionPdfModel(model,messageService);
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
