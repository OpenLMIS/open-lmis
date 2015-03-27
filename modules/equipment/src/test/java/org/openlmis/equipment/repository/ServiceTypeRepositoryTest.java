package org.openlmis.equipment.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.ServiceType;
import org.openlmis.equipment.repository.mapper.ServiceTypeMapper;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ServiceTypeRepositoryTest {

  @Mock
  ServiceTypeMapper mapper;

  @InjectMocks
  ServiceTypeRepository repository;

  @Test
  public void shouldGetAll() throws Exception{
    repository.getAll();
    verify(mapper).getAll();
  }

  @Test
  public void shouldGetById() throws Exception{
    ServiceType serviceType  = new ServiceType();
    serviceType.setName("the service type");
    when(mapper.getById(1L)).thenReturn(serviceType);

    ServiceType returnedValue = repository.getById(1L);
    assertThat(returnedValue, is(serviceType));
  }

  @Test
  public void shouldInsertServiceType(){
    ServiceType serviceType  = new ServiceType();
    serviceType.setName("the service type");

    repository.insert(serviceType);

    verify(mapper).insert(serviceType);
  }

  @Test
  public void shouldUpdateServiceType(){
    ServiceType serviceType  = new ServiceType();
    serviceType.setName("the service type");

    repository.update(serviceType);

    verify(mapper).update(serviceType);
  }

}