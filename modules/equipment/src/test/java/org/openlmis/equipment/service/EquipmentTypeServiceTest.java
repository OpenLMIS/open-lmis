/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.equipment.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.EquipmentType;
import org.openlmis.equipment.repository.EquipmentTypeRepository;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class EquipmentTypeServiceTest {

  @Mock
  EquipmentTypeRepository repository;

  @InjectMocks
  EquipmentTypeService service;

  @Test
  public void shouldGetAll() throws Exception {
    service.getAll();
    verify(repository).getAll();
  }

  @Test
  public void shouldGetTypeById() throws Exception {
    service.getTypeById(1L);

    verify(repository).getEquipmentTypeById(1L);
  }

  @Test
  public void shouldSaveNewType() throws Exception {
    EquipmentType type = new EquipmentType();
    type.setName("Test");
    type.setCode("Test");

    service.save(type);

    verify(repository).insert(type);
    verify(repository, never()).update(type);
  }

  @Test
  public void shouldSaveAnUpdate() throws Exception{
    EquipmentType type = new EquipmentType();
    type.setId(1L);
    type.setName("Test");
    type.setCode("Test");

    service.save(type);

    verify(repository, never()).insert(type);
    verify(repository).update(type);
  }
}