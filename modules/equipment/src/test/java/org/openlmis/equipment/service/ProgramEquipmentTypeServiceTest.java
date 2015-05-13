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

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.ProgramEquipmentType;
import org.openlmis.equipment.repository.ProgramEquipmentRepository;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProgramEquipmentTypeServiceTest {

  @Mock
  ProgramEquipmentRepository repository;

  @InjectMocks
  ProgramEquipmentService service;

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