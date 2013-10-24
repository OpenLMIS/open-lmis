/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.event;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.dto.RnrFeedDTO;
import org.openlmis.rnr.service.NotificationServices;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;
import static org.openlmis.rnr.event.RequisitionStatusChangeEvent.FEED_CATEGORY;
import static org.openlmis.rnr.event.RequisitionStatusChangeEvent.FEED_TITLE;
import static org.powermock.api.mockito.PowerMockito.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({DateTime.class, RequisitionStatusChangeEvent.class})
public class RequisitionStatusChangeEventTest {

  @Mock
  NotificationServices nServices;

  @Test
  public void shouldCreateEventFromRequisition() throws Exception {
    mockStatic(DateTime.class);
    mockStatic(RnrFeedDTO.class);

    Rnr rnr = make(a(defaultRnr));

    DateTime date = DateTime.now();
    when(DateTime.now()).thenReturn(date);

    RnrFeedDTO feedDTO = mock(RnrFeedDTO.class);
    whenNew(RnrFeedDTO.class).withArguments(rnr).thenReturn(feedDTO);
    when(feedDTO.getSerializedContents()).thenReturn("serializedContents");

    RequisitionStatusChangeEvent event = new RequisitionStatusChangeEvent(rnr, nServices);

    assertThat(event.getTitle(), is(FEED_TITLE));
    assertThat(event.getTimeStamp(), is(date));
    assertThat(event.getUuid(), is(notNullValue()));
    assertThat(event.getContents(), is("serializedContents"));
    assertThat(event.getCategory(), is(FEED_CATEGORY));
    verify(feedDTO).getSerializedContents();
  }
}
