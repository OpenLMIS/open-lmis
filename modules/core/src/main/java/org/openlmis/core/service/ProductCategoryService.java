package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.repository.ProductCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class ProductCategoryService {


  private ProductCategoryRepository productCategoryRepository;

  @Autowired
  public ProductCategoryService(ProductCategoryRepository productCategoryRepository) {
    this.productCategoryRepository = productCategoryRepository;
  }

  public Integer getProductCategoryIdByCode(String code) {
    return productCategoryRepository.getProductCategoryIdByCode(code);
  }
}
