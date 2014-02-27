/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.exception;

import lombok.Getter;
import org.openlmis.core.message.OpenLmisMessage;

/**
 * This is the base exception class for all application level exception. It provides ways to create custom exception
 * with externalised messages and parameters.
 */
public class DataException extends RuntimeException {

  @Getter
  private OpenLmisMessage openLmisMessage;

  public DataException(String code) {
    openLmisMessage = new OpenLmisMessage(code);
  }

  public DataException(String code, Object... params) {
    StringBuilder stringParams = new StringBuilder();
    for (Object param : params) {
      stringParams.append(param.toString()).append("#");
    }
    openLmisMessage = new OpenLmisMessage(code, stringParams.toString().split("#"));
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
