/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.view.pdf;

import com.itextpdf.text.pdf.PdfDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.core.service.MessageService;
import org.openlmis.web.view.pdf.requisition.RequisitionPdfWriter;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OpenLmisPdfView.class, PdfDocument.class})
public class OpenLmisPdfViewTest {

  @Mock
  MessageService messageService;
  @Test
  public void shouldCreateADocumentAndWriteToResponse() throws Exception {

    OpenLmisPdfView pdfView = new OpenLmisPdfView(messageService);
    Map<String, Object> model = new HashMap<>();
    HttpServletRequest request = new MockHttpServletRequest();
    HttpServletResponse response = new MockHttpServletResponse();
    RequisitionPdfWriter writer = mock(RequisitionPdfWriter.class);

    whenNew(RequisitionPdfWriter.class).withAnyArguments().thenReturn(writer);

    pdfView.renderMergedOutputModel(model, request, response);

    verify(writer).buildWith(model);

  }
}
