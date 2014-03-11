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

import com.itextpdf.text.pdf.PdfDocument;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.MessageService;
import org.openlmis.web.view.pdf.requisition.RequisitionPdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * This entity is used to handle PDF view for requisition.
 */

@Component("requisitionPDF")
public class OpenLmisPdfView extends AbstractView {

  private MessageService messageService;

  @Autowired
  private ConfigurationSettingService configService;

  @Autowired
  public OpenLmisPdfView(MessageService messageService) {
    this.messageService = messageService;
    setContentType("application/pdf");
  }

  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    try (ByteArrayOutputStream stream = createTemporaryOutputStream()) {

      RequisitionPdfWriter requisitionPdfWriter = new RequisitionPdfWriter(new PdfDocument(), stream, messageService, configService);
      requisitionPdfWriter.buildWith(model);

      writeToResponse(response, stream);
    }
  }
}
