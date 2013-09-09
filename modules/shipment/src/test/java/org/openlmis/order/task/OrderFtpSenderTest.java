package org.openlmis.order.task;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
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
