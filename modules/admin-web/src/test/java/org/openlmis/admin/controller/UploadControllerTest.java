package org.openlmis.admin.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.core.domain.Product;
import org.openlmis.core.handler.ProductImportHandler;
import org.openlmis.upload.parser.CSVParser;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(UploadController.class)
@ContextConfiguration(locations = {"/applicationContext-admin-web.xml"})
public class UploadControllerTest {


    @Mock
    CSVParser csvParser;

    @Mock
    ProductImportHandler handler;

    UploadController controller;

    @Before
    public void setUp() throws Exception {
        controller = new UploadController(csvParser, handler);
    }

    @Test
    public void shouldThrowErrorIfUnsupportedModelIsSupplied() throws Exception {
        MultipartFile multipartFile = mock(MultipartFile.class);
        ModelAndView modelAndView = controller.upload(multipartFile, "Random");

        assertEquals("Incorrect file", modelAndView.getModelMap().get("error"));
    }

    @Test
    public void shouldParseIfFileIsCsv() throws Exception {
        byte[] content = null;
        MockMultipartFile multiPartMock = new MockMultipartFile("csvFile", "mock.csv", null, content);

        ModelAndView modelAndView = controller.upload(multiPartMock, "product");

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
        ModelAndView modelAndView = controller.upload(mockMultiPart, "product");

        assertEquals("File upload success. Total product uploaded in the system : 0", modelAndView.getModelMap().get("message"));
        verify(csvParser).process(mockedStream, Product.class, handler);
    }

    @Test
    public void shouldGiveErrorIfFileNotOfTypeCsv() throws Exception {
        byte[] content = null;
        MockMultipartFile multiPartMock = new MockMultipartFile("mock.doc", content);

        ModelAndView modelAndView = controller.upload(multiPartMock, "product");
        assertEquals("Incorrect file format , Please upload product data as a \".csv\" file", modelAndView.getModelMap().get("error"));
        // verify(csvParser).process(mockedStream, Product.class, handler);

    }
}
