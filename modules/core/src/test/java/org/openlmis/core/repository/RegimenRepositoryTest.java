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

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Regimen;
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.core.repository.mapper.RegimenCategoryMapper;
import org.openlmis.core.repository.mapper.RegimenMapper;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RegimenRepositoryTest {

  @Mock
  RegimenCategoryMapper regimenCategoryMapper;

  @Mock
  RegimenMapper mapper;

  @InjectMocks
  RegimenRepository repository;


  @Test
  public void shouldInsertARegimen() {
    final Regimen regimen = new Regimen();
    final Regimen regimen2 = new Regimen();
    regimen2.setName("regimen");
    List<Regimen> regimens = new ArrayList<Regimen>() {{
      add(regimen);
      add(regimen2);
    }};

    repository.save(regimens, 1L);

    assertThat(regimen.getCreatedBy(), is(1L));
    assertThat(regimen.getModifiedBy(), is(1L));
    verify(mapper).insert(regimen);
    verify(mapper).insert(regimen2);
  }

  @Test
  public void shouldInsertARegimenIfIdIsSet() {
    final Regimen regimen = new Regimen();
    final Regimen regimen2 = new Regimen();
    regimen2.setName("regimen");
    regimen.setId(1L);
    List<Regimen> regimens = new ArrayList<Regimen>() {{
      add(regimen);
      add(regimen2);
    }};

    repository.save(regimens, 1L);

    assertThat(regimen.getModifiedBy(), is(1L));
    verify(mapper).update(regimen);
    verify(mapper).insert(regimen2);
  }

  @Test
  public void shouldGetRegimenByProgram() {
    List<Regimen> expectedRegimens = new ArrayList<>();
    Long programId = 1l;
    when(mapper.getByProgram(programId)).thenReturn(expectedRegimens);

    List<Regimen> regimens = repository.getByProgram(programId);

    assertThat(regimens, is(expectedRegimens));
    verify(mapper).getByProgram(programId);
  }

  @Test
  public void shouldGetAllRegimenCategories() {
    List<RegimenCategory> expectedRegimenCategories = new ArrayList<>();
    when(regimenCategoryMapper.getAll()).thenReturn(expectedRegimenCategories);

    List<RegimenCategory> regimenCategories = repository.getAllRegimenCategories();

    assertThat(regimenCategories, is(expectedRegimenCategories));
    verify(regimenCategoryMapper).getAll();
  }

}
