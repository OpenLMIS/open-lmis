package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ProductCategoryMapperIT {

  @Autowired
  ProductCategoryMapper productCategoryMapper;

  @Test
  public void shouldInsertProductCategoryCode() {
    ProductCategory productCategory = new ProductCategory();
    productCategory.setCode("category code");
    productCategory.setName("category name");
    productCategoryMapper.insert(productCategory);

    ProductCategory returnedProductCategory = productCategoryMapper.getProductCategoryById(productCategory.getId());

    assertThat(returnedProductCategory.getId(), is(productCategory.getId()));
    assertThat(returnedProductCategory.getCode(), is(productCategory.getCode()));
    assertThat(returnedProductCategory.getName(), is(productCategory.getName()));
  }

  @Test
  public void shouldGetProductCategoryByCode() {
    ProductCategory productCategory = new ProductCategory();
    productCategory.setCode("category code");
    productCategory.setName("category name");
    productCategoryMapper.insert(productCategory);

    ProductCategory returnedProductCategory = productCategoryMapper.getProductCategoryByCode(productCategory.getCode());

    assertThat(returnedProductCategory.getId(), is(productCategory.getId()));
    assertThat(returnedProductCategory.getName(), is(productCategory.getName()));
  }

  @Test
  public void shouldUpdateProductCategory() {
    ProductCategory productCategory = new ProductCategory();
    productCategory.setCode("category code");
    productCategory.setName("category name");
    productCategory.setModifiedBy("user1");
    productCategoryMapper.insert(productCategory);

    productCategory.setName("updated category name");
    productCategory.setModifiedBy("user2");
    productCategoryMapper.update(productCategory);
    ProductCategory returnedProductCategory = productCategoryMapper.getProductCategoryByCode(productCategory.getCode());

    assertThat(returnedProductCategory.getName(), is(productCategory.getName()));
    assertThat(returnedProductCategory.getModifiedBy(), is(productCategory.getModifiedBy()));
  }

}


