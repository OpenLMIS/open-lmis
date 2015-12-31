package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Product;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.ProductFormService;
import org.openlmis.core.service.ProductService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.upload.model.AuditFields;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductsUpdateHandlerTest {
    @Mock
    private ProductService productService;

    @Mock
    private MessageService messageService;

    private ProductsUpdateHandler handler;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Mock
    private ProductFormService productFormService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new ProductsUpdateHandler();

        handler.productService = productService;
        handler.productFormService = productFormService;
        handler.messageService = messageService;
    }

    @Test
    public void shouldSetUpdateProductsBeActive() throws Exception {
        List<Product> products = new ArrayList<>();
        products.add(new Product());
        AuditFields auditFields = new AuditFields();

        handler.updateProductList = products;

        handler.postProcess(auditFields);

        assertThat(products.get(0).getActive(),is(true));
        verify(productService).deActiveAllProducts();
    }
}
