package org.openlmis.equipment.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.ServiceType;
import org.openlmis.equipment.repository.ServiceTypeRepository;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ServiceTypeServiceTest {

  @Mock
  ServiceTypeRepository repository;

  @InjectMocks
  ServiceTypeService service;

  @Test
  public void shouldGetAll() throws Exception {
    service.getAll();
    verify(repository).getAll();
  }

  @Test
  public void shouldGetById() throws Exception {
    service.getById(2L);
    verify(repository).getById(2L);
  }

  @Test
  public void shouldSaveNewServiceType() throws Exception {
    ServiceType type = new ServiceType();
    service.save(type);
    verify(repository).insert(type);
    verify(repository, never()).update(any(ServiceType.class));
  }

  @Test
  public void shouldSaveUpdate() throws Exception {
    ServiceType type = new ServiceType();
    type.setId(23L);
    service.save(type);
    verify(repository, never()).insert(type);
    verify(repository).update(type);
  }
}