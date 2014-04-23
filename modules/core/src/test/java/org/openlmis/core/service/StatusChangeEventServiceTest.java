/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.core.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.User;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.email.service.EmailService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mail.SimpleMailMessage;

import java.util.ArrayList;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(StatusChangeEventService.class)
public class StatusChangeEventServiceTest {
  @Mock
  String baseUrl = "http://localhost:9091";

  @Mock
  private EmailService emailService;

  @Mock
  private MessageService messageService;

  @InjectMocks
  private StatusChangeEventService service;

  @Test
  public void shouldNotifyUserAboutRnrStatusChange() throws Exception {
    Long rnrId = 1L;
    String status = "SUBMITTED";
    Facility facility = make(a(defaultFacility));
    Program program = make(a(defaultProgram));
    ProcessingPeriod period = make(a(defaultProcessingPeriod));
    User user = new User();
    ArrayList<User> userList = new ArrayList<>();
    userList.add(user);
    SimpleMailMessage emailMessage = new SimpleMailMessage();
    String actionUrl = baseUrl + "public/pages/logistics/rnr/index.html#/create-rnr/{0}/{2}/{1}?supplyType=fullSupply&page=1";

    when(messageService.message("msg.email.notification.subject")).thenReturn("subject");
    when(messageService.message(actionUrl, rnrId, program.getId(), facility.getId())).thenReturn("actionUrl");
    when(messageService.message("msg.email.notification.body", null, "\n\n", facility.getName(),
      period.getName(), null, "\n\n", "actionUrl", "\n\n")).thenReturn("body");

    emailMessage.setTo(user.getEmail());
    emailMessage.setSubject("subject");
    emailMessage.setText("body");

    service.notifyUsers(userList, rnrId, facility, program, period, status);

    verify(emailService).processEmailsAsync(asList(emailMessage));
  }
}
