/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.event;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Vendor;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.dto.RnrFeedDTO;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DateTime.class, RnrFeedDTO.class})
public class RequisitionStatusChangeEventTest {


  @Test
  public void shouldCreateEventFromRequisition() throws Exception {
    mockStatic(DateTime.class);
    mockStatic(RnrFeedDTO.class);

    Rnr rnr = make(a(defaultRnr));
    Vendor vendor = new Vendor();

    DateTime date = DateTime.now();
    when(DateTime.now()).thenReturn(date);

    RnrFeedDTO feedDTO = mock(RnrFeedDTO.class);
    when(RnrFeedDTO.populate(rnr, vendor)).thenReturn(feedDTO);
    when(feedDTO.getSerializedContents()).thenReturn("serializedContents");

    RequisitionStatusChangeEvent event = new RequisitionStatusChangeEvent(rnr, vendor);

    assertThat(event.getTitle(), is("Requisition"));
    assertThat(event.getTimeStamp(), is(date));
    assertThat(event.getUuid(), is(notNullValue()));
    assertThat(event.getContents(), is("serializedContents"));
    verify(feedDTO).getSerializedContents();
  }
}
