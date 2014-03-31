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
import static org.openlmis.core.builder.FacilityBuilder.FACILITY_TYPE_ID;
import static org.openlmis.core.builder.ProductBuilder.*;
import static org.openlmis.core.builder.ProductBuilder.displayOrder;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
@Category(IntegrationTests.class)
public class FacilityApprovedProductMapperIT {

  public static final Integer MAX_MONTHS_OF_STOCK = 3;

  @Autowired
  ProductMapper productMapper;
  @Autowired
  ProgramProductMapper programProductMapper;
  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  FacilityApprovedProductMapper facilityApprovedProductMapper;
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
  public void shouldInsertFacilityApprovedProduct() throws Exception {
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
    int insertionCount = facilityApprovedProductMapper.insert(facilityTypeApprovedProduct);

    assertThat(facilityTypeApprovedProduct.getId(), is(notNullValue()));
    assertThat(insertionCount, is(1));
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


    Product pro01 = product("PRO01", true);
    Product pro02 = product("PRO02", true);
    Product pro03 = product("PRO03", false);
    Product pro04 = product("PRO04", true);
    Product pro05 = product("PRO05", true);
    Product pro06 = product("PRO06", true);
    Product pro07 = product("PRO07", true);

    ProgramProduct programProduct1 = addToProgramProduct(yellowFeverProgram, pro01, true, category1, 6);
    ProgramProduct programProduct2 = addToProgramProduct(yellowFeverProgram, pro02, true, category2, 4);
    ProgramProduct programProduct3 = addToProgramProduct(yellowFeverProgram, pro03, true, category3, 1);
    ProgramProduct programProduct4 = addToProgramProduct(yellowFeverProgram, pro04, false, category4, 2);
    ProgramProduct programProduct5 = addToProgramProduct(yellowFeverProgram, pro05, true, category1, 5);
    ProgramProduct programProduct6 = addToProgramProduct(yellowFeverProgram, pro06, true, category6, 5);
    ProgramProduct programProduct7 = addToProgramProduct(bpProgram, pro07, true, category2, null);

    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct1);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct3);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct4);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct5);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct6);
    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct7);

    // Get full supply products
    List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts = facilityApprovedProductMapper.getFullSupplyProductsByFacilityAndProgram(
      facility.getId(), yellowFeverProgram.getId());
    assertEquals(3, facilityTypeApprovedProducts.size());

    FacilityTypeApprovedProduct facilityTypeApprovedProduct = facilityTypeApprovedProducts.get(0);

    assertEquals(programProduct6.getId(), facilityTypeApprovedProduct.getProgramProduct().getId());
    assertEquals(30, facilityTypeApprovedProduct.getProgramProduct().getDosesPerMonth().intValue());
    Product product = facilityTypeApprovedProduct.getProgramProduct().getProduct();
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

    // Non-full supply products
    List<FacilityTypeApprovedProduct> nonFullSupplyfacilityTypeApprovedProducts = facilityApprovedProductMapper.getNonFullSupplyProductsByFacilityAndProgram(
      facility.getId(), yellowFeverProgram.getId());

    assertThat(nonFullSupplyfacilityTypeApprovedProducts.size(), is(1));
    assertThat(nonFullSupplyfacilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getCode(), is("PRO03"));
    assertThat(nonFullSupplyfacilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getManufacturer(), is(nullValue()));
    assertThat(nonFullSupplyfacilityTypeApprovedProducts.get(0).getProgramProduct().getProduct().getFlammable(), is(nullValue()));
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
    facilityApprovedProductMapper.insert(facilityTypeApprovedProduct);
    return facilityTypeApprovedProduct;
  }

  private Product product(String productCode, boolean isFullSupply) {
    Product product = make(a(ProductBuilder.defaultProduct, with(code, productCode), with(fullSupply, isFullSupply)));
    productMapper.insert(product);
    return product;
  }

  private ProgramProduct addToProgramProduct(Program program, Product product, boolean isActive, ProductCategory productCategory, Integer displayOrder) {
    ProgramProduct programProduct = new ProgramProduct(program, product, 30, isActive);
    programProduct.setProductCategory(productCategory);
    programProduct.setDisplayOrder(displayOrder);
    programProductMapper.insert(programProduct);
    return programProduct;
  }

  @Test
  public void shouldGetFacilityApprovedProductId() {
    Program program = make(a(defaultProgram));

    Product product = make(a(defaultProduct));
    productMapper.insert(product);

    ProgramProduct programProduct = addToProgramProduct(program, product, true, category1, null);


    insertFacilityApprovedProduct(FACILITY_TYPE_ID, programProduct);

    FacilityTypeApprovedProduct facilityTypeApprovedProductsFromDB = facilityApprovedProductMapper.getFacilityApprovedProductIdByProgramProductAndFacilityTypeCode(programProduct.getId(), "warehouse");

    assertNotNull(facilityTypeApprovedProductsFromDB);
    assertEquals(facilityTypeApprovedProductsFromDB.getMaxMonthsOfStock(), MAX_MONTHS_OF_STOCK);
  }
}
