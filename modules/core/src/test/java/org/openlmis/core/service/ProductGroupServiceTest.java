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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.repository.ProductGroupRepository;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductGroupServiceTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private ProductGroupRepository repository;

  @InjectMocks
  private ProductGroupService service;
  private ProductGroup productGroup;

  @Before
  public void setup() {
    productGroup = new ProductGroup();
  }

  @Test
  public void shouldSaveProductGroup() throws Exception {
    service.save(productGroup);

    verify(repository).insert(productGroup);
  }

  @Test
  public void shouldUpdateProductGroup() throws Exception {
    productGroup.setId(1L);

    service.save(productGroup);

    verify(repository).update(productGroup);
  }

  @Test
  public void shouldGetAll() {
    service.getAll();

    verify(repository).getAll();
  }

  @Test
  public void shouldValidateAndReturnNullIfGroupEmpty() throws Exception {
    assertThat(service.validateAndReturn(null), is(nullValue()));
  }

  @Test
  public void shouldValidateAndReturnNullIfGroupCodeEmpty() throws Exception {
    assertThat(service.validateAndReturn(new ProductGroup(null,"name")), is(nullValue()));
  }

  @Test
  public void shouldValidateAndSetProductGroup() throws Exception {
    ProductGroup group1 = new ProductGroup("code1", "name1");
    ProductGroup group2 = new ProductGroup("code1", "name2");

    when(repository.getByCode("code1")).thenReturn(group2);

    ProductGroup response = service.validateAndReturn(group1);

    assertThat(response, is(group2));
  }

  @Test
  public void shouldRaiseInvalidReferenceDataProductGroupError() throws Exception {
    ProductGroup group = new ProductGroup("invalid product group code","name");
    when(repository.getByCode("invalid product group code")).thenReturn(null);

    expectedEx.expect(dataExceptionMatcher("error.reference.data.invalid.product.group"));

    service.validateAndReturn(group);
  }
}
