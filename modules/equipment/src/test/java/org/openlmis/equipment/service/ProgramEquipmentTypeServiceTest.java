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

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.ProgramEquipmentType;
import org.openlmis.equipment.repository.ProgramEquipmentTypeRepository;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProgramEquipmentTypeServiceTest {

  @Mock
  ProgramEquipmentTypeRepository repository;

  @InjectMocks
  ProgramEquipmentTypeService service;

  @Test
  public void shouldGetByProgramId() throws Exception {
    service.getByProgramId(4L);
    verify(repository).getByProgramId(4L);
  }

  @Test
  public void shouldSaveNew() throws Exception {
    ProgramEquipmentType programEquipmentType = new ProgramEquipmentType();
    service.Save(programEquipmentType);
    verify(repository).insert(programEquipmentType);
    verify(repository, never()).update(programEquipmentType);
  }

  @Test
  public void shouldSaveUpdate() throws Exception {
    ProgramEquipmentType programEquipmentType = new ProgramEquipmentType();
    programEquipmentType.setId(4L);
    service.Save(programEquipmentType);
    verify(repository, never()).insert(programEquipmentType);
    verify(repository).update(programEquipmentType);
  }

  @Test
  public void shouldRemove() throws Exception {
    service.remove(4L);
    verify(repository).remove(4L);
  }
}