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

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.FACILITY_TYPE_CODE;
import static org.openlmis.core.builder.FacilityBuilder.FACILITY_TYPE_ID;
import static org.openlmis.core.builder.ProductBuilder.*;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
@Category(IntegrationTests.class)
public class FacilityApprovedProductMapperIT {

  public static final Double MAX_MONTHS_OF_STOCK = 3.0;

  @Autowired
  ProductMapper productMapper;

  @Autowired
  ProgramProductMapper programProductMapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  FacilityApprovedProductMapper mapper;

  @Autowired
  private ProgramMapper programMapper;

  @Autowired
  private ProductCategoryMapper productCategoryMapper;

  private ProductCategory category1;

  @Before
  public void setUp() throws Exception {
    category1 = category("C1", "Category 1", 2);
  }

  @Test
  public void shouldInsertFacilityApprovedProduct() {
    Program program = make(a(ProgramBuilder.defaultProgram));
    Product product = make(a(ProductBuilder.defaultProduct));
    programMapper.insert(program);
    productMapper.insert(product);

    ProgramProduct programProduct = new ProgramProduct(program, product, 30, true);
    programProduct.setProductCategory(category1);
    programProductMapper.insert(programProduct);

    FacilityType facilityType = new FacilityType();
    facilityType.setId(FACILITY_TYPE_ID);
    FacilityTypeApprovedProduct facilityTypeApprovedProduct = new FacilityTypeApprovedProduct(facilityType, programProduct, MAX_MONTHS_OF_STOCK);
    facilityTypeApprovedProduct.setMinMonthsOfStock(3.45);
    facilityTypeApprovedProduct.setEop(8.45);
    int insertionCount = mapper.insert(facilityTypeApprovedProduct);

    assertThat(facilityTypeApprovedProduct.getId(), is(notNullValue()));
    assertThat(insertionCount, is(1));
  }

  @Test
  public void shouldUpdateFacilityApprovedProduct() {
    Program program = make(a(ProgramBuilder.defaultProgram));
    Product product = make(a(ProductBuilder.defaultProduct));
    programMapper.insert(program);
    productMapper.insert(product);

    ProgramProduct programProduct = new ProgramProduct(program, product, 30, true);
    programProduct.setProductCategory(category1);
    programProductMapper.insert(programProduct);

    FacilityType facilityType = new FacilityType();
    facilityType.setId(FACILITY_TYPE_ID);
    FacilityTypeApprovedProduct facilityTypeApprovedProduct = new FacilityTypeApprovedProduct(facilityType, programProduct, MAX_MONTHS_OF_STOCK);
    facilityTypeApprovedProduct.setMaxMonthsOfStock(3.45);
    facilityTypeApprovedProduct.setEop(8.45);
    mapper.insert(facilityTypeApprovedProduct);
    facilityTypeApprovedProduct = mapper.getBy(programProduct.getId(), "warehouse");

    facilityTypeApprovedProduct.setFacilityType(facilityType);
    facilityTypeApprovedProduct.setProgramProduct(programProduct);
    facilityTypeApprovedProduct.setMaxMonthsOfStock(MAX_MONTHS_OF_STOCK);
    facilityTypeApprovedProduct.setMinMonthsOfStock(20.98);
    facilityTypeApprovedProduct.setEop(19.85);
    facilityTypeApprovedProduct.setModifiedDate(null);
    mapper.update(facilityTypeApprovedProduct);
    FacilityTypeApprovedProduct result = mapper.getBy(programProduct.getId(), "warehouse");

    assertThat(result.getMinMonthsOfStock(), is(20.98));
    assertThat(result.getEop(), is(19.85));
    assertThat(result.getModifiedDate(), is(notNullValue()));

  }

  @Test
  public void shouldGetActiveProductsByFacilityAndProgramInOrderOfDisplayAndProductCode() {
    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);
    Program yellowFeverProgram = make(a(defaultProgram));
    Program bpProgram = make(a(defaultProgram, with(programCode, "BP")));

    programMapper.insert(bpProgram);
    programMapper.insert(yellowFeverProgram);

