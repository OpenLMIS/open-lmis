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

import org.apache.commons.collections.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertTrue;
import static org.apache.commons.collections.CollectionUtils.exists;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProductBuilder.*;
import static org.openlmis.core.builder.ProgramBuilder.*;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ProgramProductMapperIT {

  @Autowired
  private ProgramMapper programMapper;

  @Autowired
  private ProductMapper productMapper;

  @Autowired
  private ProgramProductMapper programProductMapper;

  @Autowired
  private ProgramProductIsaMapper programProductISAMapper;

  @Autowired
  private FacilityApprovedProductMapper facilityApprovedProductMapper;

  @Autowired
  private ProductCategoryMapper productCategoryMapper;

  @Autowired
  QueryExecutor executor;

  private Product product;
  private Program program;
  private ProductCategory productCategory;

  @Before
  public void setup() {
    product = make(a(ProductBuilder.defaultProduct));
    productCategory = new ProductCategory("10", "P1", 1);
    productCategoryMapper.insert(productCategory);

    productMapper.insert(product);
    program = make(a(defaultProgram));
    programMapper.insert(program);
  }

  @Test
  public void shouldInsertProductForAProgram() throws Exception {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProduct.setProductCategory(productCategory);
    programProduct.setDisplayOrder(1);
    programProductMapper.insert(programProduct);
    ResultSet resultSet = executor.execute("SELECT * FROM program_products");
    resultSet.next();
    assertThat(programProduct.getId(), is(notNullValue()));
    assertThat(resultSet.getLong("productCategoryId"), is(productCategory.getId()));
    assertThat(resultSet.getInt("displayOrder"), is(1));
  }

  @Test
  public void shouldGetProgramProductIdByProgramIdAndProductId() throws Exception {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProduct.setProductCategory(productCategory);
    programProductMapper.insert(programProduct);

    Long id = programProductMapper.getIdByProgramAndProductId(program.getId(), product.getId());

    assertThat(id, is(programProduct.getId()));
  }

  @Test
  public void shouldGetProgramProductByProgramIdAndProductId() throws Exception {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProduct.setProductCategory(productCategory);
    programProductMapper.insert(programProduct);

    ProgramProduct result = programProductMapper.getByProgramAndProductId(program.getId(), product.getId());

    assertThat(result.getId(), is(programProduct.getId()));
  }

  @Test
  public void shouldUpdateCurrentPriceForProgramProduct() throws Exception {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true, new Money("100.0"));
    programProduct.setModifiedBy(1L);
    programProduct.setModifiedDate(new Date());
    programProduct.setProductCategory(productCategory);
    programProductMapper.insert(programProduct);
    Money price = new Money("200.01");
    programProduct.setCurrentPrice(price);

    programProductMapper.updateCurrentPrice(programProduct);

    ProgramProduct returnedProgramProduct = programProductMapper.getByProgramAndProductId(program.getId(),
      product.getId());
    assertThat(returnedProgramProduct.getCurrentPrice(), is(price));
    assertThat(returnedProgramProduct.getModifiedBy(), is(1L));
    assertThat(returnedProgramProduct.getModifiedDate(), is(notNullValue()));
  }

  @Test
  public void shouldUpdateProgramProduct() throws Exception {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProduct.setProductCategory(productCategory);
    programProductMapper.insert(programProduct);
    programProduct.setDosesPerMonth(10);
    programProduct.setActive(false);
    programProduct.setProductCategory(productCategory);

    programProductMapper.update(programProduct);

    ProgramProduct dbProgramProduct = programProductMapper.getByProgramAndProductId(program.getId(), product.getId());

    assertThat(dbProgramProduct.getDosesPerMonth(), is(10));
    assertThat(dbProgramProduct.getActive(), is(false));
    assertThat(dbProgramProduct.getProductCategory(), is(productCategory));
  }

  @Test
  public void shouldGetProgramProductsByProgramInOrderOfDisplayOrderAndProductCode() {
    ProgramProduct programProduct1 = new ProgramProduct(program, product, 10, true);
    programProduct1.setProductCategory(productCategory);
    programProduct1.setDisplayOrder(3);
    programProductMapper.insert(programProduct1);

    Product product1 = make(a(defaultProduct, with(code, "Product 1")));
    productMapper.insert(product1);

    Product product2 = make(a(defaultProduct, with(code, "Product 2")));
    productMapper.insert(product2);
    ProgramProduct programProduct2 = new ProgramProduct(program, product1, 10, true);
    programProduct2.setProductCategory(productCategory);
    programProduct2.setDisplayOrder(1);
    programProductMapper.insert(programProduct2);

    ProgramProduct programProduct3 = new ProgramProduct(program, product2, 10, true);
    programProduct3.setProductCategory(productCategory);
    programProduct3.setDisplayOrder(2);
    programProductMapper.insert(programProduct3);

    ISA isa = new ISA(1d, 2, 3.3, 5.6, 4, 5, 5, null);
    ProgramProductISA programProductISA = new ProgramProductISA(programProduct1.getId(), isa);

    programProductISAMapper.insert(programProductISA);

    List<ProgramProduct> programProducts = programProductMapper.getByProgram(program);

    assertThat(programProducts.size(), is(3));
    assertThat(programProducts.get(0).getId(), is(programProduct2.getId()));
    assertThat(programProducts.get(0).getDisplayOrder(), is(programProduct2.getDisplayOrder()));
    assertThat(programProducts.get(0).getProductCategory(), is(productCategory));
    assertThat(programProducts.get(1).getId(), is(programProduct3.getId()));
    assertThat(programProducts.get(1).getProductCategory(), is(productCategory));
    assertThat(programProducts.get(2).getId(), is(programProduct1.getId()));
    assertThat(programProducts.get(2).getProgramProductIsa(), is(programProductISA));
    assertThat(programProducts.get(2).getProductCategory(), is(productCategory));
  }

  @Test
  public void shouldGetNonFullSuppProgramProductsByProgram() {
    Product product1 = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, "P2"), with(fullSupply, false)));
    productMapper.insert(product1);

    ProgramProduct programProduct = new ProgramProduct(program, product1, 10, true);
    programProduct.setProductCategory(productCategory);
    programProductMapper.insert(programProduct);

    List<ProgramProduct> programProducts = programProductMapper.getNonFullSupplyProductsForProgram(program);

    assertThat(programProducts.size(), is(1));
    assertThat(programProducts.get(0).getId(), is(programProduct.getId()));
  }

  @Test
  public void shouldGetById() throws Exception {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProduct.setProductCategory(productCategory);
    programProductMapper.insert(programProduct);

    ProgramProduct savedProgramProduct = programProductMapper.getById(programProduct.getId());

    assertThat(savedProgramProduct.getId(), is(programProduct.getId()));
  }

  @Test
  public void shouldGetByProductCode() throws Exception {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProduct.setProductCategory(productCategory);
    Program hiv = new Program(1L);
    hiv.setCode("HIV");
    hiv.setName("HIV");

    ProgramProduct programProduct2 = new ProgramProduct(hiv, product, 10, true);
    programProduct2.setProductCategory(productCategory);
    programProductMapper.insert(programProduct);
    programProductMapper.insert(programProduct2);

    List<ProgramProduct> returnedProducts = programProductMapper.getByProductCode(
      programProduct.getProduct().getCode());

    assertThat(returnedProducts.size(), is(2));
    assertContainsProgramProduct(returnedProducts, programProduct);
    assertContainsProgramProduct(returnedProducts, programProduct2);
  }

  @Test
  public void shouldGetByProgramProductIdAndFacilityTypeCode() throws Exception {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProduct.setProductCategory(productCategory);
    programProductMapper.insert(programProduct);
    String facilityTypeCode = "warehouse";

    FacilityTypeApprovedProduct facilityTypeApprovedProduct = new FacilityTypeApprovedProduct();

    FacilityType facilityType = new FacilityType(facilityTypeCode);
    facilityType.setId(1L);

    facilityTypeApprovedProduct.setFacilityType(facilityType);
    facilityTypeApprovedProduct.setProgramProduct(programProduct);
    facilityTypeApprovedProduct.setMaxMonthsOfStock(33.22);

    facilityApprovedProductMapper.insert(facilityTypeApprovedProduct);

    List<ProgramProduct> returnedProducts = programProductMapper.getByProgramIdAndFacilityTypeCode(program.getId(),
      facilityTypeCode);

    assertThat(returnedProducts.size(), is(1));
    assertContainsProgramProduct(returnedProducts, programProduct);
    assertThat(returnedProducts.get(0).getProductCategory(), is(productCategory));
  }

  @Test
  public void shouldGetAllProgramProductsForProgramIdWhenFacilityTypeCodeIsNull() throws Exception {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProduct.setProductCategory(productCategory);
    Product product1 = make(a(ProductBuilder.defaultProduct, with(code, "P1")));
    productMapper.insert(product1);

    ProgramProduct programProduct2 = new ProgramProduct(program, product1, 10, true);
    programProduct2.setProductCategory(productCategory);
    programProductMapper.insert(programProduct);
    programProductMapper.insert(programProduct2);

    List<ProgramProduct> returnedProducts = programProductMapper.getByProgramIdAndFacilityTypeCode(program.getId(), null);

    assertThat(returnedProducts.size(), is(2));
    assertContainsProgramProduct(returnedProducts, programProduct);
    assertContainsProgramProduct(returnedProducts, programProduct2);
  }

  @Test
  public void shouldGetTotalResultCount() {
    String searchParam = "Yell";
    Program program2 = make(a(defaultProgram, with(programName, "TB"), with(programCode, "anshul")));
    programMapper.insert(program2);

    ProgramProduct programProduct1 = new ProgramProduct(program, product, 10, true);
    programProduct1.setProductCategory(productCategory);
    ProgramProduct programProduct2 = new ProgramProduct(program2, product, 10, true);
    programProduct2.setProductCategory(productCategory);

    programProductMapper.insert(programProduct1);
    programProductMapper.insert(programProduct2);

    assertThat(programProductMapper.getTotalSearchResultCount(searchParam), is(1));
  }

  @Test
  public void shouldSearchProgramProductByProduct() {
    Pagination pagination = new Pagination(1, 10);
    Product prod1 = make(a(defaultProduct, with(primaryName, "prod1"), with(code, "p1")));
    Product prod2 = make(a(defaultProduct, with(primaryName, "prod2"), with(code, "p2")));
    Product prod3 = make(a(defaultProduct, with(primaryName, "new"), with(code, "p3")));
    Product prod4 = make(a(defaultProduct, with(primaryName, "prod4"), with(code, "p4")));
    productMapper.insert(prod1);
    productMapper.insert(prod2);
    productMapper.insert(prod3);
    productMapper.insert(prod4);

    Program program1 = make(a(defaultProgram, with(programName, "Essnl Mdn"), with(programCode, "prog1")));
    Program program2 = make(a(defaultProgram, with(programName, "TB"), with(programCode, "prog2")));
    programMapper.insert(program1);
    programMapper.insert(program2);

    ProductCategory productCategory1 = new ProductCategory("C1", "Anaesthetics", 1);
    ProductCategory productCategory2 = new ProductCategory("C2", "Antibiotics", 1);
    productCategoryMapper.insert(productCategory1);
    productCategoryMapper.insert(productCategory2);

    ProgramProduct programProduct1 = new ProgramProduct(program1, prod1, 10, true);
    programProduct1.setProductCategory(productCategory1);
    programProductMapper.insert(programProduct1);

    ProgramProduct programProduct2 = new ProgramProduct(program1, prod2, 10, true);
    programProduct2.setProductCategory(productCategory1);
    programProductMapper.insert(programProduct2);

    ProgramProduct programProduct3 = new ProgramProduct(program2, prod2, 10, true);
    programProduct3.setProductCategory(productCategory2);
    programProductMapper.insert(programProduct3);

    ProgramProduct programProduct4 = new ProgramProduct(program2, prod3, 10, true);
    programProduct4.setProductCategory(productCategory2);
    programProductMapper.insert(programProduct4);

    List<ProgramProduct> programProducts = programProductMapper.searchByProduct("prod", pagination);

    assertThat(programProducts.size(), is(4));
    assertThat(programProducts.get(0).getProduct().getCode(), is(programProduct1.getProduct().getCode()));
    assertThat(programProducts.get(1).getProduct().getCode(), is(programProduct2.getProduct().getCode()));
    assertThat(programProducts.get(2).getProduct().getCode(), is(programProduct3.getProduct().getCode()));
    assertThat(programProducts.get(3).getProduct().getCode(), is(prod4.getCode()));
  }

  @Test
  public void shouldSearchProgramProductByProgram() {
    Pagination pagination = new Pagination(1, 3);
    Product prod1 = make(a(defaultProduct, with(primaryName, "prod1"), with(code, "p1")));
    Product prod2 = make(a(defaultProduct, with(primaryName, "prod2"), with(code, "p2")));
    Product prod3 = make(a(defaultProduct, with(primaryName, "new"), with(code, "p3")));
    Product prod4 = make(a(defaultProduct, with(primaryName, "prod4"), with(code, "p4")));
    productMapper.insert(prod1);
    productMapper.insert(prod2);
    productMapper.insert(prod3);
    productMapper.insert(prod4);

    Program program1 = make(a(defaultProgram, with(programName, "TB1"), with(programCode, "prog1")));
    Program program2 = make(a(defaultProgram, with(programName, "TB2"), with(programCode, "prog2")));
    programMapper.insert(program1);
    programMapper.insert(program2);

    ProductCategory productCategory1 = new ProductCategory("C1", "Anaesthetics", 1);
    ProductCategory productCategory2 = new ProductCategory("C2", "Antibiotics", 1);
    productCategoryMapper.insert(productCategory1);
    productCategoryMapper.insert(productCategory2);

    ProgramProduct programProduct1 = new ProgramProduct(program2, prod1, 10, true);
    programProduct1.setProductCategory(productCategory1);
    programProductMapper.insert(programProduct1);

    ProgramProduct programProduct2 = new ProgramProduct(program1, prod2, 10, true);
    programProduct2.setProductCategory(productCategory1);
    programProductMapper.insert(programProduct2);

    ProgramProduct programProduct3 = new ProgramProduct(program2, prod2, 10, true);
    programProduct3.setProductCategory(productCategory1);
    programProductMapper.insert(programProduct3);

    ProgramProduct programProduct4 = new ProgramProduct(program1, prod3, 10, true);
    programProduct4.setProductCategory(productCategory2);
    programProductMapper.insert(programProduct4);

    List<ProgramProduct> programProducts = programProductMapper.searchByProgram("tb", pagination);

    assertThat(programProducts.size(), is(3));
    assertThat(programProducts.get(0).getProduct().getCode(), is(programProduct2.getProduct().getCode()));
    assertThat(programProducts.get(1).getProduct().getCode(), is(programProduct4.getProduct().getCode()));
    assertThat(programProducts.get(2).getProduct().getCode(), is(programProduct1.getProduct().getCode()));
  }

  private void assertContainsProgramProduct(List<ProgramProduct> returnedProducts, final ProgramProduct programProduct) {
    boolean exists = exists(returnedProducts, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        ProgramProduct productFromCollection = (ProgramProduct) o;
        return (productFromCollection.getProgram().getCode().equals(programProduct.getProgram().getCode())) &&
          (productFromCollection.getProgram().getName().equals(programProduct.getProgram().getName())) &&
          (productFromCollection.getProductCategory().getCode().equals(programProduct.getProductCategory().getCode())) &&
          (productFromCollection.getProductCategory().getName().equals(programProduct.getProductCategory().getName())) &&
          (productFromCollection.getProductCategory().getDisplayOrder().equals(programProduct.getProductCategory().getDisplayOrder())) &&
          (productFromCollection.getActive() == programProduct.getActive()) &&
          (productFromCollection.getProduct().getActive() == programProduct.getProduct().getActive());
      }
    });

    assertTrue(exists);
  }
}
