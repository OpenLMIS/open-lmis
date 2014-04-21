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
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.repository.EquipmentRepository;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class EquipmentServiceTest {

  @Mock
  private EquipmentRepository repository;

  @InjectMocks
  private EquipmentService service;

  @Test
  public void shouldGetAll() throws Exception {
    List<Equipment> expectedEquipments = new ArrayList<Equipment>();
    expectedEquipments.add(new Equipment());
    when(repository.getAll()).thenReturn(expectedEquipments);

    List<Equipment> equipments = service.getAll();
    verify(repository).getAll();

    assertEquals(equipments, expectedEquipments);
  }

  @Test
  public void shouldGetById() throws Exception {
    Equipment equipment = new Equipment();
    equipment.setCode("123");
    when(repository.getById(1L)).thenReturn(equipment);

    Equipment result = service.getById(1L);
    assertEquals(result.getCode(), equipment.getCode());
  }

  @Test
  public void shouldSaveNewEquipment() throws Exception {
    Equipment equipment = new Equipment();
    equipment.setCode("123");

    service.save(equipment);
    verify(repository).insert(equipment);
    verify(repository, never()).update(equipment);
  }

  @Test
  public void shouldSaveChangesInExistingEquipment() throws Exception {
    Equipment equipment = new Equipment();
    equipment.setId(1L);
    equipment.setCode("123");

    service.save(equipment);
    verify(repository, never()).insert(equipment);
    verify(repository).update(equipment);
  }
}