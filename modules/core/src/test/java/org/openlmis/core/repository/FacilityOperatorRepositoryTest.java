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
import org.openlmis.core.domain.FacilityOperator;
import org.openlmis.core.repository.mapper.FacilityOperatorMapper;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FacilityOperatorRepositoryTest {
  @Mock
  private FacilityOperatorMapper mapper;

  private FacilityOperatorRepository repo;

  @Before
  public void setup() {
    repo = new FacilityOperatorRepository(mapper);
  }

  @Test
  public void shouldReturnNullIfGetByCodeGivenNull() {
    assertThat(repo.getByCode(null), nullValue());
  }

  @Test(expected=NullPointerException.class)
  public void shouldThrowNullPointerExceptionOnSaveWithNull() {
    repo.save(null);
  }

  @Test
  public void shouldUpdateOrInsertOnSaveUsingHasId() {
    FacilityOperator facOp = mock(FacilityOperator.class);

    // when hasId() is true, call update
    when(facOp.hasId()).thenReturn(true);
    repo.save(facOp);
    verify(mapper).update(facOp);

    // when hasId() is false, call insert
    when(facOp.hasId()).thenReturn(false);
    repo.save(facOp);
    verify(mapper).insert(facOp);
  }
}
