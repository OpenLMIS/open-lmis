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