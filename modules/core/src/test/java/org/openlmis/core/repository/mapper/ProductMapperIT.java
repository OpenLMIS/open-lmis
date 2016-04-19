/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;


import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.KitProductBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.DosageUnit;
import org.openlmis.core.domain.KitProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.utils.DateUtil;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.openlmis.core.builder.ProductBuilder.*;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ProductMapperIT {

  public static final String PRODUCT_DOSAGE_UNIT_MG = "mg";

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Autowired
  ProgramProductMapper programProductMapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  ProgramSupportedMapper programSupportedMapper;

  @Autowired
  ProductGroupMapper productGroupMapper;

  @Autowired
  ProductMapper productMapper;

  @Autowired
  ProductCategoryMapper productCategoryMapper;

  @Autowired
  private QueryExecutor queryExecutor;

  @Test
  public void shouldNotSaveProductWithoutMandatoryFields() throws Exception {
    expectedEx.expect(DataIntegrityViolationException.class);
    expectedEx.expectMessage("null value in column \"primaryname\" violates not-null constraint");
    Product product = new Product();
    product.setCode("ABCD123");
    Long status = productMapper.insert(product);
    Long expected = 0L;
    assertEquals( expected , status);
  }

  @Test
  public void shouldReturnDosageUnitByCode() {
    DosageUnit dosageUnit = productMapper.getDosageUnitByCode(PRODUCT_DOSAGE_UNIT_MG);

    assertThat(dosageUnit.getCode(), is("mg"));
    assertThat(dosageUnit.getDisplayOrder(), is(1));
  }

  @Test
  public void shouldReturnNullForInvalidProductCode() {
    String code = "invalid_code";
    Long productId = productMapper.getIdByCode(code);
    assertThat(productId, is(nullValue()));
  }

  @Test
  public void shouldReturnProductIdForValidProductCode() {
    Product product = make(a(defaultProduct));
    productMapper.insert(product);
    Long id = productMapper.getIdByCode(product.getCode());
    assertThat(id, is(product.getId()));
  }

  @Test
  public void shouldReturnProductByCode() {
    Product product = make(a(defaultProduct));
    productMapper.insert(product);
    Product expectedProduct = productMapper.getByCode(product.getCode());
    assertThat(expectedProduct.getId(), is(product.getId()));
    assertThat(expectedProduct.getCode(), is(product.getCode()));
    assertThat(expectedProduct.getPrimaryName(), is(product.getPrimaryName()));
    assertThat(expectedProduct.getForm().getCode(), is("Tablet"));
    assertThat(expectedProduct.getDosageUnit().getCode(), is("mg"));
    assertThat(expectedProduct.getModifiedDate(), is(not(nullValue())));
  }

  @Test
  public void shouldUpdateProduct() {
    Product product = make(a(defaultProduct));
    productMapper.insert(product);

    product.setCode("Product Code Updated");
    product.setPrimaryName("Updated Name");
    product.setAlternateItemCode("Alternate Code");

    productMapper.update(product);

    Product returnedProduct = productMapper.getByCode(product.getCode());

    assertThat(returnedProduct.getCode(), is("Product Code Updated"));
    assertThat(returnedProduct.getPrimaryName(), is("Updated Name"));
    assertThat(returnedProduct.getAlternateItemCode(), is("Alternate Code"));
    assertThat(returnedProduct.getModifiedDate(), is(not(nullValue())));
  }

  @Test
  public void shouldGetAProductById() {
    Product product = make(a(defaultProduct));

    productMapper.insert(product);

    Product returnedProduct = productMapper.getById(product.getId());

    assertThat(returnedProduct.getId(), is(product.getId()));
    assertThat(returnedProduct.getCode(), is(product.getCode()));
    assertThat(returnedProduct.getProductGroup(), is(product.getProductGroup()));
    assertThat(returnedProduct.getPrimaryName(), is(product.getPrimaryName()));
    assertThat(returnedProduct.getDosageUnit(), is(product.getDosageUnit()));
    assertThat(returnedProduct.getForm(), is(product.getForm()));
  }

  @Test
  public void shouldReturnFalseIfProductInactive() throws Exception {
    Product product = make(a(defaultProduct, with(active, false)));

    productMapper.insert(product);

    assertFalse(productMapper.isActive("p999"));
  }

  @Test
  public void shouldReturnTrueIfProductActive() throws Exception {
    Product product = make(a(defaultProduct, with(active, true)));

    productMapper.insert(product);

    assertTrue(productMapper.isActive(product.getCode()));
  }

  @Test
  public void shouldGetLWProduct() throws Exception {
    Product product1 = make(a(defaultProduct, with(active, true), with(code, "Prod1")));
    Product product2 = make(a(defaultProduct, with(active, true), with(code, "code2")));
    Product product3 = make(a(defaultProduct, with(active, true), with(code, "Prod3")));

    productMapper.insert(product1);
    productMapper.insert(product2);
    productMapper.insert(product3);

    Product product = productMapper.getLWProduct(product1.getId());

    assertThat(product.getCode(), is("Prod1"));
    assertThat(product.getDosageUnit().getCode(), is("mg"));
  }

  @Test
  public void shouldGetTotalSearchResultCount() {
    String searchParam = "prod";
    Product prod1 = make(a(defaultProduct, with(primaryName, "prod1"), with(code, "pro")));
    Product prod2 = make(a(defaultProduct, with(primaryName, "prod2"), with(code, "p1")));
    Product prod3 = make(a(defaultProduct, with(primaryName, "prod3"), with(code, "p2")));
    Product prod4 = make(a(defaultProduct, with(primaryName, "diff pro"), with(code, "p3")));
    productMapper.insert(prod1);
    productMapper.insert(prod2);
    productMapper.insert(prod3);
    productMapper.insert(prod4);

    Integer resultCount = productMapper.getTotalSearchResultCount(searchParam);

    assertThat(resultCount, is(3));
  }

  @Test
  public void shouldInsertAndDeleteKitProduct() throws SQLException {
    Product product = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, "KIT")));
    productMapper.insert(product);

    Product product1 = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, "P1")));
    productMapper.insert(product1);

    KitProduct kitProduct1 = make(a(KitProductBuilder.defaultKit,
        with(KitProductBuilder.kitCode, "KIT"),
        with(KitProductBuilder.productCode, "P1"),
        with(KitProductBuilder.quantity, 100)));

    productMapper.insertKitProduct(kitProduct1);

    List<Product> productList = productMapper.list();

    assertThat(productList.size(), is(2));
    assertThat(productList.get(0).getCode(), is("KIT"));
    assertThat(productList.get(0).getPrimaryName(), is("Primary Name"));
    assertThat(productList.get(1).getCode(), is("P1"));
    assertThat(productList.get(0).getKitProductList().size(), is(1));
    assertThat(productList.get(1).getKitProductList().size(), is(0));

    productMapper.deleteKitProduct(kitProduct1);

    Product kit = productMapper.getByCode("KIT");
    assertTrue(kit.getKitProductList().isEmpty());
  }

  @Test
  public void shouldGetAllProductsWithFormAndDosageUnit() {
    Product product = make(a(defaultProduct));
    productMapper.insert(product);

    List<Product> products = productMapper.list();
    assertNotNull(products.get(0).getForm());
    assertNotNull(products.get(0).getDosageUnit());
  }

  @Test
  public void shouldListProductsAfterUpdatedTime() throws SQLException {
    Product product = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, "KIT")));
    productMapper.insert(product);

    Product product1 = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, "P1")));
    productMapper.insert(product1);

    KitProduct kitProduct1 = make(a(KitProductBuilder.defaultKit,
        with(KitProductBuilder.kitCode, "KIT"),
        with(KitProductBuilder.productCode, "P1"),
        with(KitProductBuilder.quantity, 100)));

    Timestamp date1 = new Timestamp(DateUtil.parseDate("2025-12-12 12:12:12").getTime());
    updateModifiedDateForProducts(date1, product.getId());

    productMapper.insertKitProduct(kitProduct1);

    List<Product> productList = productMapper.listProductsAfterUpdatedTime(new Date());

    assertThat(productList.size(), is(1));
    assertThat(productList.get(0).getCode(), is("KIT"));
    assertThat(productList.get(0).getKitProductList().size(), is(1));
  }

  @Test
  public void shouldUpdateProductStatus(){
    Product prod = make(a(defaultProduct, with(primaryName, "prod"), with(code, "pro")));
      Date modifiedDate = DateUtil.parseDate("2015-10-10 12:00:00", DateUtil.FORMAT_DATE_TIME);
      prod.setModifiedDate(modifiedDate);
    prod.setActive(false);
    productMapper.insert(prod);

    productMapper.updateProductActiveStatus(true,prod.getId());

    Product product = productMapper.getById(prod.getId());
    assertThat(product.getActive(),is(true));
    assertNotEquals(DateUtil.formatDate(product.getModifiedDate()), is(DateUtil.formatDate(modifiedDate)));
  }

  private void updateModifiedDateForProducts(Timestamp modifiedDate, Long productId) throws SQLException {
    queryExecutor.executeUpdate("UPDATE products SET modifieddate = ? WHERE id = ?", modifiedDate, productId);
  }
}
