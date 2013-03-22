/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.view.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import org.openlmis.web.view.pdf.requisition.RequisitionDocument;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Map;

public class OpenLmisPdfView extends AbstractView {

  public static final Rectangle PAGE_SIZE = new Rectangle(1500, 1059);
  public static final float LEFT_MARGIN = 20;
  public static final float RIGHT_MARGIN = 10;
  public static final float TOP_MARGIN = 10;
  public static final float BOTTOM_MARGIN = 30;

  public OpenLmisPdfView() {
    setContentType("application/pdf");
  }

  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    try (ByteArrayOutputStream stream = createTemporaryOutputStream()) {

      Document document = new Document(PAGE_SIZE, LEFT_MARGIN, RIGHT_MARGIN, TOP_MARGIN, BOTTOM_MARGIN);

      PdfWriter writer = PdfWriter.getInstance(document, stream);

      OpenLmisPdfWriter pdfWriter = new OpenLmisPdfWriter(writer);
      pdfWriter.attachPageEvents();

      new RequisitionDocument(document).buildWith(model);

      writeToResponse(response, stream);
    }
  }
}
