package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProductGroupRepository;
import org.openlmis.core.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@NoArgsConstructor
public class ProductService {

  private ProductRepository repository;
  private ProductGroupRepository productGroupRepository;
  private ProductCategoryService categoryService;
  public static final String INVALID_PRODUCT_CATEGORY_CODE = "product.reference.category.invalid";
  private static final String INVALID_PRODUCT_GROUP_CODE = "product.reference.group.invalid";

  @Autowired
  public ProductService(ProductRepository repository, ProductCategoryService categoryService, ProductGroupRepository productGroupRepository) {
    this.repository = repository;
    this.categoryService = categoryService;
    this.productGroupRepository = productGroupRepository;
  }

  public void save(Product product) {
    validateAndSetProductCategory(product);

    if (product.getId() == null) {
      repository.insert(product);
      return;
    }

    setReferenceDataForProduct(product);

    repository.update(product);
  }

  private void setReferenceDataForProduct(Product product) {
    if (product.getForm() != null) {
      product.getForm().setId(repository.getProductFormIdForCode(product.getForm().getCode()));
    }
    if (product.getDosageUnit() != null) {
      product.getDosageUnit().setId(repository.getDosageUnitIdForCode(product.getDosageUnit().getCode()));
    }
    if (product.getProductGroup() != null) {
      ProductGroup productGroup = productGroupRepository.getByCode(product.getProductGroup().getCode());
      if (productGroup == null) throw new DataException(INVALID_PRODUCT_GROUP_CODE);
      product.getProductGroup().setId(productGroup.getId());

    }
  }


  private void validateAndSetProductCategory(Product product) {
    ProductCategory category = product.getCategory();
    if (category == null) return;
    String categoryCode = category.getCode();
    if (categoryCode == null || categoryCode.isEmpty()) return;
    Long categoryId = categoryService.getProductCategoryIdByCode(category.getCode());
    if (categoryId == null) {
      throw new DataException(INVALID_PRODUCT_CATEGORY_CODE);
    }
    category.setId(categoryId);
  }

  public Long getIdForCode(String code) {
    return repository.getIdByCode(code);
  }

  public Product getByCode(String code) {
    return repository.getByCode(code);
  }

}
