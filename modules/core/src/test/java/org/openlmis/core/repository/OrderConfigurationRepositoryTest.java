/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.OrderConfiguration;
import org.openlmis.core.domain.OrderNumberConfiguration;
import org.openlmis.core.repository.mapper.OrderConfigurationMapper;
import org.openlmis.core.repository.mapper.OrderNumberConfigurationMapper;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class OrderConfigurationRepositoryTest {
  @InjectMocks
  private OrderConfigurationRepository orderConfigurationRepository;

  @Mock
  private OrderConfigurationMapper orderConfigurationMapper;

  @Mock
  private OrderNumberConfigurationMapper orderNumberConfigurationMapper;

  @Test
  public void shouldGetConfiguration() {
    OrderConfiguration orderConfiguration = new OrderConfiguration();
    orderConfiguration.setHeaderInFile(true);
    when(orderConfigurationMapper.get()).thenReturn(orderConfiguration);
    assertThat(orderConfigurationRepository.getConfiguration(), is(orderConfiguration));
    verify(orderConfigurationMapper).get();
  }

  @Test
  public void shouldUpdateConfigurations() throws Exception {
    OrderConfiguration orderConfiguration = new OrderConfiguration();
    orderConfigurationRepository.update(orderConfiguration);
    verify(orderConfigurationMapper).update(orderConfiguration);
  }

  @Test
  public void shouldUpdateOrderNumberConfiguration() {
    OrderNumberConfiguration orderNumberConfiguration = new OrderNumberConfiguration();
    orderConfigurationRepository.updateOrderNumberConfiguration(orderNumberConfiguration);

    verify(orderNumberConfigurationMapper).delete();
    verify(orderNumberConfigurationMapper).insert(orderNumberConfiguration);
  }
}
