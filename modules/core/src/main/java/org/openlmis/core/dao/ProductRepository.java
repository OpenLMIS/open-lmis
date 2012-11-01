package org.openlmis.core.dao;

import org.openlmis.core.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductRepository {

    ProductMapper mapper;

    @Autowired
    public ProductRepository(ProductMapper mapper) {
        this.mapper = mapper;
    }

    public void insertProducts(Product product) {
        mapper.insert(product);
    }
}
