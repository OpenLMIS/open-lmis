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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.equipment.domain.EquipmentType;
import org.openlmis.equipment.repository.mapper.EquipmentTypeMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EquipmentTypeRepositoryTest {



  @Mock
  private EquipmentTypeMapper mapper;

  @InjectMocks
  private EquipmentTypeRepository repository;

  @Test
  public void testGetAll() throws Exception {
    List<EquipmentType> types = new ArrayList<EquipmentType>();
    types.add(new EquipmentType());
    when(mapper.getAll()).thenReturn(types);

    List<EquipmentType> results = repository.getAll();
    assertEquals(results.size(), types.size());
  }

  @Test
  public void testInsert() throws Exception {
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