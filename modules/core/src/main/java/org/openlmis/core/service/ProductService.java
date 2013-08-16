package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
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

  @Autowired
  private ProductRepository repository;

  @Autowired
  private ProductGroupRepository productGroupRepository;

  @Autowired
  private ProductCategoryService categoryService;

  @Autowired
  ProgramProductService programProductService;

  @Autowired
  ProgramService programService;

  public void save(Product product) {
    validateAndSetProductCategory(product);

    if (product.getId() == null) {
      repository.insert(product);
      return;
    }

    setReferenceDataForProduct(product);
    List<ProgramProduct> existingProgramProducts = programProductService.getByProductCode(product.getCode());

    repository.update(product);

    notifyProgramCatalogChange(product, existingProgramProducts);
  }

  private void notifyProgramCatalogChange(Product product, List<ProgramProduct> existingProgramProducts) {
    for (ProgramProduct existingProgramProduct : existingProgramProducts) {
      if (existingProgramProduct.isActive() && (existingProgramProduct.getProduct().getActive() != product.getActive())) {
        programService.setFeedSendFlag(existingProgramProduct.getProgram(), true);
      }
    }
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
      if (productGroup == null) throw new DataException("error.reference.data.invalid.product.group");
      product.getProductGroup().setId(productGroup.getId());
    }

    // set from reference information for the online form... that returns it using the id columns
    if(product.getForm() == null && product.getFormId() != null){
      product.setForm(new ProductForm());
      product.getForm().setId(product.getFormId());
    }

    if(product.getDosageUnit() == null && product.getDosageUnitId() != null){
      product.setDosageUnit(new DosageUnit());
      product.getDosageUnit().setId(product.getDosageUnitId());
    }

    if(product.getProductGroup() == null && product.getProductGroupId() != null){
      product.setProductGroup(new ProductGroup());
      product.getProductGroup().setId(product.getProductGroupId());
    }

    if(product.getCategory() == null && product.getCategoryId() != null){
      product.setCategory(new ProductCategory());
      product.getCategory().setId(product.getFormId());
    }

  }


  private void validateAndSetProductCategory(Product product) {
    ProductCategory category = product.getCategory();
    if (category == null) return;
    String categoryCode = category.getCode();
    if (categoryCode == null || categoryCode.isEmpty()) return;
    Long categoryId = categoryService.getProductCategoryIdByCode(category.getCode());
    if (categoryId == null) {
      throw new DataException("error.reference.data.invalid.product");
    }
    category.setId(categoryId);
  }

  public Long getIdForCode(String code) {
    return repository.getIdByCode(code);
  }

  public Product getByCode(String code) {
    return repository.getByCode(code);
  }

  public boolean isActive(String code) {
    return repository.isActive(code);
  }
}
