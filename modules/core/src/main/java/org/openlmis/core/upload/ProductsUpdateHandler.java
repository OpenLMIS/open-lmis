package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Product;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.ProductFormService;
import org.openlmis.core.service.ProductService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;
import org.openlmis.upload.model.AuditFields;
import org.openlmis.upload.model.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class ProductsUpdateHandler extends AbstractModelPersistenceHandler {

    @Autowired
    ProductService productService;

    @Autowired
    ProductFormService productFormService;

    List<Product> uploadProductList;
    private List<Field> importFields;
    private List<String> headers;

    @Override
    public void setUp() {
        uploadProductList = new ArrayList<>();
    }

    public ProductsUpdateHandler() {
        importFields = new ArrayList<>();
        loadImportFields();
    }

    @Override
    protected Product getExisting(BaseModel record) {
        return productService.getByCode(((Product) record).getCode());
    }

    @Override
    protected void save(BaseModel record) {
        productService.save((Product) record);
    }

    @Override
    public void execute(Importable importable, int rowNumber, AuditFields auditFields) {
        Product currentRecord = (Product) importable;
        Product existing = getExisting(currentRecord);

        try {
            throwExceptionIfProcessedInCurrentUpload(auditFields, existing);
        } catch (DataException exception) {
            throwException("upload.record.error", exception.getOpenLmisMessage().getCode(), rowNumber);
        }

        UpdateAuditField(auditFields, currentRecord, existing);
        validateProductForm(currentRecord);

        uploadProductList.add(currentRecord);
    }

    @Override
    public void postProcess(AuditFields auditFields) {

        headers = lowerCase(auditFields.getHeaders());

        updateExistingProducts();

        saveUploadProduct();
    }

    private void loadImportFields() {
        for (java.lang.reflect.Field field : Arrays.asList(Product.class.getDeclaredFields())) {
            if (field.getAnnotation(ImportField.class) != null && !field.getName().equals("code")) {
                importFields.add(new Field(field, field.getAnnotation(ImportField.class)));
            }
        }
    }

    private void updateExistingProducts() {
        for (Product existingProduct : productService.getProductsForUpdateStatus()) {
            if (!hasUpload(existingProduct)) {
                productService.updateProductStatus(false, existingProduct.getId());
            }
        }
    }

    private void saveUploadProduct() {
        for (Product uploadProduct : uploadProductList) {
            Product existingProduct = productService.getExisting(uploadProduct);
            if (existingProduct == null) {
                save(uploadProduct);
            } else if (!matchProduct(uploadProduct, existingProduct)) {
                existingProduct.setForm(productFormService.getProductForm(existingProduct.getForm().getCode()));
                existingProduct.setModifiedDate(new Date());
                save(existingProduct);
            }
        }
    }

    private boolean hasUpload(Product existingProduct) {
        for (Product product : uploadProductList) {
            if (existingProduct.getCode().equals(product.getCode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indicates whether some other Product is "equal to" this one. if not equal then set uploadProduct's field to existingProduct
     *
     * @param uploadProduct
     * @param existingProduct
     * @return if uploadProduct equal existingProduct then return true else return false
     */
    private boolean matchProduct(Product uploadProduct, Product existingProduct) {
        boolean isEqual = true;
        try {
            for (Field csvField : importFields) {
                if (headers.contains(csvField.getName().toLowerCase())) {
                    java.lang.reflect.Field field = csvField.getField();
                    field.setAccessible(true);
                    Object uploadValue = field.get(uploadProduct);
                    Object existingValue = field.get(existingProduct);
                    if (uploadValue == null) {
                        field.set(existingProduct, uploadValue);
                    } else if (!uploadValue.equals(existingValue)) {
                        isEqual = false;
                        field.set(existingProduct, uploadValue);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return isEqual;
    }

    private void validateProductForm(Product product) {
        try {

            productFormService.checkProductFormExisting(product.getForm().getCode());
        } catch (DataException e) {
            throw new DataException(new OpenLmisMessage(messageService.message("error.update.products.productForm.invalid")));
        }
    }

    private List<String> lowerCase(List<String> headers) {
        List<String> lowerCaseHeaders = new ArrayList<>();
        for (String header : headers) {
            lowerCaseHeaders.add(header.toLowerCase());
        }
        return lowerCaseHeaders;
    }
}
