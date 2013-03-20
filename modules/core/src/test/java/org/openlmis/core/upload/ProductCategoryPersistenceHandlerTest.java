package org.openlmis.core.upload;

import org.junit.Test;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.repository.ProductCategoryRepository;
import org.openlmis.upload.model.AuditFields;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ProductCategoryPersistenceHandlerTest {



  @Test
  public void shouldSaveImportedProductCategory() throws Exception {
    ProductCategoryRepository productCategoryRepository = mock(ProductCategoryRepository.class);
    ProductCategory productCategory = new ProductCategory();

    new ProductCategoryPersistenceHandler(productCategoryRepository).execute(productCategory, 0, new AuditFields(1, null));
    assertThat(productCategory.getModifiedBy(), is(1));
    assertThat(productCategory.getModifiedDate(), is(notNullValue()));
    verify(productCategoryRepository).save(productCategory);
  }
}

