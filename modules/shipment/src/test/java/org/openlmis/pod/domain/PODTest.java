/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */


package org.openlmis.pod.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.rnr.domain.Rnr;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Category(IntegrationTests.class)
public class PODTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldThrowErrorIfOrderIdIsBlank() {
    OrderPOD orderPod = new OrderPOD(1l);
    orderPod.setOrderId(null);
    List<OrderPODLineItem> orderPodLineItems = asList(new OrderPODLineItem(1l, "P100", 100));
    orderPod.setPodLineItems(orderPodLineItems);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    orderPod.validate();
  }

  @Test
  public void shouldFillPODWithFacilityProgramAndPeriodFromRequisition() throws Exception {
    Rnr rnr = new Rnr(new Facility(2L), new Program(3L), new ProcessingPeriod(4L));
    OrderPOD orderPod = new OrderPOD();

    orderPod.fillPOD(rnr);

    assertThat(orderPod.getFacilityId(), is(2L));
    assertThat(orderPod.getProgramId(), is(3L));
    assertThat(orderPod.getPeriodId(), is(4L));
  }

  @Test
  public void shouldThrowErrorIfLineItemsNotPresent() {
    OrderPOD orderPod = new OrderPOD(1l);
    orderPod.setOrderId(2l);
    orderPod.setPodLineItems(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    orderPod.validate();
  }

  @Test
  public void shouldThrowErrorIfLineItemsSizeIsZero() {
    OrderPOD orderPod = new OrderPOD(1l);
    orderPod.setOrderId(2l);
    List<OrderPODLineItem> orderPodLineItems = new ArrayList<>();
    orderPod.setPodLineItems(orderPodLineItems);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.mandatory.fields.missing");

    orderPod.validate();
  }

  @Test
  public void shouldValidateLineItemsForPOD() {
    OrderPOD orderPod = new OrderPOD(1l);
    orderPod.setOrderId(2l);
    OrderPODLineItem orderPodLineItem = mock(OrderPODLineItem.class);
    List<OrderPODLineItem> orderPodLineItems = asList(orderPodLineItem);
    orderPod.setPodLineItems(orderPodLineItems);

    orderPod.validate();

    verify(orderPodLineItem).validate();
  }
}
