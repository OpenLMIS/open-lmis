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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.ServiceContract;
import org.openlmis.equipment.dto.ContractDetail;
import org.openlmis.equipment.repository.ServiceContractRepository;
import org.openlmis.equipment.repository.mapper.ServiceContractMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ServiceContractServiceTest {

  @Mock
  ServiceContractRepository repository;

  @Mock
  private ServiceContractMapper mapper;


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

    contract.setEquipments(new ArrayList<ContractDetail>());
    contract.setFacilities(new ArrayList<ContractDetail>());
    contract.setServiceTypes(new ArrayList<ContractDetail>());

    doNothing().when(mapper).insertEquipment(any(Long.class), any(Long.class));
    doNothing().when(mapper).insertFacilities(any(Long.class), any(Long.class));
    doNothing().when(mapper).insertServiceTypes(any(Long.class), any(Long.class));

    service.save(contract);
    verify(repository).insert(contract);
    verify(repository, never()).update(any(ServiceContract.class));
  }


  @Test
  public void shouldSaveUpdate() throws Exception {
    ServiceContract contract = new ServiceContract();
    contract.setEquipments(new ArrayList<ContractDetail>());
    contract.setFacilities(new ArrayList<ContractDetail>());
    contract.setServiceTypes(new ArrayList<ContractDetail>());
    doNothing().when(mapper).deleteEquipments(any(Long.class));
    doNothing().when(mapper).insertEquipment(any(Long.class), any(Long.class));
    doNothing().when(mapper).insertFacilities(any(Long.class), any(Long.class));
    doNothing().when(mapper).insertServiceTypes(any(Long.class), any(Long.class));
    contract.setId(5L);
    service.save(contract);
    verify(repository, never()).insert(any(ServiceContract.class));
    verify(repository).update(contract);
  }
}