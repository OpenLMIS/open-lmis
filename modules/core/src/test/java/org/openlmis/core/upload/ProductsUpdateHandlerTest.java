package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.DosageUnit;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductForm;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.ProductFormService;
import org.openlmis.core.service.ProductService;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.upload.annotation.ImportField;
import org.openlmis.upload.model.AuditFields;
import org.openlmis.upload.model.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
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

        ArrayList<String> headers = new ArrayList<>();
        headers.add("Product Code");
        headers.add("Product Primary Name");
        headers.add("Product Form");
        headers.add("Product Is Active");
        headers.add("Product Strength");
        auditFields.setHeaders(headers);
        auditFields.setCurrentTimestamp(new Date());

        handler = new ProductsUpdateHandler();
        handler.productService = productService;
        handler.productFormService = productFormService;
        handler.messageService = messageService;
        List<Product> products = new ArrayList<>();
        handler.uploadProductList = products;
    }

    @Test
    public void shouldAddProductToUploadList() throws Exception {
        //given
        Product product = new Product();
        product.setCode("code");
        ProductForm form = new ProductForm();
        form.setCode("form code");
        product.setForm(form);

        //when
        handler.execute(product, 1, new AuditFields());

        //then
        assertThat(handler.uploadProductList.size(), is(1));
    }

    @Test
    public void shouldSaveUploadProductsBeActiveWhenIsNewProduct() throws Exception {
        //given
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setActive(true);
        products.add(product);
        when(productService.getExisting(product)).thenReturn(null);

        handler.uploadProductList = products;
        //when
        handler.postProcess(auditFields);

        //then
        assertThat(products.get(0).getActive(), is(true));
        verify(productService).save(products.get(0));
    }

    @Test
    public void shouldSaveUploadProductsBeActiveWhenIsExistingButHasChange() throws Exception {
        //given
        Product product = initProduct();
        product.setPrimaryName("new Name");
        handler.uploadProductList.add(product);
        ArrayList<Product> existingList = new ArrayList<>();
        Product existingProduct = initProduct();
        existingList.add(existingProduct);
        when(productService.getProductsForUpdateStatus()).thenReturn(existingList);
        when(productService.getExisting(product)).thenReturn(existingProduct);

        //when
        handler.postProcess(auditFields);

        //then
        assertThat(product.getActive(), is(true));
        verify(productService).save(existingProduct);
    }

    @Test
    public void shouldSetProductsBeDeActiveWhenExistingProductNotInUploadProducts() throws Exception {
        //given
        Product uploadProduct = initProduct();
        handler.uploadProductList.add(uploadProduct);

        ArrayList<Product> existingList = new ArrayList<>();
        Product existingProduct = initProduct();
        existingProduct.setId(1l);
        existingProduct.setActive(true);
        existingProduct.setCode("Diff Code");
        existingList.add(existingProduct);
        when(productService.getProductsForUpdateStatus()).thenReturn(existingList);
        when(productService.getExisting(uploadProduct)).thenReturn(null);

        //when
        handler.postProcess(auditFields);

        //then
        verify(productService).getProductsForUpdateStatus();
        verify(productService).updateProductStatus(false,1l);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productService).save(captor.capture());
        List<Product> captorAllValues = captor.getAllValues();
        assertEquals(uploadProduct, captorAllValues.get(0));
    }

    @Test
    public void shouldSaveUploadProductAndNotChangeExistingProductUnMadatoryFieldWhenNotEqualUpload() throws Exception {
        //given
        Product product = initProduct();
        product.setPrimaryName("new name");
        handler.uploadProductList.add(product);

        ArrayList<Product> existingList = new ArrayList<>();
        Product existingProduct = initProduct();
        existingProduct.setSpecialStorageInstructions("SpecialStorageInstructions");
        existingProduct.setFormId(10l);
        existingList.add(existingProduct);
        when(productService.getProductsForUpdateStatus()).thenReturn(existingList);
        when(productService.getExisting(product)).thenReturn(existingProduct);

        //when
        handler.postProcess(auditFields);

        //then
        verify(productService).getProductsForUpdateStatus();
        assertThat(product.getActive(), is(true));
        verify(productService).save(existingProduct);
        assertThat(existingProduct.getPrimaryName(), is("new name"));
        assertThat(existingProduct.getSpecialStorageInstructions(), is("SpecialStorageInstructions"));
        assertThat(existingProduct.getFormId(), is(10l));
    }

    @Test
    public void shouldSetFormAndDate() {
        //given
        Product product = initProduct();
        handler.uploadProductList.add(product);

        ArrayList<Product> existingProducts = new ArrayList<>();
        Product existingProduct = initProduct();
        existingProduct.setId(1l);
        existingProduct.setModifiedDate(DateUtil.parseDate("2025-12-12 12:12:12"));
        existingProduct.setPrimaryName("new name");
        existingProducts.add(existingProduct);

        when(productService.getExisting(product)).thenReturn(existingProduct);
        ProductForm existingProductForm = new ProductForm();
        existingProductForm.setCode("form code");
        existingProductForm.setDisplayOrder(2);

        when(productFormService.getProductForm(existingProduct.getForm().getCode())).thenReturn(existingProductForm);

        //when
        handler.postProcess(auditFields);

        //then
        assertThat(existingProduct.getForm().getDisplayOrder(), is(2));
        assertNotEquals(DateUtil.parseDate("2025-12-12 12:12:12"), existingProduct.getModifiedDate());
    }


    @Test
    public void shouldReactiveProductButNotChangeProductCodeWhenJustHasCaseChange() throws Exception {
        //given
        Product uploadProduct = initProduct();
        handler.uploadProductList.add(uploadProduct);

        ArrayList<Product> existingList = new ArrayList<>();
        Product existingProduct = initProduct();
        existingProduct.setId(1l);
        existingProduct.setActive(false);
        existingProduct.setCode("Code");
        existingList.add(existingProduct);
        when(productService.getProductsForUpdateStatus()).thenReturn(existingList);
        when(productService.getExisting(uploadProduct)).thenReturn(existingProduct);
        ProductForm existingProductForm = new ProductForm();
        existingProductForm.setCode("form code");
        existingProductForm.setDisplayOrder(2);
        when(productFormService.getProductForm(existingProduct.getForm().getCode())).thenReturn(existingProductForm);

        //when
        handler.postProcess(auditFields);


        //then
        verify(productService).save(existingProduct);
        assertThat(existingProduct.getActive(),is(true));
        assertThat(existingProduct.getCode(),is("Code"));
        assertNull(existingProduct.getStrength());
    }


    private Product initProduct() {
        Product product = new Product();
        product.setCode("code");
        product.setPrimaryName("Primary name");

        DosageUnit dosageUnit = new DosageUnit();
        dosageUnit.setCode("dosageUnit code");
        product.setDosageUnit(dosageUnit);
        product.setDispensingUnit("Dispensing Units");
        product.setDosesPerDispensingUnit(1);
        product.setPackSize(1);
        product.setFullSupply(true);
        product.setTracer(false);
        product.setPackRoundingThreshold(1);
        product.setRoundToZero(false);
        product.setStrength(null);
        product.setDescription("des");

        product.setActive(true);
        ProductForm form = new ProductForm();
        form.setCode("form code");
        product.setForm(form);
        return product;

    }
}
