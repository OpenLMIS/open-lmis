package org.openlmis.core.upload;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Product;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.ProductFormService;
import org.openlmis.core.service.ProductService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
import org.openlmis.upload.model.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@NoArgsConstructor
public class ProductsUpdateHandler extends AbstractModelPersistenceHandler {

    @Autowired
    ProductService productService;

    @Autowired
    ProductFormService productFormService;

    List<Product> uploadProductList;
    private List<Field> mandatoryFields;

    @Override
    public void setUp() {
        uploadProductList = new ArrayList<>();
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

        currentRecord.setActive(true);
        uploadProductList.add(currentRecord);
    }

    @Override
    public void postProcess(AuditFields auditFields) {
        mandatoryFields = getMandatoryFields(auditFields);

        updateExistingProducts();

        saveUploadProduct();
    }

    private void updateExistingProducts() {
        for (Product existingProduct : productService.getProductsForUpdateStatus()) {
            if (!hasUpload(existingProduct)) {
                productService.updateProductStatus(false,existingProduct.getId());
            } else if (!existingProduct.getActive()) {
                productService.updateProductStatus(true,existingProduct.getId());
            }
        }
    }

    private void saveUploadProduct() {
        for (Product uploadProduct : uploadProductList) {
            Product existingProduct = productService.getExisting(uploadProduct);
            if (existingProduct == null) {
                save(uploadProduct);
            } else if (!matchProduct(uploadProduct, existingProduct)) {
                SetFormAndDosageUnitByCode(existingProduct);
                existingProduct.setModifiedDate(new Date());
                save(existingProduct);
            }
        }
    }

    private void SetFormAndDosageUnitByCode(Product existingProduct) {
        existingProduct.setForm(productFormService.getProductForm(existingProduct.getForm().getCode()));
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
     * @param uploadProduct
     * @param existingProduct
     * @return if uploadProduct equal existingProduct then return true else return false
     */
    private boolean matchProduct(Product uploadProduct, Product existingProduct) {
        boolean isEqual = true;
        try {
            for (Field csvField : mandatoryFields) {
                java.lang.reflect.Field field = csvField.getField();
                field.setAccessible(true);
                Object uploadValue = field.get(uploadProduct);
                Object existingValue = field.get(existingProduct);
                if (!uploadValue.equals(existingValue)) {
                    isEqual = false;
                    field.set(existingProduct, uploadValue);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return isEqual;
    }

    private List<Field> getMandatoryFields(AuditFields auditFields) {
        return FluentIterable.from(auditFields.getImportFields()).filter(new Predicate<Field>() {
            @Override
            public boolean apply(Field field) {
                return field.isMandatory();
            }
        }).toList();
    }

    private void validateProductForm(Product product) {
        try {

            productFormService.checkProductFormExisting(product.getForm().getCode());
        } catch (DataException e) {
            throw new DataException(new OpenLmisMessage(messageService.message("error.update.products.productForm.invalid")));
        }
    }
}
