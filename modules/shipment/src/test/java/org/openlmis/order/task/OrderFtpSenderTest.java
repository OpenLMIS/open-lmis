/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.order.task;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.io.File;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(OrderFtpSender.class)
public class OrderFtpSenderTest {

  @Test
  public void shouldSendFileToFtpLocation() throws Exception {
    DefaultCamelContext camelContext = mock(DefaultCamelContext.class);
    ProducerTemplate producerTemplate = mock(ProducerTemplate.class);
    whenNew(DefaultCamelContext.class).withNoArguments().thenReturn(camelContext);
    when(camelContext.createProducerTemplate()).thenReturn(producerTemplate);
    OrderFtpSender orderFtpSender = new OrderFtpSender();
    FacilityFtpDetails facilityFtpDetails = getFacilityFtpDetails();
    File file = mock(File.class);
    String fileName = "dummyfile";
    when(file.getName()).thenReturn(fileName);

    orderFtpSender.sendFile(facilityFtpDetails, file);

    verify(producerTemplate).sendBodyAndHeader("ftp://username@serverhost:serverport/localfolderpath?password=password&passiveMode=true", file, Exchange.FILE_NAME, fileName);
  }

  private FacilityFtpDetails getFacilityFtpDetails() {
    FacilityFtpDetails facilityFtpDetails = new FacilityFtpDetails();
    facilityFtpDetails.setUserName("username");
    facilityFtpDetails.setServerHost("serverhost");
    facilityFtpDetails.setServerPort("serverport");
    facilityFtpDetails.setLocalFolderPath("/localfolderpath");
    facilityFtpDetails.setPassword("password");
    return facilityFtpDetails;
  }


}
