package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProductMapperExtension;
import org.openlmis.core.repository.mapper.ProductGroupMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class ProductRepositoryExtension {

    @Autowired
    private ProductMapperExtension mapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    public ProductRepositoryExtension(ProductMapperExtension productMapperExt) {
        this.mapper = productMapperExt;
    }

    public List<Product> searchProduct(String productSearchParam) {
        return mapper.getProductWithSearchedName(productSearchParam);
    }

    public List<Product> getProductList() {
        return mapper.getAllProducts_Ext();
    }

    public Product getByProductId(Long id){
        return mapper.getProductById(id);
    }

    // mahmed - 07.11.2013 delete product
    public void deleteById(Long productId) {
        mapper.deleteById_Ext(productId);
    }

    // mahmed - 07.11.2013 delete product
    public void restoreById(Long productId) {
        mapper.restoreById_Ext(productId);
    }

    public void addProduct(Product product) {
        productMapper.insert(product);

    }

    public void updateProduct(Product product) {
        productMapper.update(product);

    }
}
