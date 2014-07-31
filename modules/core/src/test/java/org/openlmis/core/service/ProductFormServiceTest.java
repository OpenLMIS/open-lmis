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

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ProductForm;
import org.openlmis.core.repository.ProductFormRepository;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductFormServiceTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private ProductFormRepository repository;

  @InjectMocks
  private ProductFormService service;

  @Test
  public void shouldGetAll() {
    service.getAll();

    verify(repository).getAll();
  }

  @Test
  public void shouldValidateAndReturnNullIfFormEmpty() throws Exception {
    assertThat(service.validateAndReturn(null), is(nullValue()));
  }

  @Test
  public void shouldValidateAndReturnNullIfFormCodeEmpty() throws Exception {
    assertThat(service.validateAndReturn(new ProductForm(null, 1)), is(nullValue()));
  }

  @Test
  public void shouldValidateAndSetProductForm() throws Exception {
    ProductForm form1 = new ProductForm("code1", 1);
    ProductForm form2 = new ProductForm("code1", 2);

    when(repository.getByCode("code1")).thenReturn(form2);

    ProductForm response = service.validateAndReturn(form1);

    assertThat(response, is(form2));
  }

  @Test
  public void shouldRaiseInvalidReferenceDataProductFormError() throws Exception {
    ProductForm form = new ProductForm("invalid product form code", 1);
    when(repository.getByCode("invalid product form code")).thenReturn(null);

    expectedEx.expect(dataExceptionMatcher("error.reference.data.invalid.product.form"));

    service.validateAndReturn(form);
  }
}
