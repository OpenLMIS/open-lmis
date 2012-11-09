package org.openlmis.core.handler;

import org.openlmis.core.domain.Product;
import org.openlmis.core.service.ProductService;
import org.openlmis.upload.Importable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("productImportHandler")
public class ProductImportHandler extends AbstractModelPersistenceHandler {

    private ProductService service;

    @Autowired
    public ProductImportHandler(ProductService service) {
        this.service = service;
    }

    @Override
    protected void save(Importable importable) {
        service.save((Product)importable);
    }
}
