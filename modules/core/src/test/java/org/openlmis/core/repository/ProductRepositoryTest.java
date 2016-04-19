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
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.KitProductBuilder;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.KitProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.DosageUnitMapper;
import org.openlmis.core.repository.mapper.ProductGroupMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.db.categories.UnitTests;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductRepositoryTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  ProductMapper mockedMapper;

  @Mock
  DosageUnitMapper dosageUnitMapper;

  @Mock
  ProductGroupMapper mockedProductGroupMapper;
  @InjectMocks
  ProductRepository repository;

  Product product;

  @Before
  public void setUp() {
    product = make(a(ProductBuilder.defaultProduct));
    Mockito.when(mockedProductGroupMapper.getByCode(product.getProductGroup().getCode())).thenReturn(product.getProductGroup());
  }

  @Test
  public void shouldInsertProduct() throws Exception {
    Product product = make(a(ProductBuilder.defaultProduct));
    repository.insert(product);
    verify(mockedMapper).insert(product);
  }

  @Test
  public void shouldRaiseDuplicateProductCodeError() throws Exception {

    expectedEx.expect(dataExceptionMatcher("error.duplicate.product.code"));

    doThrow(new DuplicateKeyException("")).when(mockedMapper).insert(product);
    repository.insert(product);
  }

  @Test
  public void shouldRaiseIncorrectReferenceDataError() throws Exception {
    expectedEx.expect(dataExceptionMatcher("error.reference.data.missing"));

    doThrow(new DataIntegrityViolationException("foreign key")).when(mockedMapper).insert(product);

    repository.insert(product);
  }

  @Test
  public void shouldRaiseMissingReferenceDataError() throws Exception {
    expectedEx.expect(dataExceptionMatcher("error.reference.data.missing"));

    doThrow(new DataIntegrityViolationException("violates not-null constraint")).when(mockedMapper).insert(product);

    repository.insert(product);
  }

  @Test
  public void shouldRaiseIncorrectDataValueError() throws Exception {
    expectedEx.expect(dataExceptionMatcher("error.incorrect.length"));

    doThrow(new DataIntegrityViolationException("value too long")).when(mockedMapper).insert(product);

    repository.insert(product);
  }

  @Test
  public void shouldGetProductIdForCode() throws Exception {
    when(mockedMapper.getIdByCode("code")).thenReturn(1L);
    assertThat(repository.getIdByCode("code"), is(1L));
  }

  @Test
  public void shouldThrowExceptionWhenProductCodeDoesNotExistWhenTryingToFetchProgramCodeById() throws Exception {
    when(mockedMapper.getIdByCode("code")).thenReturn(null);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("product.code.invalid");

    repository.getIdByCode("code");
  }

  @Test
  public void shouldReturnProductByCode() {
    Product product = new Product();
    String productCode = "P1";
    when(mockedMapper.getByCode(productCode)).thenReturn(product);
    Product returnedProduct = repository.getByCode(productCode);
    assertThat(returnedProduct, is(product));
  }

  @Test
  public void shouldUpdateProduct() {
    Product product = new Product();
    repository.update(product);
    verify(mockedMapper).update(product);
  }

  @Test
  public void shouldGetAllDosageUnits() {
    repository.getAllDosageUnits();
    verify(dosageUnitMapper).getAll();
  }

  @Test
  public void shouldGetById() {
    repository.getById(1l);
    verify(mockedMapper).getById(1l);
  }

  @Test
  public void shouldInsertKitProductsIfProductHasKitProducts() {
    KitProduct kitProduct1 = make(a(KitProductBuilder.defaultKit,
        with(KitProductBuilder.kitCode, product.getCode()),
        with(KitProductBuilder.productCode, "P1")));
    KitProduct kitProduct2 = make(a(KitProductBuilder.defaultKit,
        with(KitProductBuilder.kitCode, product.getCode()),
        with(KitProductBuilder.productCode, "P2")));
    product.setKitProductList(asList(kitProduct1, kitProduct2));

    repository.insert(product);

    verify(mockedMapper).insertKitProduct(kitProduct1);
    verify(mockedMapper).insertKitProduct(kitProduct2);

  }

  @Test
  public void shouldNotCallInsertKitProductsIfProductHasNoKitProducts() {
    repository.insert(product);

    verify(mockedMapper, never()).insertKitProduct(any(KitProduct.class));
  }

  @Test
  public void shouldCallGetProductsAfterUpdatedTime() {
    Date date = new Date();
    repository.getProductsAfterUpdatedTime(date);

    verify(mockedMapper).listProductsAfterUpdatedTime(date);
  }

  @Test
  public void shouldUpdateKitProductList() {
    Product product = make(a(ProductBuilder.defaultProduct, with(ProductBuilder.code, "KIT1")));
    product.setIsKit(true);

    KitProduct kitProduct1 = make(a(KitProductBuilder.defaultKit,
        with(KitProductBuilder.kitCode, product.getCode()),
        with(KitProductBuilder.productCode, "P1")));
    KitProduct kitProduct2 = make(a(KitProductBuilder.defaultKit,
        with(KitProductBuilder.kitCode, product.getCode()),
        with(KitProductBuilder.productCode, "P2")));
    List<KitProduct> oldKitProductList = asList(kitProduct1, kitProduct2);
    when(mockedMapper.getKitProductsByKitCode("KIT1")).thenReturn(oldKitProductList);

    KitProduct kitProduct3 = make(a(KitProductBuilder.defaultKit,
        with(KitProductBuilder.kitCode, product.getCode()),
        with(KitProductBuilder.productCode, "P3")));
    KitProduct kitProduct4 = make(a(KitProductBuilder.defaultKit,
        with(KitProductBuilder.kitCode, product.getCode()),
        with(KitProductBuilder.productCode, "P4")));
    List<KitProduct> newKitProductList = asList(kitProduct3, kitProduct4);
    product.setKitProductList(newKitProductList);

    repository.update(product);

    verify(mockedMapper).deleteKitProduct(kitProduct1);
    verify(mockedMapper).deleteKitProduct(kitProduct2);
    verify(mockedMapper).insertKitProduct(kitProduct3);
    verify(mockedMapper).insertKitProduct(kitProduct4);
  }
}

