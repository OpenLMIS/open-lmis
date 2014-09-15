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

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.MessageFormat;

/**
 * Exposes the services for creating FTP URI and sending file through FTP.
 */

// todo: make the passive mode configurable.
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
