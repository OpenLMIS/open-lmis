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
import org.openlmis.equipment.domain.EquipmentInventory;
import org.openlmis.equipment.repository.mapper.EquipmentInventoryMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class EquipmentInventoryRepositoryTest {

  @Mock
  private EquipmentInventoryMapper mapper;

  @InjectMocks
  private EquipmentInventoryRepository repository;

  @Test
  public void shouldGetFacilityInventory() throws Exception {
    EquipmentInventory inventory = new EquipmentInventory();
    inventory.setModel("123");
    List<EquipmentInventory> inventories = new ArrayList<>();
    inventories.add(inventory);

    when(mapper.getInventoryByFacilityAndProgram(1L, 1L)).thenReturn(inventories);

    List<EquipmentInventory> results = repository.getFacilityInventory(1L, 1L);
    verify(mapper).getInventoryByFacilityAndProgram(1L, 1L);
    assertEquals(results, inventories);

  }

  @Test
  public void shouldGetInventoryById() throws Exception {
    EquipmentInventory inventory = new EquipmentInventory();
    inventory.setModel("123");

    when(mapper.getInventoryById(1L)).thenReturn(inventory);

    EquipmentInventory result = repository.getInventoryById(1L);

    verify(mapper).getInventoryById(1L);
    assertEquals(result, inventory);
  }

  @Test
  public void shouldInsert() throws Exception {
    EquipmentInventory inventory = new EquipmentInventory();
    inventory.setModel("123");

    repository.insert(inventory);
    verify(mapper).insert(inventory);
  }

  @Test
  public void shouldUpdate() throws Exception {
    EquipmentInventory inventory = new EquipmentInventory();
    inventory.setModel("123");

    repository.update(inventory);
    verify(mapper).update(inventory);
  }
}