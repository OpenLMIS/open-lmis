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
import org.openlmis.core.domain.User;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.email.service.EmailService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
import org.openlmis.rnr.event.RequisitionStatusChangeEvent;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mail.SimpleMailMessage;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRequisition;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(RequisitionEventService.class)
public class RequisitionEventServiceTest {

  @Mock
  String baseUrl = "http://localhost:9091";

  @Mock
  private EventService eventService;

  @Mock
  private EmailService emailService;

  @Mock
  private MessageService messageService;

  @InjectMocks
  private RequisitionEventService service;

  @Test
  public void shouldTriggerNotifyOnEventService() throws Exception {
    Rnr requisition = make(a(defaultRequisition));
    RequisitionStatusChangeEvent event = mock(RequisitionStatusChangeEvent.class);
    whenNew(RequisitionStatusChangeEvent.class).withArguments(requisition).thenReturn(event);

    service.notifyForStatusChange(requisition);

    verify(eventService).notify(event);
  }

  @Test
  public void shouldNotifyUserAboutRnrStatusChange() throws Exception {
    Rnr requisition = make(a(defaultRequisition));
    requisition.setStatus(RnrStatus.SUBMITTED);
    User user = new User();
    List<User> userList = asList(user);
    SimpleMailMessage emailMessage = new SimpleMailMessage();
    String actionUrl = baseUrl + "/public/pages/rnr";

    when(messageService.message("msg.email.notification.subject")).thenReturn("subject");
    when(messageService.message("msg.email.notification.body", null, requisition.getFacility().getName(),
      requisition.getPeriod().getName(), null, actionUrl)).thenReturn("body");

    emailMessage.setTo(user.getEmail());
    emailMessage.setSubject("subject");
    emailMessage.setText("body");

    service.notifyUsers(requisition, userList);

    verify(emailService).send(emailMessage);
  }
}
