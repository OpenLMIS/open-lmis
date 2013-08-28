/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;

import java.io.InputStream;
import java.util.HashMap;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
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
      new UploadBean("mandatoryFields", handler, MandatoryFields.class, "products"));
    when(uploadBeansMap.get("nonMandatoryFields")).thenReturn(
      new UploadBean("nonMandatoryFields", handler, NonMandatoryFields.class, "products"));
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

    when(dbService.getCount("products")).thenReturn(10).thenReturn(12);

    when(messageService.message(UploadController.UPLOAD_FILE_SUCCESS, 2, 0)).thenReturn("File uploaded successfully. " +
      "'Number of records created: 2', " +
      "'Number of records updated: 0'");

    when(csvParser.process(any(InputStream.class), any(ModelClass.class),
      any(RecordHandler.class), any(AuditFields.class))).thenReturn(2);


    ResponseEntity<OpenLmisResponse> uploadResponse = controller.upload(multiPartFile, "mandatoryFields", request);

    assertThat(uploadResponse.getBody().getSuccessMsg(), is("File uploaded successfully. " +
      "'Number of records created: 2', " +
      "'Number of records updated: 0'"));

  }

  @Test
  public void shouldParseCsvWithNonMandatoryFields() throws Exception {
    InputStream in = UploadControllerIT.class.getClassLoader()
      .getResourceAsStream("non-mandatory-fields.csv");
    MockMultipartFile multiPart = new MockMultipartFile("csvFile", "mock.csv", null, in);

    when(dbService.getCount("products")).thenReturn(10).thenReturn(12);
    when(messageService.message(UploadController.UPLOAD_FILE_SUCCESS, 2, 1)).thenReturn("File uploaded successfully. " +
      "'Number of records created: 2', " +
      "'Number of records updated: 1'");

    when(csvParser.process(any(InputStream.class), any(ModelClass.class),
      any(RecordHandler.class), any(AuditFields.class))).thenReturn(3);

    ResponseEntity<OpenLmisResponse> uploadResponse = controller.upload(multiPart, "nonMandatoryFields", request);

    assertThat(uploadResponse.getBody().getSuccessMsg(), is("File uploaded successfully. " +
      "'Number of records created: 2', " +
      "'Number of records updated: 1'"));
  }

}
