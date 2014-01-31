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
import org.openlmis.db.categories.UnitTests;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.pod.domain.OrderPODLineItem;
import org.openlmis.pod.repository.mapper.PODMapper;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class PODRepositoryTest {

  @Mock
  PODMapper mapper;

  @InjectMocks
  PODRepository repository;

  @Test
  public void shouldInsertPODLineItem() {
    OrderPODLineItem orderPodLineItem = new OrderPODLineItem();
    repository.insertPODLineItem(orderPodLineItem);
    verify(mapper).insertPODLineItem(orderPodLineItem);
  }

  @Test
  public void shouldInsertPOD() {
    OrderPOD orderPod = new OrderPOD();
    repository.insertPOD(orderPod);
    verify(mapper).insertPOD(orderPod);
  }

  @Test
  public void shouldGetPODByOrderId() {
    Long orderId = 1l;
    OrderPOD expectedOrderPOD = new OrderPOD();
    when(mapper.getPODByOrderId(orderId)).thenReturn(expectedOrderPOD);
    OrderPOD orderPod = repository.getPODByOrderId(orderId);
    verify(mapper).getPODByOrderId(orderId);
    assertThat(orderPod, is(expectedOrderPOD));
  }

  @Test
  public void shouldGetPODWithLineItemsByOrderId() throws Exception {
    Long podId = 2L;
    OrderPOD expectedOrderPOD = new OrderPOD();
    expectedOrderPOD.setId(podId);
    OrderPODLineItem lineItem = new OrderPODLineItem();
    lineItem.setPodId(podId);

    when(mapper.getPODById(podId)).thenReturn(expectedOrderPOD);
    expectedOrderPOD.setPodLineItems(asList(lineItem));

    OrderPOD orderPod = repository.getPOD(podId);

    verify(mapper).getPODById(podId);
    assertThat(orderPod, is(expectedOrderPOD));
  }

  @Test
  public void shouldInsertPODWithLineItems() throws Exception {
    OrderPOD orderPOD = new OrderPOD();
    Long podId = 12345L;
    orderPOD.setId(podId);
    OrderPODLineItem lineItem = new OrderPODLineItem();
    OrderPODLineItem lineItem2 = new OrderPODLineItem();
    lineItem2.setDispensingUnit("unit");
    orderPOD.setPodLineItems(asList(lineItem, lineItem2));
    lineItem.setPodId(podId);

    repository.insert(orderPOD);

    verify(mapper).insertPOD(orderPOD);
    verify(mapper).insertPODLineItem(lineItem);
    verify(mapper).insertPODLineItem(lineItem2);
    assertThat(lineItem.getPodId(), is(podId));
    assertThat(lineItem2.getPodId(), is(podId));
  }

  @Test
  public void shouldUpdatePOD() {
    OrderPOD orderPOD = new OrderPOD();
    OrderPODLineItem lineItem1 = new OrderPODLineItem(1L, "P1", null);
    OrderPODLineItem lineItem2 = new OrderPODLineItem(2L, "P2", 30);
    orderPOD.setPodLineItems(asList(lineItem1, lineItem2));

    OrderPOD updatedPOD = repository.update(orderPOD);

    assertThat(updatedPOD, is(orderPOD));
    verify(mapper).update(orderPOD);
    verify(mapper).updateLineItem(lineItem1);
    verify(mapper).updateLineItem(lineItem2);
  }
}
