package org.openlmis.web.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Product;
import org.openlmis.upload.RecordHandler;
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
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UploadControllerTest {

  public static final String USER = "user";
  @Mock
  CSVParser csvParser;

  UploadController controller;

  @Mock
  RecordHandler handler;


  private MockHttpServletRequest request;
  private UploadBean productUploadBean;

  Map<String, UploadBean> uploadBeansMap;

  @Before
  public void setUp() throws Exception {
    productUploadBean = new UploadBean("product", handler, Product.class);
    uploadBeansMap = new HashMap<String, UploadBean>() {{
      put("product", productUploadBean);
    }};
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
    request.setSession(session);
    controller = new UploadController(csvParser, uploadBeansMap);
  }

  @Test
  public void shouldThrowErrorIfUnsupportedModelIsSupplied() throws Exception {
    MultipartFile multipartFile = mock(MultipartFile.class);
    String uploadPage = controller.upload(multipartFile, "Random", request);
    assertThat(uploadPage, is("redirect:/public/pages/admin/upload/index.html#/upload?model=Random&" +
        "error=Incorrect file"));
  }

  @Test
  public void shouldThrowErrorIfFileIsEmpty() throws Exception {
    byte[] content = new byte[0];
    MockMultipartFile multiPartMock = new MockMultipartFile("csvFile", "mock.csv", null, content);
    String uploadPage = controller.upload(multiPartMock, "product", request);
    assertThat(uploadPage, is("redirect:/public/pages/admin/upload/index.html#/upload?model=product&" +
        "error=File is empty"));
  }

  @Test
  public void shouldParseIfFileIsCsv() throws Exception {
    byte[] content = new byte[1];
    MockMultipartFile multiPartMock = new MockMultipartFile("csvFile", "mock.csv", null, content);

    String uploadPage = controller.upload(multiPartMock, "product", request);
    assertThat(uploadPage, is("redirect:/public/pages/admin/upload/index.html#/upload?model=product&" +
        "success=File uploaded successfully. Total records uploaded: 0"));
  }

  @Test
  public void shouldUseCsvParserService() throws Exception {
    byte[] content = new byte[1];
    MultipartFile mockMultiPart = spy(new MockMultipartFile("csvFile", "mock.csv", null, content));

    InputStream mockInputStream = mock(InputStream.class);
    when(mockMultiPart.getInputStream()).thenReturn(mockInputStream);

    String uploadPage = controller.upload(mockMultiPart, "product", request);

    assertThat(uploadPage, is("redirect:/public/pages/admin/upload/index.html#/upload?model=product&" +
        "success=File uploaded successfully. Total records uploaded: 0"));

    verify(csvParser).process(eq(mockMultiPart.getInputStream()), argThat(modelMatcher(Product.class)), eq(handler), eq(USER));
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

    String uploadPage = controller.upload(multiPartMock, "product", request);
    assertThat(uploadPage, is("redirect:/public/pages/admin/upload/index.html#/upload?model=product&" +
        "error=Incorrect file format , Please upload product data as a \".csv\" file"));
  }

  @Test
  public void shouldGetListOfUploadsSupported() throws Exception {
    ResponseEntity<OpenLmisResponse> responseEntity = controller.getSupportedUploads();
    Map<String, UploadBean> result = (Map<String, UploadBean>) responseEntity.getBody().getData().get("supportedUploads");
    assertThat(result, is(uploadBeansMap));
  }
}
