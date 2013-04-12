/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.MessageEndpoint;

@MessageEndpoint
public class ShipmentFileProcessor {

  private static Logger logger = LoggerFactory.getLogger(ShipmentFileProcessor.class);

  public void process(Message message){
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    logger.info("File processed"+ message.toString()+ Thread.currentThread());
  }
}
