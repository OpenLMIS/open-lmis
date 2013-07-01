/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.exception;

import lombok.Getter;
import org.openlmis.core.message.OpenLmisMessage;


public class DataException extends RuntimeException {

  @Getter
  private OpenLmisMessage openLmisMessage;


  public DataException(String code) {
    openLmisMessage = new OpenLmisMessage(code);
  }

  public DataException(OpenLmisMessage openLmisMessage) {
    this.openLmisMessage = openLmisMessage;
  }

  @Override
  public String toString() {
    return openLmisMessage.toString();
  }

  @Deprecated
  @Override
  public String getMessage() {
    return openLmisMessage.toString();
  }
}
