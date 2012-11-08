package org.openlmis.core.handler;

import org.openlmis.core.domain.Product;
import org.openlmis.core.service.ProductService;
import org.openlmis.upload.RecordHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
public class ProductImportHandler implements RecordHandler<Product> {

    private ProductService service;

    @Autowired
    public ProductImportHandler(ProductService service) {
        this.service = service;
    }

    @Override
    public void execute(Product product, int rowNumber) {
        try {
            service.save(product);
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new RuntimeException(String.format("Duplicate Product Code at record# %d", rowNumber - 1));
        } catch (DataIntegrityViolationException foreignKeyException) {
            if (foreignKeyException.getMessage().toLowerCase().contains("foreign key")) {
                throw new RuntimeException(String.format("Incorrect reference data at record# %d", rowNumber - 1));
            }
        }
    }
}