    ProductCategory category2 = category("C2", "Category 2", 7);
    ProductCategory category3 = category("C3", "Category 3", 4);
    ProductCategory category4 = category("C4", "Category 4", 5);
    ProductCategory category6 = category("C6", "Category 6", 1);

    Product pro01 = product("PRO01", "Primary Name", true);
    Product pro03 = product("PRO03", "Primary Name", false);
    Product pro04 = product("PRO04", "Primary Name", true);
    Product pro05 = product("PRO05", "Primary Name", true);
    Product pro06 = product("PRO06", "Primary Name", true);
    Product pro07 = product("PRO07", "Primary Name", true);

    ProgramProduct programProduct1 = addToProgramProduct(yellowFeverProgram, pro01, true, category1, 6, true);
    ProgramProduct programProduct3 = addToProgramProduct(yellowFeverProgram, pro03, true, category3, 1, false);
    ProgramProduct programProduct4 = addToProgramProduct(yellowFeverProgram, pro04, false, category4, 2, true);
    ProgramProduct programProduct5 = addToProgramProduct(yellowFeverProgram, pro05, true, category1, 5, true);
    ProgramProduct programProduct6 = addToProgramProduct(yellowFeverProgram, pro06, true, category6, 5, true);
    ProgramProduct programProduct7 = addToProgramProduct(bpProgram, pro07, true, category2, null, true);

    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct1);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct3);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct4);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct5);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct6);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct7);

    List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts = mapper.getFullSupplyProductsBy(facility.getId(), yellowFeverProgram.getId());

    assertEquals(3, facilityTypeApprovedProducts.size());

    FacilityTypeApprovedProduct facilityTypeApprovedProduct = facilityTypeApprovedProducts.get(0);
    Product product = facilityTypeApprovedProduct.getProgramProduct().getProduct();

    assertEquals(programProduct6.getId(), facilityTypeApprovedProduct.getProgramProduct().getId());
    assertEquals(30, facilityTypeApprovedProduct.getProgramProduct().getDosesPerMonth().intValue());
    assertEquals("PRO06", product.getCode());
    assertEquals("Primary Name", product.getPrimaryName());
    assertEquals("strength", product.getStrength());
    assertThat(product.getForm().getCode(), Is.is("Tablet"));
    assertEquals("Strip", product.getDispensingUnit());
    assertThat(product.getDosageUnit().getCode(), Is.is("mg"));
    assertNotNull(product.getForm());
    assertEquals("Tablet", product.getForm().getCode());
    assertNotNull(product.getDosageUnit());
    assertThat(facilityTypeApprovedProduct.getProgramProduct().getProductCategory().getName(), is("Category 6"));
    assertEquals("mg", product.getDosageUnit().getCode());
    assertEquals(10, product.getDosesPerDispensingUnit().intValue());

    assertEquals("PRO05", facilityTypeApprovedProducts.get(1).getProgramProduct().getProduct().getCode());
    assertEquals("PRO01", facilityTypeApprovedProducts.get(2).getProgramProduct().getProduct().getCode());

    List<FacilityTypeApprovedProduct> nonFullSupplyFacilityTypeApprovedProducts = mapper.getNonFullSupplyProductsBy(facility.getId(), yellowFeverProgram.getId());

    assertThat(nonFullSupplyFacilityTypeApprovedProducts.size(), is(1));
    assertThat(nonFullSupplyFacilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getCode(), is("PRO03"));
    assertThat(nonFullSupplyFacilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getManufacturer(), is(nullValue()));
    assertThat(nonFullSupplyFacilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getFlammable(), is(nullValue()));
  }

  @Test
  public void shouldGetFacilityApprovedProductId() {
    Program program = make(a(defaultProgram));
    Product product = make(a(defaultProduct));
    productMapper.insert(product);

    ProgramProduct programProduct = addToProgramProduct(program, product, true, category1, null, true);

    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct);

    FacilityTypeApprovedProduct facilityTypeApprovedProductsFromDB = mapper.getBy(programProduct.getId(), "warehouse");

    assertNotNull(facilityTypeApprovedProductsFromDB);
    assertEquals(facilityTypeApprovedProductsFromDB.getMaxMonthsOfStock(), MAX_MONTHS_OF_STOCK);
  }

  @Test
  public void shouldGetPaginatedFacilityApprovedProductsOnlyByProgramIdAndFacilityTypeId() {
    String searchParam = "";
    Program yellowFeverProgram = make(a(defaultProgram));
    Program bpProgram = make(a(defaultProgram, with(programCode, "BP")));

    programMapper.insert(bpProgram);
    programMapper.insert(yellowFeverProgram);

    ProductCategory category2 = category("C2", "Category 2", 7);
    ProductCategory category3 = category("C3", "Category 3", 4);
    ProductCategory category4 = category("C4", "Category 4", 5);

    Product pro01 = product("PRO01", "Primary Name", true);
    Product pro03 = product("PRO03", "Primary Name", false);
    Product pro04 = product("PRO04", "Primary Name", true);
    Product pro05 = product("PRO05", "Primary Name", true);
    Product pro06 = product("aPRO06", "Primary Name", true);
    Product pro07 = product("PRO07", "Primary Name", true);

    ProgramProduct programProduct1 = addToProgramProduct(yellowFeverProgram, pro01, true, category1, 6, true);
    ProgramProduct programProduct3 = addToProgramProduct(yellowFeverProgram, pro03, true, category3, 1, false);
    ProgramProduct programProduct4 = addToProgramProduct(yellowFeverProgram, pro04, false, category4, 2, true);
    ProgramProduct programProduct5 = addToProgramProduct(yellowFeverProgram, pro05, true, category1, 5, true);
    ProgramProduct programProduct6 = addToProgramProduct(yellowFeverProgram, pro06, true, category1, 5, true);
    ProgramProduct programProduct7 = addToProgramProduct(bpProgram, pro07, true, category2, null, true);

    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct1);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct3);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct4);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct5);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct6);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct7);

    Pagination pagination = new Pagination(1, 4);
    List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts = mapper.getAllBy(FACILITY_TYPE_ID, yellowFeverProgram.getId(), searchParam, pagination);

    assertThat(facilityTypeApprovedProducts.size(), is(4));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getCode(), is("aPRO06"));
    assertThat(facilityTypeApprovedProducts.get(1).getProgramProduct().getProduct().getCode(), is("PRO01"));
    assertThat(facilityTypeApprovedProducts.get(2).getProgramProduct().getProduct().getCode(), is("PRO05"));
    assertThat(facilityTypeApprovedProducts.get(3).getProgramProduct().getProduct().getCode(), is("PRO03"));

    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getId(), is(programProduct6.getId()));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getActive(), is(programProduct6.getActive()));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getId(), is(pro06.getId()));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getPrimaryName(), is(pro06.getPrimaryName()));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getFullSupply(), is(pro06.getFullSupply()));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getActive(), is(pro06.getActive()));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getStrength(), is(pro06.getStrength()));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getDosageUnit(), is(pro06.getDosageUnit()));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getProductCategory().getId(), is(category1.getId()));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getProductCategory().getName(), is(category1.getName()));
  }

  @Test
  public void shouldGetPaginatedFacilityApprovedProductsByProgramIdAndFacilityTypeIdAndSearchedByProductCodeOrName() {
    String searchParam = "PRO04";
    Program yellowFeverProgram = make(a(defaultProgram));
    Program bpProgram = make(a(defaultProgram, with(programCode, "BP")));

    programMapper.insert(bpProgram);
    programMapper.insert(yellowFeverProgram);

    ProductCategory category2 = category("C3", "Category 3", 4);
    ProductCategory category3 = category("C4", "Category 4", 5);

    Product pro01 = product("PRO01", "Primary Name", true);
    Product pro03 = product("PRO03", "Primary Name", false);
    Product pro04 = product("PRO04", "Primary Name", true);
    Product pro05 = product("PRO05", "Primary Name", true);

    ProgramProduct programProduct1 = addToProgramProduct(yellowFeverProgram, pro01, true, category1, 6,true);
    ProgramProduct programProduct3 = addToProgramProduct(yellowFeverProgram, pro03, true, category2, 1,false);
    ProgramProduct programProduct4 = addToProgramProduct(yellowFeverProgram, pro04, false, category3, 2,true);
    ProgramProduct programProduct5 = addToProgramProduct(yellowFeverProgram, pro05, true, category1, 5, true);

    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct1);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct3);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct4);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct5);

    Pagination pagination = new Pagination(1, 4);
    List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts = mapper.getAllBy(FACILITY_TYPE_ID, yellowFeverProgram.getId(), searchParam, pagination);

    assertThat(facilityTypeApprovedProducts.size(), is(1));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getCode(), is("PRO04"));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getId(), is(programProduct4.getId()));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getActive(), is(programProduct4.getActive()));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getId(), is(pro04.getId()));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getPrimaryName(), is(pro04.getPrimaryName()));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getFullSupply(), is(pro04.getFullSupply()));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getActive(), is(pro04.getActive()));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getStrength(), is(pro04.getStrength()));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getDosageUnit(), is(pro04.getDosageUnit()));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getProductCategory().getId(), is(category3.getId()));
    assertThat(facilityTypeApprovedProducts.get(0).getProgramProduct().getProductCategory().getName(), is(category3.getName()));
  }

  @Test
  public void shouldGetTotalCountOfResultsSearchedOnlyByProgramIdAndFacilityTypeId() {
    String searchParam = "";
    Program yellowFeverProgram = make(a(defaultProgram));
    Program bpProgram = make(a(defaultProgram, with(programCode, "BP")));

    programMapper.insert(bpProgram);
    programMapper.insert(yellowFeverProgram);

    ProductCategory category2 = category("C2", "Category 2", 7);
    ProductCategory category3 = category("C3", "Category 3", 4);
    ProductCategory category4 = category("C4", "Category 4", 5);

    Product pro01 = product("PRO01", "Primary Name", true);
    Product pro03 = product("PRO03", "Primary Name", false);
    Product pro04 = product("PRO04", "Primary Name", true);
    Product pro05 = product("PRO05", "Primary Name", true);
    Product pro06 = product("aPRO06", "Primary Name", true);
    Product pro07 = product("PRO07", "Primary Name", true);

    ProgramProduct programProduct1 = addToProgramProduct(yellowFeverProgram, pro01, true, category1, 6, true);
    ProgramProduct programProduct3 = addToProgramProduct(yellowFeverProgram, pro03, true, category3, 1, false);
    ProgramProduct programProduct4 = addToProgramProduct(yellowFeverProgram, pro04, false, category4, 2, true);
    ProgramProduct programProduct5 = addToProgramProduct(yellowFeverProgram, pro05, true, category1, 5, true);
    ProgramProduct programProduct6 = addToProgramProduct(yellowFeverProgram, pro06, true, category1, 5, true);
    ProgramProduct programProduct7 = addToProgramProduct(bpProgram, pro07, true, category2, null, true);

    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct1);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct3);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct4);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct5);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct6);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct7);

    Integer count = mapper.getTotalSearchResultCount(FACILITY_TYPE_ID, yellowFeverProgram.getId(), searchParam);

    assertThat(count, is(5));
  }

  @Test
  public void shouldGetTotalCountOfResultsFetchedByProgramIdAndFacilityTypeIdAndSearchedByProductCodeOrName() {
    String searchParam = "name";
    Program yellowFeverProgram = make(a(defaultProgram));
    Program bpProgram = make(a(defaultProgram, with(programCode, "BP")));

    programMapper.insert(bpProgram);
    programMapper.insert(yellowFeverProgram);

    ProductCategory category2 = category("C2", "Category 2", 7);
    ProductCategory category3 = category("C3", "Category 3", 4);
    ProductCategory category4 = category("C4", "Category 4", 5);

    Product pro01 = product("PRO01", "Primary Name", true);
    Product pro03 = product("PRO03", "Primary", false);
    Product pro04 = product("PRO04", "Primary Name", true);
    Product pro05 = product("PRO05", "Primary", true);
    Product pro06 = product("aPRO06", "Primary", true);
    Product pro07 = product("PRO07", "Primary Name", true);

    ProgramProduct programProduct1 = addToProgramProduct(yellowFeverProgram, pro01, true, category1, 6, true);
    ProgramProduct programProduct3 = addToProgramProduct(yellowFeverProgram, pro03, true, category3, 1, false);
    ProgramProduct programProduct4 = addToProgramProduct(yellowFeverProgram, pro04, false, category4, 2, true);
    ProgramProduct programProduct5 = addToProgramProduct(yellowFeverProgram, pro05, true, category1, 5, true);
    ProgramProduct programProduct6 = addToProgramProduct(yellowFeverProgram, pro06, true, category1, 5, true);
    ProgramProduct programProduct7 = addToProgramProduct(bpProgram, pro07, true, category2, null, true);

    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct1);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct3);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct4);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct5);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct6);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct7);

    Integer count = mapper.getTotalSearchResultCount(FACILITY_TYPE_ID, yellowFeverProgram.getId(), searchParam);

    assertThat(count, is(2));
  }

  @Test
  public void shouldDeleteFacilityTypeApprovedProduct() {
    Program yellowFeverProgram = make(a(defaultProgram));
    programMapper.insert(yellowFeverProgram);

    Product product = product("PRO01", "Primary Name", true);
    ProgramProduct programProduct = addToProgramProduct(yellowFeverProgram, product, true, category1, 6, true);
    FacilityTypeApprovedProduct facilityTypeApprovedProduct = insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct);

    mapper.delete(facilityTypeApprovedProduct.getId());

    FacilityTypeApprovedProduct deletedFacilityTypeApprovedProduct = mapper.getBy(programProduct.getId(), FACILITY_TYPE_CODE);

    assertThat(deletedFacilityTypeApprovedProduct, is(nullValue()));
  }

  @Test
  public void shouldGetFacilityTypeApprovedProduct(){
    Program yellowFeverProgram = make(a(defaultProgram));
    programMapper.insert(yellowFeverProgram);

    Product product = product("PRO01", "Primary Name", true);
    ProgramProduct programProduct = addToProgramProduct(yellowFeverProgram, product, true, category1, 6, true);
    FacilityTypeApprovedProduct facilityTypeApprovedProduct = insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct);

    FacilityTypeApprovedProduct facilityTAProduct = mapper.get(facilityTypeApprovedProduct.getId());

    assertThat(facilityTAProduct.getMaxMonthsOfStock(),is(MAX_MONTHS_OF_STOCK));
  }

  private ProductCategory category(String categoryCode, String categoryName, int categoryDisplayOrder) {
    ProductCategory productCategory = new ProductCategory(categoryCode, categoryName, categoryDisplayOrder);
    productCategoryMapper.insert(productCategory);
    return productCategory;
  }

  private FacilityTypeApprovedProduct insertFacilityApprovedProduct(Long facilityTypeId, ProgramProduct programProduct) {
    FacilityType facilityType = new FacilityType();
    facilityType.setId(facilityTypeId);

    FacilityTypeApprovedProduct facilityTypeApprovedProduct = new FacilityTypeApprovedProduct(facilityType, programProduct, MAX_MONTHS_OF_STOCK);
    mapper.insert(facilityTypeApprovedProduct);
    return facilityTypeApprovedProduct;
  }

  private Product product(String productCode, String productPrimaryName, boolean isFullSupply) {
    Product product = make(a(ProductBuilder.defaultProduct, with(code, productCode), with(primaryName, productPrimaryName), with(fullSupply, isFullSupply)));
    productMapper.insert(product);
    return product;
  }

  private ProgramProduct addToProgramProduct(Program program, Product product, boolean isActive, ProductCategory productCategory, Integer displayOrder, boolean fullSupply) {
    ProgramProduct programProduct = new ProgramProduct(program, product, 30, isActive);
    programProduct.setProductCategory(productCategory);
    programProduct.setDisplayOrder(displayOrder);
    programProduct.setFullSupply(fullSupply);
    programProductMapper.insert(programProduct);
    return programProduct;
  }
}
