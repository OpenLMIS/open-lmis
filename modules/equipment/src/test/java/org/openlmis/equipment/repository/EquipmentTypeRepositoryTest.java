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
import org.openlmis.equipment.domain.EquipmentType;
import org.openlmis.equipment.repository.mapper.EquipmentTypeMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class EquipmentTypeRepositoryTest {
  @Mock
  private EquipmentTypeMapper mapper;

  @InjectMocks
  private EquipmentTypeRepository repository;

  @Test
  public void shouldGetAll() throws Exception {
    List<EquipmentType> types = new ArrayList<EquipmentType>();
    types.add(new EquipmentType());
    when(mapper.getAll()).thenReturn(types);

    List<EquipmentType> results = repository.getAll();
    assertEquals(results.size(), types.size());
  }

  @Test
  public void shouldInsert() throws Exception {
    EquipmentType type = new EquipmentType();
    type.setCode("Test");
    type.setName("Test");

    repository.insert(type);
    verify(mapper).insert(type);
  }

  @Test
  public void shouldGetById() throws Exception{
    EquipmentType type = new EquipmentType();
    type.setName("Test");
    type.setCode("Test");

    when(mapper.getEquipmentTypeById(1L)).thenReturn(type);

    EquipmentType result = repository.getEquipmentTypeById(1L);
    assertEquals(result.getName(), type.getName());
    assertEquals(result.getCode(), type.getCode());
  }

  @Test
  public void shouldUpdate() throws Exception{
    EquipmentType type = new EquipmentType();
    type.setCode("Test");
    type.setName("Test");

    repository.update(type);
    verify(mapper).update(type);
  }
}