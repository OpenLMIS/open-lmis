package org.openlmis.equipment.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.ProgramEquipmentType;
import org.openlmis.equipment.repository.mapper.EquipmentTypeProgramMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProgramEquipmentTypeRepositoryTest {

  @Mock
  EquipmentTypeProgramMapper mapper;

  @InjectMocks
  ProgramEquipmentRepository repository;

  @Test
  public void shouldGetByProgramId() throws Exception {
    repository.getByProgramId(1L);
    verify(mapper).getByProgramId(1L);
  }

  @Test
  public void shouldInsert() throws Exception {
    ProgramEquipmentType programEquipmentType = new ProgramEquipmentType();
    programEquipmentType.setDisplayOrder(29);
    repository.insert(programEquipmentType);
    verify(mapper).insert(programEquipmentType);
  }

  @Test
  public void shouldUpdate() throws Exception {
    ProgramEquipmentType programEquipmentType = new ProgramEquipmentType();
    programEquipmentType.setDisplayOrder(29);
    repository.update(programEquipmentType);
    verify(mapper).update(programEquipmentType);
  }

  @Test
  public void shouldRemove() throws Exception {
    repository.remove(1L);
    verify(mapper).remove(1L);
  }
}