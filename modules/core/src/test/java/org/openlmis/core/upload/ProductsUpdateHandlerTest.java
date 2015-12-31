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
import org.openlmis.core.domain.ProductForm;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.ProductFormService;
import org.openlmis.core.service.ProductService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.upload.model.AuditFields;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    private AuditFields auditFields;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        auditFields = new AuditFields();
        auditFields.setCurrentTimestamp(new Date());

        handler = new ProductsUpdateHandler();
        handler.productService = productService;
        handler.productFormService = productFormService;
        handler.messageService = messageService;
        List<Product> products = new ArrayList<>();
        handler.updateProductList = products;
    }

    @Test
    public void shouldAddProductToUpdateList() throws Exception {
        //given
        Product product = initProduct();

        //when
        handler.execute(product, 1, new AuditFields());

        //then
        assertThat(handler.updateProductList.size(), is(1));
    }

    @Test
    public void shouldSetUpdateProductsBeActive() throws Exception {
        //given
        List<Product> products = new ArrayList<>();
        products.add(new Product());
        AuditFields auditFields = new AuditFields();
        handler.updateProductList = products;
        //when
        handler.postProcess(auditFields);

        //then
        verify(productService).deActiveAllProducts();
        assertThat(products.get(0).getActive(), is(true));
        verify(productService).save(products.get(0));
    }

    private Product initProduct() {
        Product product = new Product();
        product.setCode("code");
        ProductForm form = new ProductForm();
        form.setCode("form code");
        product.setForm(form);
        return product;
    }
}
