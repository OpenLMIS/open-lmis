package org.openlmis.admin.controller;


import org.junit.Before;
import org.junit.Ignore;
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

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ProductController.class)
@ContextConfiguration(locations = {"/applicationContext-admin-web.xml"})
public class ProductControllerTest {


    String uploadDir = "~/uploads-openlmis/";

    @Mock
    CSVParser csvParser;

    @Mock
    ProductImportHandler handler;

    ProductController controller;

    @Before
    public void setUp() throws Exception {
        controller = new ProductController(uploadDir, csvParser, handler);
    }

    @Test
    public void shouldSaveTheFileOnServer() throws Exception {
        MultipartFile multipartFile = mock(MultipartFile.class);
        String uploadPath = uploadDir + "productUpload.csv";

        controller.upload(multipartFile);

        verify(multipartFile).transferTo(new File(uploadPath));
    }

    @Test
    public void shouldUseCsvParserService() throws Exception {
        MultipartFile multipartFile = mock(MultipartFile.class);
        String uploadPath = uploadDir + "productUpload.csv";
        File mockedFile = mock(File.class);
        whenNew(File.class).withArguments(uploadPath).thenReturn(mockedFile);
        controller.upload(multipartFile);

        verify(csvParser).process(mockedFile, Product.class, handler);


    }

    @Test
    @Ignore
    public void shouldGiveErrorIfFileNotOfTypeCsv() throws Exception {
    }
}
