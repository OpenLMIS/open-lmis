package org.openlmis.core.upload;

import org.openlmis.core.domain.Product;
import org.openlmis.core.service.ProductService;
import org.openlmis.upload.Importable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("productPersistenceHandler")
public class ProductPersistenceHandler extends AbstractModelPersistenceHandler {

    private ProductService productService;

    @Autowired
    public ProductPersistenceHandler (ProductService productService) {
        this.productService = productService;
    }

    @Override
    protected void save(Importable importable, Integer modifiedBy) {
        Product product = (Product) importable;
        product.setModifiedBy(modifiedBy);
        product.setModifiedDate(new Date());
        productService.save(product);
    }
}
