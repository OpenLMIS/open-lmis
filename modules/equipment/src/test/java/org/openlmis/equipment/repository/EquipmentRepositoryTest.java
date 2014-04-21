/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
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
    equipment.setCode("Test");
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
    equipment.setCode("Test");
    equipment.setName("Name");

    repository.insert(equipment);
    verify(mapper).insert(equipment);
  }

  @Test
  public void shouldUpdate() throws Exception {
    Equipment equipment = new Equipment();
    equipment.setCode("Test");
    equipment.setName("Name");

    repository.update(equipment);
    verify(mapper).update(equipment);
  }
}