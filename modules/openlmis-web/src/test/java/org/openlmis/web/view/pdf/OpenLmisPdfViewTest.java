/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.view.pdf;

import com.itextpdf.text.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.web.view.pdf.requisition.RequisitionDocument;
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
import static org.openlmis.web.view.pdf.OpenLmisPdfView.*;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OpenLmisPdfView.class, Document.class})
public class OpenLmisPdfViewTest {
  @Test
  public void shouldCreateADocumentAndWriteToResponse() throws Exception {

    OpenLmisPdfView pdfView = new OpenLmisPdfView();
    Map<String, Object> model = new HashMap<>();
    HttpServletRequest request = new MockHttpServletRequest();
    HttpServletResponse response = new MockHttpServletResponse();
    Document document = mock(Document.class);
    whenNew(Document.class).withArguments(PAGE_SIZE, LEFT_MARGIN, RIGHT_MARGIN, TOP_MARGIN, BOTTOM_MARGIN).thenReturn(document);
    RequisitionDocument requisitionDocument = mock(RequisitionDocument.class);
    whenNew(RequisitionDocument.class).withArguments(document).thenReturn(requisitionDocument);
    doNothing().when(requisitionDocument).buildWith(model);

    OpenLmisPdfWriter pdfWriter = mock(OpenLmisPdfWriter.class);

    whenNew(OpenLmisPdfWriter.class).withAnyArguments().thenReturn(pdfWriter);

    pdfView.renderMergedOutputModel(model, request, response);

    verify(requisitionDocument).buildWith(model);
    verify(pdfWriter).attachPageEvents();
  }
}
