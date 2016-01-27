/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.equipment.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.repository.mapper.EquipmentMapper;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class EquipmentRepositoryTest {

  @Mock
  EquipmentMapper mapper;

  @InjectMocks
  EquipmentRepository repository;

  @Test
  public void shouldGetById() throws Exception {
    Equipment equipment = new Equipment();
    equipment.setName("Name");

    when(mapper.getById(1L)).thenReturn(equipment);

    Equipment result = repository.getById(1L);
    verify(mapper).getById(1L);

    assertEquals(result, equipment);
  }

  @Test
  public void shouldGetAll() throws Exception {
    repository.getAll();
    verify(mapper).getAll();
  }

  @Test
  public void shouldInsert() throws Exception {
    Equipment equipment = new Equipment();
    equipment.setName("Name");

    repository.insert(equipment);
    verify(mapper).insert(equipment);
  }

  @Test
  public void shouldUpdate() throws Exception {
    Equipment equipment = new Equipment();
    equipment.setName("Name");

    repository.update(equipment);
    verify(mapper).update(equipment);
  }
}