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

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.repository.mapper.ProductGroupMapper;
import org.openlmis.db.categories.UnitTests;
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

  @InjectMocks
  ProductGroupRepository repository;

  @Mock
  ProductGroupMapper mapper;

  @Test
  public void shouldSaveProductGroup() throws Exception {
    ProductGroup productGroup = new ProductGroup();

    repository.insert(productGroup);
    verify(mapper).insert(productGroup);
  }

  @Test
  public void shouldUpdateProductGroup() throws Exception {
    ProductGroup productGroup = new ProductGroup();

    repository.update(productGroup);
    verify(mapper).update(productGroup);
  }

  @Test
  public void shouldThrowDuplicateKeyExceptionWhenDuplicateProductGroupCodeFound() throws Exception {
    expectedEx.expect(dataExceptionMatcher("error.duplicate.product.group.code"));

    ProductGroup productGroup = new ProductGroup();
    doThrow(new DuplicateKeyException("")).when(mapper).insert(productGroup);
    repository.insert(productGroup);
  }

  @Test
  public void shouldThrowDataIntegrityViolationExceptionWhenMissingMandatoryData() throws Exception {
    expectedEx.expect(dataExceptionMatcher("error.reference.data.missing"));

    ProductGroup productGroup = new ProductGroup();
    doThrow(new DataIntegrityViolationException("violates not-null constraint")).when(mapper).insert(productGroup);

    repository.insert(productGroup);
  }

  @Test
  public void shouldThrowIncorrectDataLengthErrorWhenInvalidDataLength() throws Exception {
    expectedEx.expect(dataExceptionMatcher("error.incorrect.length"));

    ProductGroup productGroup = new ProductGroup();

    doThrow(new DataIntegrityViolationException("")).when(mapper).insert(productGroup);

    repository.insert(productGroup);
  }

  @Test
  public void shouldGetAll() {
    repository.getAll();
    verify(mapper).getAll();
  }
}
