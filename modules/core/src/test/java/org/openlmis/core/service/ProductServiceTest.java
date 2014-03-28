/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.ProductRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.ProgramProductBuilder.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {

  @Mock
  private ProductCategoryService categoryService;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private ProgramService programService;

  @Mock
  ProgramProductService programProductService;

  @InjectMocks
  private ProductService productService;

  @Test
  public void shouldStoreProduct() throws Exception {
    Product product = new Product();
    product.setPackSize(5);

    productService.save(product);

    verify(productRepository).insert(product);
  }

  @Test
  public void shouldValidateProductBeforeSaveThrowExceptionIfPackSizeIsZero() throws Exception {
    Product spyProduct = spy(new Product());
    doNothing().when(spyProduct).validate();

    productService.save(spyProduct);

    verify(spyProduct).validate();
  }

  @Test
  public void shouldInsertProductIfNotPresent() throws Exception {
    Product product = new Product();
    product.setCode("P1");
    product.setPackSize(5);

    when(productRepository.getByCode("P1")).thenReturn(null);

    productService.save(product);

    verify(productRepository).insert(product);
  }

  @Test
  public void shouldUpdateProductIfPresent() {
    Product product = new Product();
    product.setId(2L);
    product.setCode("proCode");
    product.setPackSize(5);

    List<ProgramProduct> programProducts = new ArrayList<>();
    when(programProductService.getByProductCode("proCode")).thenReturn(programProducts);

    productService.save(product);

    verify(productRepository).update(product);
  }

  @Test
  public void shouldNotifyIfActiveFlagsChangeFromTTtoTF() throws Exception {
    String productCode = "P1";
    Product product = new Product();
    product.setActive(false);
    product.setCode(productCode);
    product.setId(2L);
    product.setPackSize(5);

    final ProgramProduct existingProgramProduct = make(a(defaultProgramProduct, with(active, true), with(productActive, true)));
    List programProductList = new ArrayList() {{
      add(existingProgramProduct);
    }};
    when(programProductService.getByProductCode(productCode)).thenReturn(programProductList);

    productService.save(product);

    verify(programService).setFeedSendFlag(existingProgramProduct.getProgram(), true);
  }

  @Test
  public void shouldNotifyIfActiveFlagsChangeFromTFtoTT() throws Exception {
    String productCode = "P1";
    Product product = new Product();
    product.setActive(true);
    product.setCode(productCode);
    product.setId(2L);
    product.setPackSize(5);

    final ProgramProduct tbProduct = make(a(defaultProgramProduct, with(programCode, "TB"), with(active, true), with(productActive, false)));
    final ProgramProduct hivProduct = make(a(defaultProgramProduct, with(programCode, "HIV"), with(active, true), with(productActive, false)));
    List programProductList = new ArrayList() {{
      add(hivProduct);
      add(tbProduct);
    }};
    when(programProductService.getByProductCode(productCode)).thenReturn(programProductList);

    productService.save(product);

    verify(programService).setFeedSendFlag(tbProduct.getProgram(), true);
    verify(programService).setFeedSendFlag(hivProduct.getProgram(), true);
  }

  @Test
  public void shouldNotNotifyIfNotAnUpdate() throws Exception {
    String productCode = "P1";
    Product product = new Product();
    product.setActive(true);
    product.setCode(productCode);
    product.setPackSize(5);

    productService.save(product);

    verify(programService, never()).setFeedSendFlag(any(Program.class), anyBoolean());
    verify(programProductService, never()).getByProductCode(anyString());
  }

  @Test
  public void shouldNotNotifyIfActiveFlagsChangeFromFTtoFF() throws Exception {
    String productCode = "P1";
    Product product = new Product();
    product.setActive(false);
    product.setCode(productCode);
    product.setId(2L);
    product.setPackSize(5);

    final ProgramProduct existingProgramProduct = make(a(defaultProgramProduct, with(active, false), with(productActive, true)));
    List programProductList = new ArrayList() {{
      add(existingProgramProduct);
    }};
    when(programProductService.getByProductCode(productCode)).thenReturn(programProductList);

    productService.save(product);

    verify(programService, never()).setFeedSendFlag(any(Program.class), anyBoolean());
  }

  @Test
  public void shouldNotNotifyIfActiveFlagsChangeFromFFtoFT() throws Exception {
    String productCode = "P1";
    Product product = new Product();
    product.setActive(true);
    product.setCode(productCode);
    product.setId(2L);
    product.setPackSize(5);

    final ProgramProduct existingProgramProduct = make(a(defaultProgramProduct, with(active, false), with(productActive, false)));
    List programProductList = new ArrayList() {{
      add(existingProgramProduct);
    }};
    when(programProductService.getByProductCode(productCode)).thenReturn(programProductList);

    productService.save(product);

    verify(programService, never()).setFeedSendFlag(any(Program.class), anyBoolean());
  }

}
