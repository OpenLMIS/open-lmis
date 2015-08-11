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

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.EquipmentTypeProduct;
import org.openlmis.equipment.repository.EquipmentTypeProductRepository;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProgramEquipmentTypeProductServiceTest {

  @Mock
  EquipmentTypeProductRepository repository;

  @InjectMocks
  ProgramEquipmentTypeProductService service;

  @Test
  public void shouldGetByProgramEquipmentId() throws Exception {
    service.getByProgramEquipmentId(3L);
    verify(repository).getByProgramEquipmentId(3L);
  }

  @Test
  public void shouldSaveNewRecords() throws Exception {
    EquipmentTypeProduct pp = new EquipmentTypeProduct();
    service.Save(pp);
    verify(repository).insert(pp);
    verify(repository, never()).update(pp);
  }

  @Test
  public void shouldUpdaetRecords() throws Exception {
    EquipmentTypeProduct pp = new EquipmentTypeProduct();
    pp.setId(3L);
    service.Save(pp);

    verify(repository, never()).insert(pp);
    verify(repository).update(pp);
  }


  @Test
  public void shouldRemove() throws Exception{
    service.remove(3L);
    verify(repository).remove(3L);
  }

  @Test
  public void shouldRemoveEquipmentProducts() throws Exception {
    service.removeAllByEquipmentProducts(5L);
    verify(repository).removeAllByEquipmentProducts(5L);
  }

  @Test
  public void shouldGetAvailableProductsToLink() throws Exception {
    service.getAvailableProductsToLink(4L, 6L);
    verify(repository).getAvailableProductsToLink(4L, 6L);
  }
}