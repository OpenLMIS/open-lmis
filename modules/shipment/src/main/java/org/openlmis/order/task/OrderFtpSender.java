/*
* Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
*
* If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

package org.openlmis.order.task;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.MessageFormat;

@Service
public class OrderFtpSender {
  private static final String CAMEL_FTP_PATTERN = "ftp://{0}@{1}:{2}{3}?password={4}&passiveMode=true";

  public void sendFile(FacilityFtpDetails supplyingFacility, File file) {
    CamelContext context = new DefaultCamelContext();
    ProducerTemplate template = context.createProducerTemplate();
    template.sendBodyAndHeader(createFtpUri(supplyingFacility), file, Exchange.FILE_NAME, file.getName());
  }

  private String createFtpUri(FacilityFtpDetails supplyingFacility) {
    return MessageFormat.format(CAMEL_FTP_PATTERN,
      supplyingFacility.getUserName(),
      supplyingFacility.getServerHost(),
      supplyingFacility.getServerPort(),
      supplyingFacility.getLocalFolderPath(),
      supplyingFacility.getPassword());
  }
}
