package org.openlmis.core.handler;

import org.openlmis.core.domain.Product;
import org.openlmis.core.service.ProductService;
import org.openlmis.upload.Importable;
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
    protected void save(Importable importable) {
        service.save((Product)importable);
    }
}
