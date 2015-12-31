package org.openlmis.core.upload;

import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Product;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.ProductFormService;
import org.openlmis.core.service.ProductService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductsUpdateHandler extends AbstractModelPersistenceHandler {

    @Autowired
    ProductService productService;

    @Autowired
    ProductFormService productFormService;

    List<Product> updateProductList;

    public ProductsUpdateHandler() {
        updateProductList = new ArrayList<>();
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
        currentRecord.setModifiedBy(auditFields.getUser());
        currentRecord.setModifiedDate(auditFields.getCurrentTimestamp());
        if (existing != null) {
            currentRecord.setId(existing.getId());
        } else {
            currentRecord.setCreatedBy(auditFields.getUser());
        }
        validateProductForm(currentRecord);
        updateProductList.add(currentRecord);
    }

    @Override
    public void postProcess(AuditFields auditFields) {
        productService.deActiveAllProducts();

        for (Product productFromNet : updateProductList) {
            productFromNet.setActive(true);
            save(productFromNet);
        }
    }

    private void validateProductForm(Product product) {
        try {
            productFormService.checkProductFormExisting(product.getForm().getCode());
        } catch (DataException e) {
            throw new DataException(new OpenLmisMessage(messageService.message("error.update.products.productForm.invalid")));
        }
    }
}
