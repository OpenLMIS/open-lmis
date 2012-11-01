package org.openlmis.core.service;

import org.openlmis.core.dao.ProductRepository;
import org.openlmis.core.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductService {

    private ProductRepository repository;

    @Autowired
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public void storeProducts(List<Product> productList) {
        repository.insertProducts(productList);
    }
}
