package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProductRepository;
import org.openlmis.upload.Importable;
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
  private ProductCategoryService categoryService;
  public static final String INVALID_PRODUCT_CATEGORY_CODE = "product.reference.category.invalid";

  @Autowired
  public ProductService(ProductRepository repository, ProductCategoryService categoryService) {
    this.repository = repository;
    this.categoryService = categoryService;
  }

  public void save(Product product) {
    validateAndSetProductCategory(product);

    if (product.getId() == null) {
      repository.insert(product);
      return;
    }

    setProductFormIdAndDosageUnitId(product);

    repository.update(product);
  }

  private void setProductFormIdAndDosageUnitId(Product product) {
    if(product.getForm()!=null )  {
      product.getForm().setId(repository.getProductFormIdForCode(product.getForm().getCode()));
    }
    if(product.getDosageUnit()!=null) {
      product.getDosageUnit().setId(repository.getDosageUnitIdForCode(product.getDosageUnit().getCode()));
    }
  }


  private void validateAndSetProductCategory(Product product) {
    ProductCategory category = product.getCategory();
    if (category == null) return;
    String categoryCode = category.getCode();
    if (categoryCode == null || categoryCode.isEmpty()) return;
    Integer categoryId = categoryService.getProductCategoryIdByCode(category.getCode());
    if (categoryId == null) {
      throw new DataException(INVALID_PRODUCT_CATEGORY_CODE);
    }
    category.setId(categoryId);
  }

  public Integer getIdForCode(String code) {
    return repository.getIdByCode(code);
  }

  public Product getByCode(String code) {
    return repository.getByCode(code);
  }

    public List<Product> getAllProducts() {
        return repository.getAll();
    }
}
