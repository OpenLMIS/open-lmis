/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.RegimenColumn;
import org.openlmis.rnr.domain.RegimenTemplate;
import org.openlmis.rnr.repository.mapper.RegimenColumnMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RegimenColumnRepositoryTest {

  @InjectMocks
  RegimenColumnRepository repository;

  @Mock
  RegimenColumnMapper mapper;

  @Test
  public void shouldGetMasterRegimenColumns() throws Exception {
    List<RegimenColumn> regimenColumns = new ArrayList<>();
    when(mapper.getMasterRegimenColumns()).thenReturn(regimenColumns);

    List<RegimenColumn> regimenColumnsFromDb = repository.getMasterRegimenColumnsByProgramId();

    verify(mapper).getMasterRegimenColumns();
    assertThat(regimenColumnsFromDb, is(regimenColumns));
  }

  @Test
  public void shouldInsertRegimenColumn() throws Exception {
    Long userId = 5L;
    List<RegimenColumn> regimenColumns = new ArrayList<>();
    RegimenColumn regimenColumn = new RegimenColumn();
    regimenColumns.add(regimenColumn);

    RegimenTemplate regimenTemplate = new RegimenTemplate(1l, regimenColumns);

    repository.save(regimenTemplate, userId);

    verify(mapper).insert(regimenColumn, regimenTemplate.getProgramId());
    assertThat(regimenColumn.getModifiedBy(), is(userId));
    assertThat(regimenColumn.getCreatedBy(), is(userId));
  }

  @Test
  public void shouldUpdateRegimenColumnIfIdExists() throws Exception {
    Long userId = 5L;
    List<RegimenColumn> regimenColumns = new ArrayList<>();
    RegimenColumn regimenColumn = new RegimenColumn();
    regimenColumn.setId(2L);
    regimenColumns.add(regimenColumn);

    RegimenTemplate regimenTemplate = new RegimenTemplate(1l, regimenColumns);

    repository.save(regimenTemplate, userId);

    verify(mapper).update(regimenColumn);
    assertThat(regimenColumn.getModifiedBy(), is(userId));
  }
}
