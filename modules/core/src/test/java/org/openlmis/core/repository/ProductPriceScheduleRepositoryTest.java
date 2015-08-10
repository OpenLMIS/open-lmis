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

package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ProductPriceSchedule;
import org.openlmis.core.repository.mapper.ProductPriceScheduleMapper;
import org.openlmis.db.categories.UnitTests;

import static org.mockito.Mockito.verify;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductPriceScheduleRepositoryTest {

  @Mock
  ProductPriceScheduleMapper mapper;

  @InjectMocks
  ProductPriceScheduleRepository repository;


  @Test
  public void shouldInsert() throws Exception {
    ProductPriceSchedule pps = new ProductPriceSchedule();
    repository.insert(pps);
    verify(mapper).insert(pps);
  }

  @Test
  public void shouldUpdate() throws Exception {
    ProductPriceSchedule pps = new ProductPriceSchedule();
    repository.update(pps);
    verify(mapper).update(pps);
  }


  @Test
  public void shouldGetByProductId() throws Exception {
    repository.getByProductId(1L);
    verify(mapper).getByProductId(1L);
  }

  @Test
  public void shouldGetPriceScheduleFullSupplyFacilityApprovedProduct() throws Exception {
    repository.getPriceScheduleFullSupplyFacilityApprovedProduct(1L, 2L);
    verify(mapper).getPriceScheduleFullSupplyFacilityApprovedProduct(1L, 2L);
  }
}