/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProductGroupMapper;
import org.openlmis.db.categories.UnitTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductGroupRepositoryTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Autowired
  ProductGroupRepository repository;

  @Mock
  ProductGroupMapper mapper;

  @Test
  public void shouldSaveProductGroup() throws Exception {
    ProductGroup productGroup = new ProductGroup();
    repository = new ProductGroupRepository(mapper);

    repository.insert(productGroup);
    verify(mapper).insert(productGroup);
  }

  @Test
  public void shouldUpdateProductGroup() throws Exception {
    ProductGroup productGroup = new ProductGroup();
    repository = new ProductGroupRepository(mapper);

    repository.update(productGroup);
    verify(mapper).update(productGroup);
  }

  @Test
  public void shouldThrowDuplicateKeyExceptionWhenDuplicateProductGroupCodeFound() throws Exception {
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Duplicate Product Group Code Found");
    repository = new ProductGroupRepository(mapper);
    ProductGroup productGroup = new ProductGroup();
    doThrow(new DuplicateKeyException("")).when(mapper).insert(productGroup);
    repository.insert(productGroup);
  }

  @Test
  public void shouldThrowDataIntegrityViolationExceptionWhenMissingMandatoryData() throws Exception {
    expectedEx.expect(dataExceptionMatcher("error.reference.data.missing"));

    ProductGroup productGroup = new ProductGroup();
    doThrow(new DataIntegrityViolationException("violates not-null constraint")).when(mapper).insert(productGroup);

    new ProductGroupRepository(mapper).insert(productGroup);
  }

  @Test
  public void shouldThrowIncorrectDataLengthErrorWhenInvalidDataLength() throws Exception {
    expectedEx.expect(dataExceptionMatcher("error.incorrect.length"));

    ProductGroup productGroup = new ProductGroup();

    doThrow(new DataIntegrityViolationException("")).when(mapper).insert(productGroup);

    new ProductGroupRepository(mapper).insert(productGroup);
  }
}
