/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
