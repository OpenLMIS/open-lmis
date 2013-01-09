package org.openlmis.web.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.parser.CSVParser;
import org.openlmis.web.controller.upload.MandatoryFields;
import org.openlmis.web.controller.upload.NonMandatoryFields;
import org.openlmis.web.model.UploadBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext.xml")
public class UploadControllerIT {

  public static final String USER = "user";

  @Autowired
  CSVParser csvParser;
  @Mock
  RecordHandler handler;

  private MockHttpServletRequest request;

  @Autowired
  UploadController controller;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    Map<String, UploadBean> uploadBeansMap = new HashMap<String, UploadBean>() {{
      put("mandatoryFields", new UploadBean("mandatoryFields", handler, MandatoryFields.class));
      put("nonMandatoryFields", new UploadBean("nonMandatoryFields", handler, NonMandatoryFields.class));
    }};
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
    request.setSession(session);
    controller = new UploadController(csvParser, uploadBeansMap);
  }

  @Test
  public void shouldParseCsvWithMandatoryFields() throws Exception {
    InputStream in = UploadControllerIT.class.getClassLoader()
        .getResourceAsStream("mandatory-fields.csv");

    MockMultipartFile multiPart = new MockMultipartFile("csvFile", "mock.csv", null, in);
    String uploadPage = controller.upload(multiPart, "mandatoryFields", request);
    assertThat(uploadPage, is("redirect:/public/pages/admin/upload/index.html#/upload?" +
        "model=mandatoryFields" +
        "&success=File uploaded successfully. Total records uploaded: 2"));

    ArgumentCaptor<MandatoryFields> validUploadTypeArgumentCaptor = ArgumentCaptor.forClass(MandatoryFields.class);
    verify(handler).execute(validUploadTypeArgumentCaptor.capture(), eq(2), eq("user"));

    assertThat(validUploadTypeArgumentCaptor.getValue().getFieldA(), is("Val11"));
    assertThat(validUploadTypeArgumentCaptor.getValue().getFieldB(), is("Val12"));
    assertThat(validUploadTypeArgumentCaptor.getValue().getNestedValidUploadType().getNestedField(), is("Nested Val13"));
  }

  @Test
  public void shouldParseCsvWithNonMandatoryFields() throws Exception {
    InputStream in = UploadControllerIT.class.getClassLoader()
        .getResourceAsStream("non-mandatory-fields.csv");
    MockMultipartFile multiPart = new MockMultipartFile("csvFile", "mock.csv", null, in);

    String uploadPage = controller.upload(multiPart, "nonMandatoryFields", request);

    assertThat(uploadPage, is("redirect:/public/pages/admin/upload/index.html#/upload?" +
        "model=nonMandatoryFields" +
        "&success=File uploaded successfully. Total records uploaded: 3"));

    ArgumentCaptor<NonMandatoryFields> nonMandatoryFieldsArgumentCaptor = ArgumentCaptor.forClass(NonMandatoryFields.class);
    verify(handler).execute(nonMandatoryFieldsArgumentCaptor.capture(), eq(4), eq("user"));

  }


}
