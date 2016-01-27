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
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.dto.RequisitionStatusFeedDTO;
import org.openlmis.rnr.service.NotificationServices;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRequisition;
import static org.openlmis.rnr.event.RequisitionStatusChangeEvent.FEED_TITLE;
import static org.powermock.api.mockito.PowerMockito.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({DateTime.class, RequisitionStatusChangeEvent.class})
public class RequisitionStatusChangeEventTest {

  @Mock
  NotificationServices nServices;

  @Test
  public void shouldCreateEventFromRequisition() throws Exception {
    mockStatic(DateTime.class);

    Rnr rnr = make(a(defaultRequisition));

    DateTime date = DateTime.now();
    when(DateTime.now()).thenReturn(date);

    RequisitionStatusFeedDTO feedDTO = mock(RequisitionStatusFeedDTO.class);
    whenNew(RequisitionStatusFeedDTO.class).withArguments(rnr).thenReturn(feedDTO);
    when(feedDTO.getSerializedContents()).thenReturn("serializedContents");

    RequisitionStatusChangeEvent event = new RequisitionStatusChangeEvent(rnr, nServices);

    assertThat(event.getTitle(), is(FEED_TITLE));
    assertThat(event.getTimeStamp(), is(date));
    assertThat(event.getUuid(), is(notNullValue()));
    assertThat(event.getContents(), is("serializedContents"));
    assertThat(event.getCategory(), is("requisition-status"));
    verify(feedDTO).getSerializedContents();
  }
}
