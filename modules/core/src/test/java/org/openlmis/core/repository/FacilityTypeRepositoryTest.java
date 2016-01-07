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
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.repository.mapper.FacilityTypeMapper;
import org.openlmis.db.categories.UnitTests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityTypeRepositoryTest {

  @Mock
  private FacilityTypeMapper mapper;

  private FacilityTypeRepository repo;

  @Before
  public void setup() {
    repo = new FacilityTypeRepository(mapper);
  }

  @Test
  public void shouldUpdateOrInsertOnSaveBasedOnHasId() {
    FacilityType facType = mock(FacilityType.class);

    // when hasId is true, update should be called
    when(facType.hasId()).thenReturn(true);
    repo.save(facType);
    verify(mapper).update(facType);

    // when hasId is false, insert should be called
    when(facType.hasId()).thenReturn(false);
    repo.save(facType);
    verify(mapper).insert(facType);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionOnSaveWithNull() {
    repo.save(null);
  }

  @Test
  public void shouldReturnNullWhenGetByCodeIsNull() {
    assertThat(repo.getByCode(null), nullValue());
  }
}
