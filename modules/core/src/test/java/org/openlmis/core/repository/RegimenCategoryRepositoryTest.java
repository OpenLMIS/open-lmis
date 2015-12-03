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
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.core.repository.mapper.RegimenCategoryMapper;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RegimenCategoryRepositoryTest {

  @Mock
  private RegimenCategoryMapper mapper;

  private RegimenCategoryRepository repo;

  @Before
  public void setup() {
    repo = new RegimenCategoryRepository(mapper);
  }

  @Test
  public void shouldReturnNullIfGetByCodeGivenNull() {
    assertThat(repo.getByCode(null), nullValue());
  }

  @Test
  public void shouldCallInsertOnSaveWithoutId() {
    RegimenCategory regCat = mock(RegimenCategory.class);
    when(regCat.hasId()).thenReturn(false);
    repo.save(regCat);
    verify(mapper).insert(regCat);
  }

  @Test
  public void shouldCallUpdateOnSaveWithId() {
    RegimenCategory regCat = mock(RegimenCategory.class);
    when(regCat.hasId()).thenReturn(true);
    repo.save(regCat);
    verify(mapper).update(regCat);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionOnSaveNull() {
    repo.save(null);
  }

  @Test
  public void shouldUseGetAllInMapper() {
    repo.getAll();
    verify(mapper).getAll();
  }
}
