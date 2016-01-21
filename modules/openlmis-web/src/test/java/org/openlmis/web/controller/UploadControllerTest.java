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
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Product;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.upload.ProductPersistenceHandler;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.db.service.DbService;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.model.AuditFields;
import org.openlmis.upload.model.ModelClass;
import org.openlmis.upload.parser.CSVParser;
import org.openlmis.web.model.UploadBean;
import org.openlmis.core.web.OpenLmisResponse;
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
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.web.controller.UploadController.INCORRECT_FILE_FORMAT;
import static org.openlmis.web.controller.UploadController.UPLOAD_FILE_SUCCESS;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class UploadControllerTest {

  public static final Long USER = 1L;

  @Mock
  CSVParser csvParser;

  @Mock
  DbService dbService;

  @Mock
  MessageService messageService;

  RecordHandler handler = new ProductPersistenceHandler();

  private MockHttpServletRequest request;

  private UploadBean productUploadBean = new UploadBean("product", handler, Product.class);

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
    initMocks(this);
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
    OpenLmisMessage message = new OpenLmisMessage(UploadController.INCORRECT_FILE);
    when(messageService.message(message)).thenReturn("Incorrect file");
    ResponseEntity<OpenLmisResponse> uploadResponse = controller.upload(multipartFile, "Random", request);
    assertThat(uploadResponse.getBody().getErrorMsg(), is("Incorrect file"));
  }

  @Test
  public void shouldThrowErrorIfFileIsEmpty() throws Exception {
    byte[] content = new byte[0];
    MockMultipartFile multiPartMock = new MockMultipartFile("csvFile", "mock.csv", null, content);
    OpenLmisMessage message = new OpenLmisMessage(UploadController.FILE_IS_EMPTY);
    when(messageService.message(message)).thenReturn("File is empty");

    ResponseEntity<OpenLmisResponse> uploadResponse = controller.upload(multiPartMock, "product", request);
    assertThat(uploadResponse.getBody().getErrorMsg(), is("File is empty"));
  }

  @Test
  public void shouldParseIfFileIsCsv() throws Exception {
    byte[] content = new byte[1];
    MockMultipartFile multiPartMock = new MockMultipartFile("csvFile", "mock.csv", null, content);
    String uploadSuccessMessage = "File uploaded successfully.  " +
      "'Number of records processed: 0'";

    when(messageService.message(UPLOAD_FILE_SUCCESS, 0)).thenReturn(uploadSuccessMessage);

    ResponseEntity<OpenLmisResponse> uploadResponse = controller.upload(multiPartMock, "product", request);

    assertThat(uploadResponse.getBody().getSuccessMsg(), is(uploadSuccessMessage));
  }

  @Test
  public void shouldUseCsvParserService() throws Exception {
    byte[] content = new byte[1];
    MultipartFile mockMultiPart = spy(new MockMultipartFile("csvFile", "mock.csv", null, content));

    InputStream mockInputStream = mock(InputStream.class);
    when(mockMultiPart.getInputStream()).thenReturn(mockInputStream);

    String uploadSuccessMessage = "File uploaded successfully. " +
      "'Number of records processed: 0'";

    when(messageService.message(UPLOAD_FILE_SUCCESS, 0)).thenReturn(uploadSuccessMessage);

    ResponseEntity<OpenLmisResponse> uploadResponse = controller.upload(mockMultiPart, "product", request);
    assertThat(uploadResponse.getBody().getSuccessMsg(), is(uploadSuccessMessage));

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
    String errorMsg = "Incorrect file format.  Please upload product data as a '.csv' file.";

    String uploadType = "product data";
    when(messageService.message(productUploadBean.getDisplayName())).thenReturn(uploadType);
    when(messageService.message(INCORRECT_FILE_FORMAT, uploadType)).thenReturn(errorMsg);
    when(messageService.message(new OpenLmisMessage(errorMsg))).thenReturn(errorMsg);

    ResponseEntity<OpenLmisResponse> uploadResponse = controller.upload(multiPartMock, "product", request);
    assertThat(uploadResponse.getBody().getErrorMsg(), is(errorMsg));
  }

  @Test
  public void shouldGetListOfUploadsSupported() throws Exception {
    ResponseEntity<OpenLmisResponse> responseEntity = controller.getSupportedUploads();
    Map<String, UploadBean> result = (Map<String, UploadBean>) responseEntity.getBody().getData().get("supportedUploads");
    assertThat(result.size(), is(uploadBeansMap.size()));
  }

  @Test
  public void shouldGetCountOfRecordsForRespectiveModelTable() throws Exception {
    byte[] content = new byte[1];
    MultipartFile mockMultiPartFile = spy(new MockMultipartFile("csvFile", "mock.csv", null, content));

    InputStream mockInputStream = mock(InputStream.class);
    when(mockMultiPartFile.getInputStream()).thenReturn(mockInputStream);

    when(csvParser.process(eq(mockMultiPartFile.getInputStream()), argThat(modelMatcher(Product.class)), eq(handler), eq(auditFields))).thenReturn(20);

    String uploadSuccessMessage = "File uploaded successfully. " +
      "'Number of records processed: 20'";
    when(messageService.message(UPLOAD_FILE_SUCCESS, 20)).thenReturn(uploadSuccessMessage);

    ResponseEntity<OpenLmisResponse> uploadResponse = controller.upload(mockMultiPartFile, "product", request);

    assertThat(uploadResponse.getBody().getSuccessMsg(), is(uploadSuccessMessage));
  }

  @Test
  public void shouldGetCurrentDbTimestampAndProcessCSVFileWithIt() throws Exception {
    byte[] content = new byte[1];
    MultipartFile mockMultiPartFile = spy(new MockMultipartFile("csvFile", "mock.csv", null, content));

    InputStream mockInputStream = mock(InputStream.class);
    when(mockMultiPartFile.getInputStream()).thenReturn(mockInputStream);

    when(csvParser.process(eq(mockMultiPartFile.getInputStream()), argThat(modelMatcher(Product.class)),
      eq(handler), eq(auditFields))).thenReturn(20);

    controller.upload(mockMultiPartFile, "product", request);

    verify(csvParser).process(eq(mockInputStream), argThat(modelMatcher(Product.class)), eq(handler), eq(auditFields));
    verify(dbService).getCurrentTimestamp();
  }


}
