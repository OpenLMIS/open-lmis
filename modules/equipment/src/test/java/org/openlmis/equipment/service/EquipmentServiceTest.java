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

package org.openlmis.equipment.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Pagination;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.ColdChainEquipment;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.repository.ColdChainEquipmentRepository;
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

  @Mock
  private ColdChainEquipmentRepository coldChainEquipmentRepository;

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
  public void shouldGetAllCCE() throws Exception {
    Pagination page=new Pagination();
    List<ColdChainEquipment> expectedEquipments = new ArrayList<ColdChainEquipment>();
    expectedEquipments.add(new ColdChainEquipment());
    when(coldChainEquipmentRepository.getAll(1L,page)).thenReturn(expectedEquipments);

    List<ColdChainEquipment> equipments = service.getAllCCE(1L, page);
    verify(coldChainEquipmentRepository).getAll(1L, page);

    assertEquals(equipments, expectedEquipments);
  }

  @Test
  public void shouldGetById() throws Exception {
    Equipment equipment = new Equipment();
    equipment.setName("123");
    when(repository.getById(1L)).thenReturn(equipment);

    Equipment result = service.getById(1L);
    assertEquals(result.getName(), equipment.getName());
  }

  @Test
  public void shouldGetTypesByProgram() throws Exception {
    Equipment equipment = new Equipment();
    equipment.setName("123");
    when(repository.getById(1L)).thenReturn(equipment);

    Equipment result = service.getById(1L);
    assertEquals(result.getName(), equipment.getName());
  }

  @Test
  public void shouldSaveNewEquipment() throws Exception {
    Equipment equipment = new Equipment();
    equipment.setName("123");

    service.saveEquipment(equipment);
    verify(repository).insert(equipment);
    verify(repository, never()).update(equipment);
  }

  @Test
  public void shouldSaveChangesInExistingEquipment() throws Exception {
    Equipment equipment = new Equipment();
    equipment.setId(1L);
    equipment.setName("123");

    service.updateEquipment(equipment);
    verify(repository, never()).insert(equipment);
    verify(repository).update(equipment);
  }
}