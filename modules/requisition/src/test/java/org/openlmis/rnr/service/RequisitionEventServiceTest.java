/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.service;

import org.ict4h.atomfeed.server.service.EventService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Vendor;
import org.openlmis.core.service.VendorService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.event.RequisitionStatusChangeEvent;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.*;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;
import static org.powermock.api.mockito.PowerMockito.whenNew;
@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(RequisitionEventService.class)
public class RequisitionEventServiceTest {

  @Mock
  EventService eventService;

  @Mock
  VendorService vendorService;

  @InjectMocks
  RequisitionEventService service;

  @Mock
  NotificationServices notificationServices;

  @Test
  public void shouldFetchVendorAndTriggerNotifyOnEventService() throws Exception {
    Rnr requisition = make(a(defaultRnr));
    Vendor vendor = new Vendor();
    when(vendorService.getByUserId(requisition.getModifiedBy())).thenReturn(vendor);
    RequisitionStatusChangeEvent event = mock(RequisitionStatusChangeEvent.class);

    whenNew(RequisitionStatusChangeEvent.class).withArguments(requisition, vendor, notificationServices).thenReturn(event);

    service.notifyForStatusChange(requisition);

    verify(vendorService).getByUserId(requisition.getModifiedBy());
    verify(eventService).notify(event);
  }
}
