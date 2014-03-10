/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.order.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;

import java.sql.ResultSet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class OrderRowMapperTest {

  @InjectMocks
  OrderRowMapper orderRowMapper;

  @Mock
  private ResultSet resultSet;

  @Test
  public void shouldCreateOrderFromResultSet() throws Exception {
    when(resultSet.getString("id")).thenReturn("5");
    when(resultSet.getString("supplyLineId")).thenReturn("55");

    Order order = orderRowMapper.mapRow(resultSet, 1);

    verify(resultSet).getString("id");
    assertThat(order.getId(), is(5L));
    assertThat(order.getRnr().getId(), is(5L));
    assertThat(order.getSupplyLine().getId(), is(55L));
  }
}
