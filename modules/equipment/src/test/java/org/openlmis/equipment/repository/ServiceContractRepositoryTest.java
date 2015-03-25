package org.openlmis.equipment.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.ServiceContract;
import org.openlmis.equipment.repository.mapper.ServiceContractMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ServiceContractRepositoryTest {

  @Mock
  ServiceContractMapper mapper;

  @InjectMocks
  ServiceContractRepository repository;

  @Test
  public void shouldGetById() throws Exception {
    when(mapper.getById(1L)).thenReturn(new ServiceContract());

    repository.getById(1L);

    verify(mapper).getById(1L);
  }

  @Test
  public void shouldGetAllForFacility() throws Exception {
    repository.getAllForFacility(3L);

    verify(mapper).getAllForFacility(3L);
  }

  @Test
  public void shouldGetAllForVendor() throws Exception {
    repository.getAllForVendor(3L);

    verify(mapper).getAllForVendor(3L);
  }

  @Test
  public void shouldGetAllForEquipment() throws Exception {
    repository.getAllForEquipment(5L);

    verify(mapper).getAllForEquipment(5L);
  }

  @Test
  public void shouldGetAll() throws Exception {
    repository.getAll();
    verify(mapper).getAll();
  }

  @Test
  public void shouldInsert() throws Exception {
    ServiceContract contract = new ServiceContract();
    repository.insert(contract);

    verify(mapper).insert(contract);
  }

  @Test
  public void shouldUpdate() throws Exception {
    ServiceContract contract = new ServiceContract();
    repository.update(contract);

    verify(mapper).update(contract);
  }
}