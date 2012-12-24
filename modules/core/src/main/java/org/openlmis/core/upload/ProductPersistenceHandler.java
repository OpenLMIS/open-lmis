package org.openlmis.core.upload;

import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.ProductRepository;
import org.openlmis.upload.Importable;
import org.openlmis.upload.handler.AbstractModelPersistenceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("productPersistenceHandler")
public class ProductPersistenceHandler extends AbstractModelPersistenceHandler {

    private ProductRepository productRepository;

    @Autowired
    public ProductPersistenceHandler (ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    protected void save(Importable importable, String modifiedBy) {
        Product product = (Product) importable;
        product.setModifiedBy(modifiedBy);
        product.setModifiedDate(new Date());
        productRepository.insert(product);
    }
}
