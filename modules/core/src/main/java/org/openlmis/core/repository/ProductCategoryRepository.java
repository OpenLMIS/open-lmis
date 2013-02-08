package org.openlmis.core.repository;


import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProductCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor
public class ProductCategoryRepository {


  private ProductCategoryMapper categoryMapper;
  public static final String DUPLICATE_CATEGORY_NAME = "product.category.name.duplicate";

  @Autowired
  public ProductCategoryRepository(ProductCategoryMapper categoryMapper) {
    this.categoryMapper = categoryMapper;
  }

  public void save(ProductCategory productCategory) {
    if (updateCategoryIfExists(productCategory)) return;
    try {
      categoryMapper.insert(productCategory);
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException(DUPLICATE_CATEGORY_NAME);
    } catch (DataIntegrityViolationException dataIntegrityViolationException) {
      String errorMessage = dataIntegrityViolationException.getMessage().toLowerCase();
      if (errorMessage.contains("foreign key") || errorMessage.contains("violates not-null constraint")) {
        throw new DataException("Missing/Invalid Reference data");
      } else {
        throw new DataException("Incorrect data length");
      }
    }
  }

  private boolean updateCategoryIfExists(ProductCategory productCategory) {
    ProductCategory category = categoryMapper.getProductCategoryByCode(productCategory.getCode());
    if (category != null) {
      category.setName(productCategory.getName());
      categoryMapper.update(category);
      return true;
    }
    return false;
  }
}
