/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.pod.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.pod.domain.OrderPODLineItem;
import org.openlmis.pod.repository.mapper.PODMapper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(IntegrationTests.class)
@RunWith(MockitoJUnitRunner.class)
public class PODRepositoryTest {

  @Mock
  PODMapper podMapper;

  @InjectMocks
  PODRepository podRepository;

  @Test
  public void shouldInsertPODLineItem() {

    OrderPODLineItem orderPodLineItem = new OrderPODLineItem();
    podRepository.insertPODLineItem(orderPodLineItem);
    verify(podMapper).insertPODLineItem(orderPodLineItem);
  }

  @Test
  public void shouldInsertPOD() {
    OrderPOD orderPod = new OrderPOD();
    podRepository.insertPOD(orderPod);
    verify(podMapper).insertPOD(orderPod);
  }

  @Test
  public void shouldGetPODByOrderId() {
    Long orderId = 1l;
    OrderPOD expectedOrderPOD = new OrderPOD();
    when(podMapper.getPODByOrderId(orderId)).thenReturn(expectedOrderPOD);
    OrderPOD orderPod = podRepository.getPODByOrderId(orderId);
    verify(podMapper).getPODByOrderId(orderId);
    assertThat(orderPod, is(expectedOrderPOD));
  }
}
