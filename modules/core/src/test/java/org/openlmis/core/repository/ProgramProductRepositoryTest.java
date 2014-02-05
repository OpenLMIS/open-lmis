/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.builder.ProgramProductBuilder;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.matchers.Matchers;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
import org.openlmis.core.repository.mapper.ProgramProductPriceMapper;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.builder.ProductBuilder.defaultProduct;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramProductBuilder.PRODUCT_CODE;
import static org.openlmis.core.builder.ProgramProductBuilder.PROGRAM_CODE;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class ProgramProductRepositoryTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @InjectMocks
  private ProgramProductRepository programProductRepository;

  @Mock
  private ProgramProductMapper programProductMapper;

  @Mock
  private ProgramRepository programRepository;

  @Mock
  private ProductMapper productMapper;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private ProgramProductPriceMapper programProductPriceMapper;

  private ProgramProduct programProduct;

  @Before
  public void setUp() throws Exception {
    programProduct = make(a(ProgramProductBuilder.defaultProgramProduct));
    programProduct.setModifiedDate(new Date());

    when(productRepository.getIdByCode("productCode")).thenReturn(1L);

    when(programProductMapper.getByProgramAndProductId(anyLong(), anyLong())).thenReturn(programProduct);
  }

  @Test
  public void shouldInsertProgramForAProduct() {
    Program program = new Program();
    program.setCode("P1");
    Product product = new Product();
    product.setCode("P2");
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProduct.setModifiedDate(new Date());

    when(programProductMapper.getByProgramAndProductId(anyLong(), anyLong())).thenReturn(null);

    programProductRepository.save(programProduct);
    verify(programProductMapper).insert(programProduct);
  }

  @Test
  public void shouldThrowErrorWhenInsertingProductForInvalidProgram() {
    Product product = make(a(defaultProduct));
    Program program = make(a(defaultProgram));
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    when(programRepository.getIdByCode(programProduct.getProgram().getCode())).thenThrow(new DataException("exception"));

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("exception");
    programProductRepository.save(programProduct);
  }

  @Test
  public void shouldThrowErrorWhenInsertingInvalidProductForAProgram() {
    Program program = make(a(defaultProgram));
    when(programRepository.getIdByCode(program.getCode())).thenReturn(1L);
    ProgramProduct programProduct = new ProgramProduct(program, new Product(), 10, true);

    expectedEx.expect(Matchers.dataExceptionMatcher("product.code.invalid"));

    programProductRepository.save(programProduct);
  }

  @Test
  public void shouldGetProgramProductIdByProgramAndProductId() {
    when(programProductMapper.getIdByProgramAndProductId(1L, 2L)).thenReturn(3L);

    assertThat(programProductRepository.getIdByProgramIdAndProductId(1L, 2L), is(3L));
    verify(programProductMapper).getIdByProgramAndProductId(1L, 2L);
  }

  @Test
  public void shouldThrowExceptionIfNoProgramProductExistForGivenProgramIdAndProductId() {
    when(programProductMapper.getIdByProgramAndProductId(1L, 2L)).thenReturn(null);
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("programProduct.product.program.invalid");
    programProductRepository.getIdByProgramIdAndProductId(1L, 2L);
  }

  @Test
  public void shouldGetProgramProductByProgramAndProductCodes() throws Exception {
    ProgramProduct programProduct = make(a(ProgramProductBuilder.defaultProgramProduct));

    final Long programId = 123L;
    when(programRepository.getIdByCode(PROGRAM_CODE)).thenReturn(programId);
    final Long productId = 12L;
    when(productRepository.getIdByCode(PRODUCT_CODE)).thenReturn(productId);
    ProgramProduct expectedProgramProduct = new ProgramProduct();
    when(programProductMapper.getByProgramAndProductId(programId, productId)).thenReturn(expectedProgramProduct);

    ProgramProduct result = programProductRepository.getByProgramAndProductCode(programProduct);
    verify(programRepository).getIdByCode(PROGRAM_CODE);
    verify(productRepository).getIdByCode(PRODUCT_CODE);
    verify(programProductMapper).getByProgramAndProductId(programId, productId);

    assertThat(result, is(expectedProgramProduct));
  }

  @Test
  public void shouldUpdateCurrentPriceOfProgramProduct() throws Exception {
    ProgramProduct programProduct = make(a(ProgramProductBuilder.defaultProgramProduct));
    programProductRepository.updateCurrentPrice(programProduct);

    verify(programProductMapper).updateCurrentPrice(programProduct);
  }

  @Test
  public void shouldInsertNewCostWithStartDateAsCurrentAndCloseLastPeriodsCostWithEndDateAsCurrent() throws Exception {
    ProgramProductPrice programProductPrice = new ProgramProductPrice();
    programProductRepository.updatePriceHistory(programProductPrice);

    verify(programProductPriceMapper).closeLastActivePrice(programProductPrice);
    verify(programProductPriceMapper).insertNewCurrentPrice(programProductPrice);
  }

  @Test
  public void shouldUpdateProgramProductIfExist() throws Exception {
    Long programId = 88L;
    Long productId = 99L;

    programProduct.setId(1L);
    when(programRepository.getIdByCode(anyString())).thenReturn(programId);
    when(productRepository.getIdByCode(anyString())).thenReturn(productId);

    programProductRepository.save(programProduct);

    assertThat(programProduct.getProgram().getId(), is(88L));
    assertThat(programProduct.getProduct().getId(), is(99L));
    verify(programProductMapper).update(programProduct);
  }

  @Test
  public void shouldUpdateProgramProduct() throws Exception {
    programProductRepository.updateProgramProduct(programProduct);

    verify(programProductMapper).update(programProduct);
  }

  @Test
  public void shouldGetProgramProductsByProgram() {
    Program program = new Program();
    List<ProgramProduct> expectedProgramProducts = new ArrayList<>();
    when(programProductMapper.getByProgram(program)).thenReturn(expectedProgramProducts);

    List<ProgramProduct> programProducts = programProductRepository.getByProgram(program);

    verify(programProductMapper).getByProgram(program);
    assertThat(programProducts, is(expectedProgramProducts));
  }

  @Test
  public void shouldGetProgramProductById() {
    ProgramProduct expectedProgramProduct = new ProgramProduct();
    when(programProductMapper.getById(2L)).thenReturn(expectedProgramProduct);

    ProgramProduct programProduct = programProductRepository.getById(2L);

    verify(programProductMapper).getById(2L);
    assertThat(programProduct, is(expectedProgramProduct));
  }

  @Test
  public void shouldGetProgramProductByProductCode() {
    List<ProgramProduct> expectedProgramProducts = new ArrayList<>();
    when(programProductMapper.getByProductCode("code")).thenReturn(expectedProgramProducts);

    List<ProgramProduct> programProducts = programProductRepository.getByProductCode("code");

    verify(programProductMapper).getByProductCode("code");
    assertThat(programProducts, is(expectedProgramProducts));
  }

  @Test
  public void shouldGetProgramProductByProgramIdAndFacilityTypeCode() {
    List<ProgramProduct> expectedProgramProducts = new ArrayList<>();
    when(programProductMapper.getByProgramIdAndFacilityTypeCode(10L, "warehouse")).thenReturn(expectedProgramProducts);

    List<ProgramProduct> programProducts = programProductRepository.getProgramProductsBy(10L, "warehouse");

    verify(programProductMapper).getByProgramIdAndFacilityTypeCode(10L, "warehouse");
    assertThat(programProducts, is(expectedProgramProducts));
  }
}
