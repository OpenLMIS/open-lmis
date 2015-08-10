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