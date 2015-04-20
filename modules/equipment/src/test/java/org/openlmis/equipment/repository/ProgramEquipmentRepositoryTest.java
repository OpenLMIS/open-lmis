package org.openlmis.equipment.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.ProgramEquipment;
import org.openlmis.equipment.repository.mapper.EquipmentProgramMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProgramEquipmentRepositoryTest {

  @Mock
  EquipmentProgramMapper mapper;

  @InjectMocks
  ProgramEquipmentRepository repository;

  @Test
  public void shouldGetByProgramId() throws Exception {
    repository.getByProgramId(1L);
    verify(mapper).getByProgramId(1L);
  }

  @Test
  public void shouldInsert() throws Exception {
    ProgramEquipment programEquipment = new ProgramEquipment();
    programEquipment.setDisplayOrder(29);
    repository.insert(programEquipment);
    verify(mapper).insert(programEquipment);
  }

  @Test
  public void shouldUpdate() throws Exception {
    ProgramEquipment programEquipment = new ProgramEquipment();
    programEquipment.setDisplayOrder(29);
    repository.update(programEquipment);
    verify(mapper).update(programEquipment);
  }

  @Test
  public void shouldRemove() throws Exception {
    repository.remove(1L);
    verify(mapper).remove(1L);
  }
}