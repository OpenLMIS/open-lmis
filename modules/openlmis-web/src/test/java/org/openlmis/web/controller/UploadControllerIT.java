/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.db.service.DbService;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.model.AuditFields;
import org.openlmis.upload.model.ModelClass;
import org.openlmis.upload.parser.CSVParser;
import org.openlmis.web.controller.upload.MandatoryFields;
import org.openlmis.web.controller.upload.NonMandatoryFields;
import org.openlmis.web.model.UploadBean;
import org.openlmis.core.web.OpenLmisResponse;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;
import java.util.HashMap;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-upload.xml")
public class UploadControllerIT {

  public static final Long USER = 1L;

  @Mock
  CSVParser csvParser;

  @Mock
  RecordHandler handler;

  @Mock
  DbService dbService;

  @Mock
  MessageService messageService;

  @Mock
  HashMap<String, UploadBean> uploadBeansMap;

  private MockHttpServletRequest request = new MockHttpServletRequest();

  @InjectMocks
  UploadController controller;

  @Before
  public void setUp() throws Exception {

    when(uploadBeansMap.get("mandatoryFields")).thenReturn(
      new UploadBean("mandatoryFields", handler, MandatoryFields.class));
    when(uploadBeansMap.get("nonMandatoryFields")).thenReturn(
      new UploadBean("nonMandatoryFields", handler, NonMandatoryFields.class));
    when(uploadBeansMap.containsKey("mandatoryFields")).thenReturn(true);
    when(uploadBeansMap.containsKey("nonMandatoryFields")).thenReturn(true);

    MockHttpSession session = new MockHttpSession();
    session.setAttribute(USER_ID, USER);
    request.setSession(session);
  }

  @Test
  public void shouldParseCsvWithMandatoryFields() throws Exception {
    InputStream inputStream = this.getClass().getClassLoader()
      .getResourceAsStream("mandatory-fields.csv");

    MockMultipartFile multiPartFile = new MockMultipartFile("csvFile", "mock.csv", null, inputStream);

    when(messageService.message(UploadController.UPLOAD_FILE_SUCCESS, 2)).thenReturn("File uploaded successfully. " +
      "'Number of records processed: 2'");

    when(csvParser.process(any(InputStream.class), any(ModelClass.class),
      any(RecordHandler.class), any(AuditFields.class))).thenReturn(2);


    ResponseEntity<OpenLmisResponse> uploadResponse = controller.upload(multiPartFile, "mandatoryFields", request);

    assertThat(uploadResponse.getBody().getSuccessMsg(), is("File uploaded successfully. " +
      "'Number of records processed: 2'"));
  }

  @Test
  public void shouldParseCsvWithNonMandatoryFields() throws Exception {
    InputStream in = UploadControllerIT.class.getClassLoader()
      .getResourceAsStream("non-mandatory-fields.csv");
    MockMultipartFile multiPart = new MockMultipartFile("csvFile", "mock.csv", null, in);

    when(messageService.message(UploadController.UPLOAD_FILE_SUCCESS, 3)).thenReturn("File uploaded successfully. " +
      "'Number of records processed: 3'");

    when(csvParser.process(any(InputStream.class), any(ModelClass.class),
      any(RecordHandler.class), any(AuditFields.class))).thenReturn(3);

    ResponseEntity<OpenLmisResponse> uploadResponse = controller.upload(multiPart, "nonMandatoryFields", request);

    assertThat(uploadResponse.getBody().getSuccessMsg(), is("File uploaded successfully. " +
      "'Number of records processed: 3'"));
  }

}
