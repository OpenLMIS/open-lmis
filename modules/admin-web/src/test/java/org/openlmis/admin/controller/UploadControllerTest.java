package org.openlmis.admin.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Product;
import org.openlmis.core.handler.UploadHandlerFactory;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.parser.CSVParser;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(UploadController.class)
@ContextConfiguration(locations = {"/applicationContext-admin-web.xml"})
public class UploadControllerTest {


    public static final String USER = "user";
    @Mock
    CSVParser csvParser;

    UploadController controller;

    @Mock
    RecordHandler handler;

    @Mock
    private UploadHandlerFactory uploadHandlerFactory;

    private MockHttpServletRequest request;

    @Before
    public void setUp() throws Exception {
        Map<String, Class> modelMap = new HashMap<String, Class>(){{put("product", Product.class);}};
        when(uploadHandlerFactory.getHandler(any(String.class))).thenReturn(handler);
        request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
        request.setSession(session);
        controller = new UploadController(csvParser , uploadHandlerFactory, modelMap);//, new HashMap<String, RecordHandler>());
    }

    @Test
    public void shouldThrowErrorIfUnsupportedModelIsSupplied() throws Exception {
        MultipartFile multipartFile = mock(MultipartFile.class);
        ModelAndView modelAndView = controller.upload(multipartFile, "Random", request);

        assertEquals("Incorrect file", modelAndView.getModelMap().get("error"));
    }

    @Test
    public void shouldThrowErrorIfFileIsEmpty() throws Exception {
        byte[] content = new byte[0];
        MockMultipartFile multiPartMock = new MockMultipartFile("csvFile", "mock.csv", null, content);
        ModelAndView modelAndView = controller.upload(multiPartMock, "product", request);

        assertEquals("File is empty", modelAndView.getModelMap().get("error"));
    }

    @Test
    public void shouldParseIfFileIsCsv() throws Exception {
        byte[] content = new byte[1];
        MockMultipartFile multiPartMock = new MockMultipartFile("csvFile", "mock.csv", null, content);

        ModelAndView modelAndView = controller.upload(multiPartMock, "product", request);

        assertEquals("File upload success. Total product uploaded in the system : 0", modelAndView.getModelMap().get("message"));
    }

    @Test
    public void shouldUseCsvParserService() throws Exception {
        byte[] content = null;
        MultipartFile mockMultiPart = spy(new MultipartFile() {
            @Override
            public String getName() {
                return "csvFile";  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public String getOriginalFilename() {
                return "mock.csv";  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public String getContentType() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean isEmpty() {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public long getSize() {
                return 0;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        InputStream mockedStream = mock(InputStream.class);
        when(mockMultiPart.getInputStream()).thenReturn(mockedStream);
        ModelAndView modelAndView = controller.upload(mockMultiPart, "product", request);

        assertEquals("File upload success. Total product uploaded in the system : 0", modelAndView.getModelMap().get("message"));
        verify(csvParser).process(mockedStream, Product.class, handler, USER);
    }

    @Test
    public void shouldGiveErrorIfFileNotOfTypeCsv() throws Exception {
        byte[] content = new byte[1];
        MockMultipartFile multiPartMock = new MockMultipartFile("mock.doc", content);

        ModelAndView modelAndView = controller.upload(multiPartMock, "product", request);
        assertEquals("Incorrect file format , Please upload product data as a \".csv\" file", modelAndView.getModelMap().get("error"));
        // verify(csvParser).process(mockedStream, Product.class, handler);
    }

}
