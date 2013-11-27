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
    POD pod = new POD(1l);
    pod.setOrderId(null);
    List<PODLineItem> podLineItems = asList(new PODLineItem(1l, "P100", 100));
    pod.setPodLineItems(podLineItems);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.restapi.mandatory.missing");

    pod.validate();
  }

  @Test
  public void shouldFillPODWithFacilityProgramAndPeriodFromRequisition() throws Exception {
    Rnr rnr = new Rnr(new Facility(2L), new Program(3L), new ProcessingPeriod(4L));
    POD pod = new POD();

    pod.fillPOD(rnr);

    assertThat(pod.getFacilityId(), is(2L));
    assertThat(pod.getProgramId(), is(3L));
    assertThat(pod.getPeriodId(), is(4L));
  }

  @Test
  public void shouldThrowErrorIfLineItemsNotPresent() {
    POD pod = new POD(1l);
    pod.setOrderId(2l);
    pod.setPodLineItems(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.restapi.mandatory.missing");

    pod.validate();
  }

  @Test
  public void shouldThrowErrorIfLineItemsSizeIsZero() {
    POD pod = new POD(1l);
    pod.setOrderId(2l);
    List<PODLineItem> podLineItems = new ArrayList<>();
    pod.setPodLineItems(podLineItems);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.restapi.mandatory.missing");

    pod.validate();
  }

  @Test
  public void shouldValidateLineItemsForPOD() {
    POD pod = new POD(1l);
    pod.setOrderId(2l);
    PODLineItem podLineItem = mock(PODLineItem.class);
    List<PODLineItem> podLineItems = asList(podLineItem);
    pod.setPodLineItems(podLineItems);

    pod.validate();

    verify(podLineItem).validate();
  }
}
