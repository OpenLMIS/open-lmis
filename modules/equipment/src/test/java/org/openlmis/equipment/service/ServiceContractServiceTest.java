package org.openlmis.equipment.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.ServiceContract;
import org.openlmis.equipment.repository.ServiceContractRepository;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ServiceContractServiceTest {

  @Mock
  ServiceContractRepository repository;

  @InjectMocks
  ServiceContractService service;


  @Test
  public void shouldGetAll() throws Exception {
    service.getAll();
    verify(repository).getAll();
  }

  @Test
  public void shouldGetAllForFacility() throws Exception {
    service.getAllForFacility(2L);
    verify(repository).getAllForFacility(2L);
  }

  @Test
  public void shouldGetAllForVendor() throws Exception {
    service.getAllForVendor(4L);
    verify(repository).getAllForVendor(4L);
  }

  @Test
  public void shouldGetAllForEquipment() throws Exception {
    service.getAllForEquipment(5L);
    verify(repository).getAllForEquipment(5L);
  }

  @Test
  public void shouldGetById() throws Exception {
    service.getById(5L);
    verify(repository).getById(5L);
  }

  @Test
  public void shouldSaveNew() throws Exception {
    ServiceContract contract = new ServiceContract();
    service.save(contract);
    verify(repository).insert(contract);
    verify(repository, never()).update(any(ServiceContract.class));
  }


  @Test
  public void shouldSaveUpdate() throws Exception {
    ServiceContract contract = new ServiceContract();
    contract.setId(5L);
    service.save(contract);
    verify(repository, never()).insert(any(ServiceContract.class));
    verify(repository).update(contract);
  }
}