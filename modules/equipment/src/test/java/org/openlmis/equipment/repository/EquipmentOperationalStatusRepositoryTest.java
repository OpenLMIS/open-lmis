package org.openlmis.equipment.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.repository.mapper.EquipmentOperationalStatusMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class EquipmentOperationalStatusRepositoryTest {

  @Mock
  EquipmentOperationalStatusMapper mapper;

  @InjectMocks
  EquipmentOperationalStatusRepository repository;

  @Test
  public void shouldGetAll() throws Exception {
    repository.getAll();
    verify(mapper).getAll();
  }
}