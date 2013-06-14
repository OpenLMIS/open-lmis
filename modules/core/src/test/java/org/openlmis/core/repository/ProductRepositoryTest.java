/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProductGroupMapper;
import org.openlmis.core.repository.mapper.ProductMapper;
import org.openlmis.db.categories.UnitTests;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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
  ProductGroupMapper mockedProductGroupMapper;

  ProductRepository repository;
  Product product;

  @Before
  public void setUp() {
    repository = new ProductRepository(mockedMapper, mockedProductGroupMapper);
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

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Duplicate Product Code found");
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
  public void shouldRaiseInvalidReferenceDataDosageUnitError() throws Exception {
    product.getDosageUnit().setCode("invalid code");
    when(mockedMapper.getDosageUnitIdForCode("invalid code")).thenReturn(null);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid reference data 'Dosage Unit'");
    repository.insert(product);
  }

  @Test
  public void shouldSetDataDosageUnitIdForCode() throws Exception {
    product.getDosageUnit().setCode("valid code");
    when(mockedMapper.getDosageUnitIdForCode("valid code")).thenReturn(1L);

    repository.insert(product);
    assertThat(product.getDosageUnit().getId(), is(1L));
  }

  @Test
  public void shouldRaiseInvalidReferenceDataProductFormError() throws Exception {
    product.getForm().setCode("invalid code");
    when(mockedMapper.getProductFormIdForCode("invalid code")).thenReturn(null);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid reference data 'Product Form'");
    repository.insert(product);
  }

  @Test
  public void shouldSetProductFormIdForCode() throws Exception {
    product.getForm().setCode("valid code");
    when(mockedMapper.getProductFormIdForCode("valid code")).thenReturn(1L);

    repository.insert(product);
    assertThat(product.getForm().getId(), is(1L));
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
  public void shouldRaiseInvalidReferenceDataProductGroupError() throws Exception {
    product.getProductGroup().setCode("invalid product group code");
    when(mockedProductGroupMapper.getByCode("invalid product group code")).thenReturn(null);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Invalid reference data 'Product Group'");
    repository.insert(product);
  }

  @Test
  public void shouldSetProductGroupIdForCode() throws Exception {
    product.getProductGroup().setCode("valid code");
    ProductGroup productGroup = new ProductGroup();
    productGroup.setId(1L);
    when(mockedProductGroupMapper.getByCode("valid code")).thenReturn(productGroup);

    repository.insert(product);
    assertThat(product.getProductGroup().getId(), is(productGroup.getId()));
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
}
