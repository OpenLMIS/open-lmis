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
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Product;
import org.openlmis.core.upload.ProductPersistenceHandler;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.db.service.DbService;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.model.AuditFields;
import org.openlmis.upload.model.ModelClass;
import org.openlmis.upload.parser.CSVParser;
import org.openlmis.web.model.UploadBean;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class UploadControllerTest {

  public static final Long USER = 1L;

  @Mock
  CSVParser csvParser;

  @Mock
  DbService dbService;

  RecordHandler handler = new ProductPersistenceHandler(null);

  private MockHttpServletRequest request;

  private UploadBean productUploadBean = new UploadBean("product", handler, Product.class, "products");

  @Spy
  Map<String, UploadBean> uploadBeansMap = new HashMap<String, UploadBean>() {{
    put("product", productUploadBean);
  }};

  @InjectMocks
  UploadController controller;

  private Date currentTimestamp;
  AuditFields auditFields;

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER);
    request.setSession(session);
    currentTimestamp = new Date();
    when(dbService.getCurrentTimestamp()).thenReturn(currentTimestamp);
    auditFields = new AuditFields(USER, currentTimestamp);
  }

  @Test
  public void shouldThrowErrorIfUnsupportedModelIsSupplied() throws Exception {
    MultipartFile multipartFile = mock(MultipartFile.class);
    ResponseEntity<OpenLmisResponse> uploadResponse = controller.upload(multipartFile, "Random", request);
    assertThat(uploadResponse.getBody().getErrorMsg(), is("Incorrect file"));
    assertThat(uploadResponse.getBody().getData().get("model").toString(), is("Random"));
  }

  @Test
  public void shouldThrowErrorIfFileIsEmpty() throws Exception {
    byte[] content = new byte[0];
    MockMultipartFile multiPartMock = new MockMultipartFile("csvFile", "mock.csv", null, content);
    ResponseEntity<OpenLmisResponse> uploadResponse = controller.upload(multiPartMock, "product", request);
    assertThat(uploadResponse.getBody().getErrorMsg(), is("File is empty"));
    assertThat(uploadResponse.getBody().getData().get("model").toString(), is("product"));
  }

  @Test
  public void shouldParseIfFileIsCsv() throws Exception {
    byte[] content = new byte[1];
    MockMultipartFile multiPartMock = new MockMultipartFile("csvFile", "mock.csv", null, content);

    ResponseEntity<OpenLmisResponse> uploadResponse = controller.upload(multiPartMock, "product", request);
    assertThat(uploadResponse.getBody().getSuccessMsg(), is("File uploaded successfully. " +
      "'Number of records created: 0', " +
      "'Number of records updated: 0'"));
    assertThat(uploadResponse.getBody().getData().get("model").toString(), is("product"));
  }

  @Test
  public void shouldUseCsvParserService() throws Exception {
    byte[] content = new byte[1];
    MultipartFile mockMultiPart = spy(new MockMultipartFile("csvFile", "mock.csv", null, content));

    InputStream mockInputStream = mock(InputStream.class);
    when(mockMultiPart.getInputStream()).thenReturn(mockInputStream);

    ResponseEntity<OpenLmisResponse> uploadResponse = controller.upload(mockMultiPart, "product", request);
    assertThat(uploadResponse.getBody().getSuccessMsg(), is("File uploaded successfully. " +
      "'Number of records created: 0', " +
      "'Number of records updated: 0'"));
    assertThat(uploadResponse.getBody().getData().get("model").toString(), is("product"));

    verify(csvParser).process(eq(mockMultiPart.getInputStream()), argThat(modelMatcher(Product.class)), eq(handler), eq(auditFields));
  }

  private ArgumentMatcher<ModelClass> modelMatcher(final Class clazz) {
    return new ArgumentMatcher<ModelClass>() {
      @Override
      public boolean matches(Object item) {
        ModelClass modelClass = (ModelClass) item;
        return modelClass.getClazz().equals(clazz);
      }
    };
  }

  @Test
  public void shouldGiveErrorIfFileNotOfTypeCsv() throws Exception {
    byte[] content = new byte[1];
    MockMultipartFile multiPartMock = new MockMultipartFile("mock.doc", content);

    ResponseEntity<OpenLmisResponse> uploadResponse = controller.upload(multiPartMock, "product", request);
    assertThat(uploadResponse.getBody().getErrorMsg(), is("Incorrect file format.  Please upload product data as a '.csv' file."));
    assertThat(uploadResponse.getBody().getData().get("model").toString(), is("product"));
  }

  @Test
  public void shouldGetListOfUploadsSupported() throws Exception {
    ResponseEntity<OpenLmisResponse> responseEntity = controller.getSupportedUploads();
    Map<String, UploadBean> result = (Map<String, UploadBean>) responseEntity.getBody().getData().get("supportedUploads");
    assertThat(result, is(uploadBeansMap));
  }

  @Test
  public void shouldGetCountOfRecordsForRespectiveModelTable() throws Exception {
    byte[] content = new byte[1];
    MultipartFile mockMultiPartFile = spy(new MockMultipartFile("csvFile", "mock.csv", null, content));

    InputStream mockInputStream = mock(InputStream.class);
    when(mockMultiPartFile.getInputStream()).thenReturn(mockInputStream);

    when(dbService.getCount(productUploadBean.getTableName())).thenReturn(10).thenReturn(25);
    when(csvParser.process(eq(mockMultiPartFile.getInputStream()), argThat(modelMatcher(Product.class)), eq(handler), eq(auditFields))).thenReturn(20);
    ResponseEntity<OpenLmisResponse> uploadResponse = controller.upload(mockMultiPartFile, "product", request);

    verify(dbService, times(2)).getCount(productUploadBean.getTableName());
    assertThat(uploadResponse.getBody().getSuccessMsg(), is("File uploaded successfully. " +
      "'Number of records created: 15', " +
      "'Number of records updated: 5'"));
    assertThat(uploadResponse.getBody().getData().get("model").toString(), is("product"));
  }

  @Test
  public void shouldGetCurrentDbTimestampAndProcessCSVFileWithIt() throws Exception {
    byte[] content = new byte[1];
    MultipartFile mockMultiPartFile = spy(new MockMultipartFile("csvFile", "mock.csv", null, content));

    InputStream mockInputStream = mock(InputStream.class);
    when(mockMultiPartFile.getInputStream()).thenReturn(mockInputStream);

    when(dbService.getCount(productUploadBean.getTableName())).thenReturn(10).thenReturn(25);
    when(csvParser.process(eq(mockMultiPartFile.getInputStream()), argThat(modelMatcher(Product.class)),
      eq(handler), eq(auditFields))).thenReturn(20);

    controller.upload(mockMultiPartFile, "product", request);


    verify(csvParser).process(eq(mockInputStream), argThat(modelMatcher(Product.class)), eq(handler), eq(auditFields));
    verify(dbService).getCurrentTimestamp();
  }


}
