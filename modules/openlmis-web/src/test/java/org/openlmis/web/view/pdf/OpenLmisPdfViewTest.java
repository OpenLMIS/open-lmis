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
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.Mock;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.view.pdf.requisition.RequisitionPdfWriter;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
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
