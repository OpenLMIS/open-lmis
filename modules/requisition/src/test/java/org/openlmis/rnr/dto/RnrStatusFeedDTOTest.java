/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.dto;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.Rnr;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;
import static org.openlmis.rnr.builder.RequisitionBuilder.period;

@Category(UnitTests.class)
public class RnrStatusFeedDTOTest {
  @Test
  public void shouldPopulateFeedFromRequisition() throws Exception {
    Rnr rnr = make(a(defaultRnr));

    RnrStatusFeedDTO feed = new RnrStatusFeedDTO(rnr);

    assertThat(feed.getRequisitionId(), is(rnr.getId()));
    assertThat(feed.getRequisitionStatus(), is(rnr.getStatus()));
    assertThat(feed.isEmergency(), is(rnr.isEmergency()));
    assertThat(feed.getStartDate(), is(rnr.getPeriod().getStartDate().getTime()));
    assertThat(feed.getEndDate(), is(rnr.getPeriod().getEndDate().getTime()));
    assertThat(feed.getSerializedContents(), is("{\"requisitionId\":1,\"requisitionStatus\":\"INITIATED\",\"emergency\":false,\"startDate\":1325356200000,\"endDate\":1328034600000}"));
  }

  @Test
  public void shouldNotSetStartDateAndEndDateIfPeriodIsNull() throws Exception {
    ProcessingPeriod processingPeriod = null;
    Rnr rnr = make(a(defaultRnr, with(period, processingPeriod)));
    rnr.setPeriod(null);

    RnrStatusFeedDTO feed = new RnrStatusFeedDTO(rnr);

    assertThat(feed.getRequisitionId(), is(rnr.getId()));
    assertThat(feed.getRequisitionStatus(), is(rnr.getStatus()));
    assertThat(feed.isEmergency(), is(rnr.isEmergency()));
    assertThat(feed.getStartDate(), is(nullValue()));
    assertThat(feed.getEndDate(), is(nullValue()));
  }

}
