package org.openlmis.core.upload;

import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.repository.ProductCategoryRepository;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("productCategoryPersistenceHandler")
public class ProductCategoryPersistenceHandler  extends AbstractModelPersistenceHandler {


  ProductCategoryRepository productCategoryRepository;

  @Autowired
  public ProductCategoryPersistenceHandler(ProductCategoryRepository productCategoryRepository) {
    this.productCategoryRepository = productCategoryRepository;
  }

  @Override
  protected void save(Importable importable, AuditFields auditFields) {
    ProductCategory productCategory = (ProductCategory) importable;
    productCategory.setModifiedBy(auditFields.getUser());
    productCategory.setModifiedDate(new Date());
    productCategoryRepository.save(productCategory);

  }
}
