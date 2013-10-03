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

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.core.domain.Vendor;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.Rnr;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;
@Category(UnitTests.class)
public class RnrFeedDTOTest {
  @Test
  public void shouldPopulateFeedFromRequisition() throws Exception {
    Rnr rnr = make(a(defaultRnr));

    Vendor vendor = new Vendor();
    vendor.setName("external system");
    RnrFeedDTO feed = RnrFeedDTO.populate(rnr, vendor);

    assertThat(feed.getRequisitionId(), is(rnr.getId()));
    assertThat(feed.getFacilityId(), is(rnr.getFacility().getId()));
    assertThat(feed.getProgramId(), is(rnr.getProgram().getId()));
    assertThat(feed.getPeriodId(), is(rnr.getPeriod().getId()));
    assertThat(feed.getRequisitionStatus(), is(rnr.getStatus()));
    assertThat(feed.getExternalSystem(), is(vendor.getName()));
  }

  @Test
  public void shouldGetSerializedContentsFromRequisition() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    Rnr requisition = make(a(defaultRnr));
    Vendor vendor = new Vendor();
    RnrFeedDTO feedDTO = RnrFeedDTO.populate(requisition, vendor);

    String serializedContents = feedDTO.getSerializedContents();

    assertThat(serializedContents, is(mapper.writeValueAsString(feedDTO)));
  }
}
