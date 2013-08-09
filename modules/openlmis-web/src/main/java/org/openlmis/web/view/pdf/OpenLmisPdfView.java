/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.view.pdf;

import com.itextpdf.text.pdf.PdfDocument;
import org.openlmis.core.service.MessageService;
import org.openlmis.web.view.pdf.requisition.RequisitionPdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Map;

@Component("requisitionPDF")
public class OpenLmisPdfView extends AbstractView {

  private MessageService messageService;

  @Autowired
  public OpenLmisPdfView(MessageService messageService) {
    this.messageService = messageService;
    setContentType("application/pdf");
  }


  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    try (ByteArrayOutputStream stream = createTemporaryOutputStream()) {

      RequisitionPdfWriter requisitionPdfWriter = new RequisitionPdfWriter(new PdfDocument(), stream, messageService);
      requisitionPdfWriter.buildWith(model);

      writeToResponse(response, stream);
    }
  }
}
