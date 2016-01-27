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
import org.openlmis.equipment.domain.EquipmentTypeProduct;
import org.openlmis.equipment.repository.mapper.EquipmentTypeProductMapper;

import static org.mockito.Mockito.verify;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class EquipmentTypeProductRepositoryTest {

  @Mock
  EquipmentTypeProductMapper mapper;

  @InjectMocks
  EquipmentTypeProductRepository repository;

  @Test
  public void shouldGetByProgramEquipmentId() throws Exception {
    repository.getByProgramEquipmentId(1L);

    verify(mapper).getByProgramEquipmentId(1L);
  }

  @Test
  public void shouldInsert() throws Exception {
    EquipmentTypeProduct product = new EquipmentTypeProduct();

    repository.insert(product);

    verify(mapper).insert(product);
  }

  @Test
  public void shouldUpdate() throws Exception {
    EquipmentTypeProduct product = new EquipmentTypeProduct();

    repository.update(product);

    verify(mapper).update(product);
  }

  @Test
  public void shouldRemove() throws Exception {
    repository.remove(5L);

    verify(mapper).remove(5L);
  }

  @Test
  public void shouldRemoveEquipmentProducts() throws Exception {
    repository.removeAllByEquipmentProducts(66L);
    verify(mapper).removeByEquipmentProducts(66L);
  }

  @Test
  public void shouldGetAvailableProductsToLink() throws Exception {
    repository.getAvailableProductsToLink(3L, 5L);
    verify(mapper).getAvailableProductsToLink(3L, 5L);
  }
}