package org.openlmis.core.repository;

import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.mapper.ProductMapper;
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
