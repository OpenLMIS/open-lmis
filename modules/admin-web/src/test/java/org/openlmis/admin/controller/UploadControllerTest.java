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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

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
    public void shouldUseCsvParserService() throws Exception {
        MultipartFile multipartFile = mock(MultipartFile.class);

        InputStream mockedStream = mock(InputStream.class);
        when(multipartFile.getInputStream()).thenReturn(mockedStream);
        ModelAndView modelAndView = controller.upload(multipartFile, "product");

        assertEquals("uploaded successfully", modelAndView.getModelMap().get("message"));
        verify(csvParser).process(mockedStream, Product.class, handler);
    }

    @Test
    public void shouldGiveErrorIfFileNotOfTypeCsv() throws Exception {
    }
}
