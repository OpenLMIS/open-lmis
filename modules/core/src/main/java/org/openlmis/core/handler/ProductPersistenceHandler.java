package org.openlmis.core.handler;

import org.openlmis.core.domain.Product;
import org.openlmis.core.service.ProductService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.handler.AbstractModelPersistenceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("productPersistenceHandler")
public class ProductPersistenceHandler extends AbstractModelPersistenceHandler {

    private ProductService service;

    @Autowired
    public ProductPersistenceHandler(ProductService service) {
        this.service = service;
    }

    @Override
    protected void save(Importable importable, String modifiedBy) {
        Product product = (Product) importable;
        product.setModifiedBy(modifiedBy);
        service.save(product);
    }
}
