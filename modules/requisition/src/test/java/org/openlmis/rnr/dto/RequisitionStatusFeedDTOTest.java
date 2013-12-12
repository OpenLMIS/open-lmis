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

import java.text.SimpleDateFormat;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRequisition;
import static org.openlmis.rnr.builder.RequisitionBuilder.period;

@Category(UnitTests.class)
public class RequisitionStatusFeedDTOTest {
  @Test
  public void shouldPopulateFeedFromRequisition() throws Exception {
    Rnr rnr = make(a(defaultRequisition));
    long startDate = rnr.getPeriod().getStartDate().getTime();
    long endDate = rnr.getPeriod().getEndDate().getTime();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    String stringStartDate = dateFormat.format(startDate);
    String stringEndDate = dateFormat.format(endDate);

    RequisitionStatusFeedDTO feed = new RequisitionStatusFeedDTO(rnr);

    assertThat(feed.getRequisitionId(), is(rnr.getId()));
    assertThat(feed.getRequisitionStatus(), is(rnr.getStatus()));
    assertThat(feed.isEmergency(), is(rnr.isEmergency()));
    assertThat(feed.getStartDate(), is(startDate));
    assertThat(feed.getEndDate(), is(endDate));
    String serializedContent = format("{\"requisitionId\":%d,\"requisitionStatus\":\"%s\",\"emergency\":%s,\"startDate\":%d,\"endDate\":%d,\"stringStartDate\":\"%s\",\"stringEndDate\":\"%s\"}",
      rnr.getId(), rnr.getStatus(), rnr.isEmergency(), startDate, endDate, stringStartDate, stringEndDate);
    assertThat(feed.getSerializedContents(), is(serializedContent));
  }

  @Test
  public void shouldNotSetStartDateAndEndDateIfPeriodIsNull() throws Exception {
    Rnr rnr = make(a(defaultRequisition, with(period, (ProcessingPeriod) null)));

    RequisitionStatusFeedDTO feed = new RequisitionStatusFeedDTO(rnr);

    assertThat(feed.getRequisitionId(), is(rnr.getId()));
    assertThat(feed.getRequisitionStatus(), is(rnr.getStatus()));
    assertThat(feed.isEmergency(), is(rnr.isEmergency()));
    assertThat(feed.getStartDate(), is(nullValue()));
    assertThat(feed.getEndDate(), is(nullValue()));
  }

}
