package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;
import org.openlmis.report.mapper.ProductListMapper;
import org.openlmis.report.model.dto.ProductList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mahmed
 * Date: 6/19/13
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
@NoArgsConstructor
@Service
public class ProductListDataProvider {

  @Autowired
   private ProductListMapper productListMapper;

    // mahmed 07.11.2013 full product list
    public List<ProductList> getProductList() {
        return productListMapper.getList();
    }

    // mahmed - 07.11.2013 delete product
    public void deleteById(Long productId) {
        productListMapper.deleteById(productId);
    }

    // mahmed - 07.11.2013 delete product
    public void restoreById(Long productId) {
        productListMapper.restoreById(productId);
    }

    // mahmed - 07.11.2013 delete product
    public Product get(Long id) {
        return productListMapper.getProductById(id);
    }


}
